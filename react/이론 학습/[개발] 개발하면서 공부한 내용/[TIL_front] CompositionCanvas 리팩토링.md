# [React] CompositionCanvas 리팩토링

> **Date:** 2026-02-05  
> **Tag:** #React #WebRTC #MediaPipe #Canvas #Refactoring #Frontend #RealTime #Streaming #Debugging #TIL

---

## 📋 목차
1. [문제점 분석](#1--문제점-분석)  
   - [1-2. 배경 제거가 방장만 되는 문제](#1-2-배경-제거가-방장만-되는-문제)  
   - [1-3. 다른 사람 영상도 이동 가능한 문제](#1-3-다른-사람-영상도-이동-가능한-문제)  
   - [1-4. 아바타(AI 영상) 동기화 안 되는 문제](#1-4-아바타ai-영상-동기화-안-되는-문제)  
   - [1-5. Layer zIndex 동기화 문제](#1-5-layer-zindex-동기화-문제)  
   - [1-6. 크기 조절 동기화 문제](#1-6-크기-조절-동기화-문제)  
   - [1-7. Border & Handle 표시 문제](#1-7-border--handle-표시-문제)  
2. [리팩토링 계획](#2-️-리팩토링-계획)  
3. [주요 수정 사항 요약](#3--주요-수정-사항-요약)  
   - [3-1. 레이어 생성 로직 수정](#3-1-레이어-생성-로직-수정)  
   - [3-2. AI 레이어 동기화 추가](#3-2-ai-레이어-동기화-추가)  
   - [3-3. 외부 클릭 시 선택 해제](#3-3-외부-클릭-시-선택-해제)  
   - [3-4. 카운트다운 완료 시 선택 해제](#3-4-카운트다운-완료-시-선택-해제)  
   - [3-5. Ref 동기화 개선](#3-5-ref-동기화-개선)  
4. [구현 우선순위](#4--구현-우선순위)  
5. [주의사항](#️-주의사항)  
6. [다음 단계](#-다음-단계)  
7. [배경 제거 디버깅 가이드](#5--배경-제거-디버깅-가이드)
   
---

## 1. 🔍 문제점 분석

### 1-2. 배경 제거가 방장만 되는 문제
  **아키텍처:**
  - ✅ 각 클라이언트가 자기 영상만 배경 제거 후 WebRTC로 송출 (이미 구현됨)
  - ✅ 다른 참가자들은 이미 배경 제거된 스트림을 받음
  
  **실제 원인 (추정):**
  1. **스트림 등록 타이밍 문제**
     - 배경 제거 준비되기 전에 WebRTC 연결이 먼저 되면 원본 스트림이 전송될 수 있음
     - `streamRegisteredRef`로 중복 등록 방지하지만, 초기 등록 타이밍이 중요
  
  2. **Peer 스트림 수신 문제**
     - `peers[layer.id]?.stream`이 배경 제거된 스트림인지 원본인지 확인 필요
     - WebRTC 연결 시점에 각 참가자가 배경 제거를 완료했는지 확인 필요
  
  3. **비디오 요소 렌더링 문제**
     - `participantVideoRefs`에 스트림이 제대로 할당되는지 확인
     - 스트림 변경 시 비디오 요소가 업데이트되는지 확인
  
  **해결 방안:**
  - WebRTC 연결 전에 배경 제거 준비 완료 대기
  - 스트림 변경 시 비디오 요소 강제 업데이트
  - 디버깅: 각 peer의 스트림이 어떤 타입인지 로깅

### 1-2. 다른 사람 영상도 이동 가능한 문제
  **원인:**
  - `layer.draggable`이 모든 user 레이어에 `true`로 설정됨
  - 본인 확인 로직이 드래그 시작 시에만 있고, 레이어 생성 시 구분 안 됨
  
  **해결 방안:**
  - 레이어 생성 시 `draggable: layer.id === myUserId || layer.isAI`로 설정
  - AI 레이어는 호스트만 `draggable: isHost`로 제한
  - 마우스 이벤트 핸들러에서 추가 검증

### 1-3. 아바타(AI 영상) 동기화 안 되는 문제
  **원인:**
  - AI 레이어 이동/리사이즈 시 WebRTC로 전송하는 로직이 없음
  - `sendMove`, `sendResize`가 `myUserId`에 대해서만 호출됨
  
  **해결 방안:**
  - AI 레이어 이동/리사이즈 시에도 별도의 메시지 타입으로 전송
  - 예: `sendAIMove(x, y)`, `sendAIResize(width, height)`
  - 또는 기존 메시지에 `layerId`를 포함하여 일반화

### 1-4. Layer zIndex 동기화 문제
  **원인:**
  - `shouldReorderRef`와 `setLayers` 호출이 로컬에서만 발생
  - zIndex 변경사항이 WebRTC로 전송되지 않음
  
  **해결 방안:**
  - zIndex 변경 시 `sendLayerOrder` 같은 메시지로 전체 레이어 순서 브로드캐스트
  - 다른 참가자들이 받으면 로컬 `videoLayers` 업데이트

### 1-5. 크기 조절 동기화 문제
  **원인:**
  - `sendResize`가 호출되지만 peer 측에서 적용이 늦거나 덮어씌워짐
  - `useEffect`에서 `peers`의 `width/height`를 반영하지만, 상태 업데이트 타이밍 이슈 가능
  
  **해결 방안:**
  - 리사이즈 완료 시점(mouseUp)에 최종 크기 전송
  - Debounce를 적용하여 과도한 업데이트 방지
  - Peer 데이터 우선순위를 명확히 (로컬 드래그 중일 때는 로컬 우선)

### 1-6. Border & Handle 표시 문제
  **원인:**
  - 선택 상태(`selectedLayer`) 관리가 제대로 안 됨
  - 카운트다운 완료 시 `tempHiddenSelectionRef`로만 처리하고 상태 초기화 안 함
  - 캔버스 외부 클릭 처리 없음
  
  **해결 방안:**
  - 레이어 외부 클릭 시 `selectedLayer` null로 설정
  - 카운트다운 완료 시 `setSelectedLayer(null)` 명시적 호출
  - 캡처 완료 후에도 선택 해제 유지

---

## 2. 🛠️ 리팩토링 계획

### Phase 1: 상태 관리 개선
  1. **레이어 권한 명확화**
     - `draggable` 속성을 정확하게 설정
     - 본인 레이어만 이동 가능하도록 제한
     - AI 레이어는 호스트만 제어
  
  2. **선택 상태 관리 개선**
     - Border/Handle 표시 로직 정리
     - 외부 클릭 시 선택 해제
     - 카운트다운/캡처 시 선택 해제

### Phase 2: WebRTC 동기화 강화
  1. **AI 레이어 동기화 추가**
     - AI 이동/리사이즈 메시지 타입 추가
     - 호스트가 변경 시 모든 참가자에게 브로드캐스트
  
  2. **레이어 순서(zIndex) 동기화**
     - 드래그 종료 시 전체 레이어 순서 전송
     - 수신 측에서 순서 적용
  
  3. **크기 조절 동기화 개선**
     - MouseUp 시 최종 크기 전송
     - 실시간 업데이트는 throttle 적용

### Phase 3: 배경 제거 디버깅
  1. **스트림 등록 순서 확인**
     - 배경 제거 완료 → WebRTC 연결 순서 보장
     - 각 참가자의 스트림 타입 로깅
  
  2. **Peer 스트림 검증**
     - 받은 스트림이 배경 제거된 것인지 확인
     - 비디오 트랙 정보 로깅

---

## 3. 📝 주요 수정 사항 요약

### 3-1. 레이어 생성 로직 수정
```typescript
// 본인 레이어만 draggable
const userLayer: VideoLayer = {
  id: myUserId,
  draggable: true, // 본인만 이동 가능
  ...
};

// Peer 레이어는 draggable false
const peerLayers: VideoLayer[] = participants
  .filter(p => p.id !== myUserId)
  .map((p) => ({
    id: p.id,
    draggable: false, // 다른 사람 레이어는 이동 불가
    ...
  }));

// AI 레이어는 호스트만
const aiLayer: VideoLayer = {
  id: 'ai-video',
  draggable: isHost, // 호스트만 이동 가능
  isAI: true,
  ...
};
```

### 3-2. AI 레이어 동기화 추가
```typescript
// WebRTC store에 추가 필요
sendAIMove(x: number, y: number)
sendAIResize(width: number, height: number)

// 레이어 이동/리사이즈 시
if (layer.isAI && isHost) {
  sendAIMove(newX, newY);
  sendAIResize(newWidth, newHeight);
}
```

### 3-3. 외부 클릭 시 선택 해제
```typescript
const handleMouseDown = (e: React.MouseEvent<HTMLCanvasElement>) => {
  const layer = getLayerAtPos(pos.x, pos.y);
  if (!layer) {
    setSelectedLayer(null); // 외부 클릭 시 선택 해제
    return;
  }
  // ...
};
```

### 3-4. 카운트다운 완료 시 선택 해제
```typescript
if (onCompleteRef.current) {
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      onCompleteRef.current?.();
      tempHiddenSelectionRef.current = false;
      setSelectedLayer(null); // 명시적 선택 해제
    });
  });
}
```

### 3-5. Ref 동기화 개선
```typescript
// 상태 변경 시 ref도 업데이트
useEffect(() => {
  selectedLayerRef.current = selectedLayer;
}, [selectedLayer]);

useEffect(() => {
  resizingLayerRef.current = resizingLayer;
}, [resizingLayer]);

useEffect(() => {
  draggedLayerRef.current = draggedLayer;
  isDraggingRef.current = isDragging;
}, [draggedLayer, isDragging]);
```

---

## 4. 🚀 구현 우선순위

### 높음 (즉시 적용)
  1. ✅ 레이어 draggable 권한 수정
  2. ✅ Border/Handle 표시 로직 수정
  3. ✅ 외부 클릭 시 선택 해제
  4. ✅ Ref 동기화
  5. ✅ 배경 제거 디버깅 로그 추가

### 중간 (다음 단계)
  6. ⚠️ AI 레이어 동기화 (WebRTC 메시지 추가 필요)
  7. ⚠️ zIndex 동기화 (WebRTC 메시지 추가 필요)
  8. ⚠️ 크기 조절 최적화

### 낮음 (추후 개선)
  9. 🔄 성능 최적화 (throttle/debounce)

---

## ⚠️ 주의사항

1. **WebRTC 메시지 타입 추가 시**
   - `useWebRTCStore`에 새 메시지 핸들러 추가 필요
   - 수신 측 로직도 함께 구현 필요

2. **성능 최적화**
   - 실시간 동기화는 throttle/debounce 적용
   - 불필요한 리렌더링 방지

3. **테스트**
   - 방장/참가자 각각 테스트
   - 다중 참가자 시나리오 확인
   - 네트워크 지연 시뮬레이션

---

## 📌 다음 단계

1. 개선된 코드 적용
2. WebRTC store 수정 사항 확인
3. 통합 테스트
4. 성능 모니터링

---

## 5. 🔍 배경 제거 디버깅 가이드

### 문제 진단 체크리스트

1. **각 클라이언트에서 확인**
   ```
   [CompositionCanvas] Registering processed stream to WebRTC
   [CompositionCanvas] Captured stream tracks: 1
   [CompositionCanvas] ✅ Processed stream registered successfully
   ```
   - 모든 참가자가 이 로그를 봐야 함
   - 안 보이면 배경 제거 준비가 안 된 것

2. **Peer 스트림 수신 확인**
   ```
   [CompositionCanvas] ✨ Creating new video element for {userId}
   hasVideoTracks: 1
   videoTrackEnabled: true
   videoTrackReadyState: "live"
   ```
   - 각 peer의 스트림 상태 확인
   - `readyState`가 "live"가 아니면 스트림 문제

3. **렌더링 시 경고 확인**
   ```
   ⚠️ No video element for peer {userId}
   ⚠️ Peer {userId} video not ready
   ⚠️ Peer {userId} has no srcObject
   ```
   - 이런 경고가 나오면 스트림 할당 문제

### 예상되는 문제 시나리오

**시나리오 1: 배경 제거 전 WebRTC 연결**
- 증상: 방장만 배경 제거됨
- 원인: 참가자가 배경 제거 준비되기 전에 스트림 전송
- 해결: WebRTC Store에서 배경 제거 완료 신호 대기

**시나리오 2: 스트림 교체 실패**
- 증상: 초기엔 원본, 나중에도 업데이트 안 됨
- 원인: `video.srcObject` 업데이트가 안 됨
- 해결: 스트림 변경 감지 및 강제 업데이트

**시나리오 3: Canvas 스트림 품질**
- 증상: 화질이 이상하거나 확대됨
- 원인: Canvas 해상도 vs 스트림 해상도 불일치
- 해결: `captureStream` FPS 조정 또는 Canvas 크기 확인

### 해결 방법

1. **개선된 코드 적용 후 콘솔 확인**
2. **모든 참가자가 "✅ Processed stream registered" 로그 확인**
3. **Peer 스트림 상태 로그 확인**
4. **문제 발견 시 WebRTC Store 타이밍 조정**
