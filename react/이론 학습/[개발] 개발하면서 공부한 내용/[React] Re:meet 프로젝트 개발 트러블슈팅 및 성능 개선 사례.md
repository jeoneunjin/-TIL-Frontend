# [React] Re:meet 프로젝트 개발 트러블슈팅 및 성능 개선 사례

## 📊 목차
1. [프로젝트 기술 선택 배경](#1-프로젝트-기술-선택-배경)
2. [실시간 영상 합성 성능 최적화 — FPS 87% 향상](#2-실시간-영상-합성-성능-최적화--fps-87-향상)
3. [Canvas vs CSS 레이어링 기술 선택 — 정량적 비교 분석](#3-canvas-vs-css-레이어링-기술-선택--정량적-비교-분석)
4. [Canvas + MP4 렌더링 Frame Drop 문제 해결](#4-canvas--mp4-렌더링-frame-drop-문제-해결)
5. [배경 제거 WebRTC 송출 문제 해결 — 크로마키 기법 적용](#5-배경-제거-webrtc-송출-문제-해결--크로마키-기법-적용)
6. [WebSocket & WebRTC 실시간 동기화 아키텍처](#6-websocket--webrtc-실시간-동기화-아키텍처)

---

## 1. 프로젝트 기술 선택 배경

### 📌 빌드 도구: 왜 Vite인가?

본 프로젝트는 **실시간(WebRTC) 기반 MVP**로, 빠른 개발 속도와 잦은 UI·기능 반복이 중요했습니다.

#### Vite vs CRA(Create React App)
- Vite는 **ESM 기반**으로 초기 실행과 HMR이 매우 빠름
- CRA는 Webpack 기반으로 초기 로딩과 개발 피드백 속도가 느림
- 빠른 실험이 필요한 MVP 성격상 Vite가 더 적합

#### Vite vs Webpack 직접 설정
- Webpack은 대규모 서비스에 적합하지만 설정 비용이 큼
- Vite는 별도 번들링 없이 개발 서버를 제공해 즉시 개발 가능
- 구조 검증과 기능 실험 중심 프로젝트에는 Vite가 효율적

#### Vite vs Next.js
- Next.js는 SSR/SEO 중심의 서비스에 적합
- 본 프로젝트는 **SEO보다 실시간 상호작용**이 핵심
- WebRTC·WebSocket 중심 구조에서는 SPA 기반 Vite가 더 적합

### 📌 상태 관리: 왜 Zustand + TanStack Query 조합인가?

본 프로젝트는 **실시간(WebRTC) 기반 MVP**로, 상태의 변경 빈도와 성격이 크게 다르기 때문에 **단일 상태 관리 도구가 아닌 역할 분리 전략**을 선택했습니다.

#### 상태의 성격이 다르기 때문

| 구분 | 특징 | 선택 도구 |
|------|------|----------|
| UI / 실시간 상태 | 매우 잦은 변경, 즉각적 반영 필요 | Zustand |
| 서버 상태 | 캐싱, 동기화, 재요청, 에러 처리 중요 | TanStack Query |

#### Zustand 선택 이유
- Redux 대비 보일러플레이트가 적고 Provider가 필요 없어 구조가 단순함
- 실시간 연결 상태, UI 상태처럼 **빈번하고 즉각적인 상태 변경에 적합**
- WebRTC 특성상 연결 상태, 참여자 상태가 매우 자주 변경됨
- 전역 상태 업데이트 비용이 낮아 실시간 서비스에 적합

#### TanStack Query 선택 이유
- 서버 데이터를 **클라이언트(UI) 상태와 명확히 분리**
- 캐싱, 로딩, 에러 처리를 표준화하여 중복 코드 감소
- refetch, invalidate 패턴으로 실시간 이벤트 대응이 용이

#### 상태 분리 기준 (설계 원칙)
- **Zustand**
  - UI 상태 (모달, 토스트 등)
  - WebRTC / WebSocket 연결 상태
  - 실시간 상호작용 상태
- **TanStack Query**
  - 서버 API 데이터
  - 초기 로딩 데이터
  - 캐싱 및 동기화 대상 데이터

---

## 2. 실시간 영상 합성 성능 최적화 — FPS 87% 향상

### 📌 문제 상황
- **위치**: `src/components/media/VideoComposition.tsx`, `src/components/photobooth/CompositionCanvas.tsx`
- **증상**: 
  - FPS: 13-16 (목표: 30fps)
  - 전경(카메라 영상) 깜빡임(Flickering) 발생
  - 잔상(Ghosting) 발생 — 손을 움직이면 잔상이 여러 개 남음

### 🔍 원인 분석
**Race Condition 발생:**
```typescript
Frame 1: processFrame 시작 (50ms 소요)
Frame 2: drawImage(bgRemovalCanvas) ← 아직 처리 중! 빈 Canvas 그림 → 깜빡임!
Frame 3: processFrame 완료
Frame 4: drawImage(bgRemovalCanvas) ← 정상
```

**동기 처리로 인한 렌더링 블로킹:**
```typescript
// Before: 동기 처리 (렌더링 멈춤)
await processFrame(userVideo);  // ❌ 50ms 블로킹
```

### ✅ 해결 방법

#### 1) 비동기 처리 (렌더링 루프 논블로킹)
```typescript
// After: 비동기 처리
processFrame(userVideo).finally(() => {
  isProcessingFrameRef.current = false;
});
// ✅ 렌더링 계속 진행
```

#### 2) Frame Holding Pattern (이전 프레임 보존)
```typescript
// 새 프레임이 준비되면 저장
processFrame(userVideo).then(() => {
  lastValidForeground.copyFrom(bgRemovalCanvas);
});

// 렌더링에서는 항상 이전 유효 프레임 사용
ctx.drawImage(lastValidForeground, x, y);
// ✅ 처리 중이어도 이전 프레임 표시 → 깜빡임 없음
```

#### 3) Double Buffering (원자적 합성)
```typescript
// 오프스크린에서 완전히 합성
offscreenCtx.drawImage(bgVideo, 0, 0);
offscreenCtx.drawImage(lastValidForeground, x, y);

// 완성된 프레임만 메인에 복사
ctx.drawImage(offscreenCanvas, 0, 0);
// ✅ 합성 중간 단계가 보이지 않음
```

#### 4) 잔상 제거 (명시적 Canvas 클리어)
```typescript
// Before
ctx.drawImage(bgVideo, 0, 0);
ctx.drawImage(fgCanvas, x, y);
// ❌ clear를 안 해서 계속 누적!

// After
ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);  // ✅ 먼저 클리어
ctx.drawImage(bgVideo, 0, 0);
ctx.drawImage(fgCanvas, x, y);
```

### 📈 개선 결과

| 항목 | 개선 전 | 개선 후 | 개선율 |
|------|---------|---------|--------|
| **FPS** | 13-16 | 28-30 | **+87%** |
| **깜빡임** | 심함 | 완전 제거 | **100%** |
| **잔상** | 심함 | 완전 제거 | **100%** |
| **CPU 부하** | 높음 | 낮음 | **-30%** |

**측정 방법:** 
- FPS: `requestAnimationFrame` 기반 프레임 타임스탬프 차이 계산
- CPU 부하: Chrome DevTools Performance 프로파일링

---

## 3. Canvas vs CSS 레이어링 기술 선택 — 정량적 비교 분석

### 📌 문제 상황
실시간 WebRTC 영상 2개(로컬 + AI 스트림)를 합성하는 UI 구현 시, **Canvas 합성**과 **CSS 레이어링** 중 어느 기술을 선택해야 할지 결정 필요

### 🔬 실험 설계
- **위치**: `src/components/photobooth/CompositionCanvas.tsx`
- **비교 대상**: Canvas 방식 vs CSS 레이어링 방식
- **측정 지표**: FPS, 메모리 사용량, Input Latency, Frame Sync Diff
- **실험 환경**: Chrome 최신 버전, 동일 WebRTC 스트림, 해상도 192×144, 30초 이상 연속 렌더링

### 📊 실험 결과

#### 성능 비교 (.log 분석)
| 항목 | Canvas 방식 | CSS 레이어링 방식 | 비교 결과 |
|------|-------------|-------------------|-----------|
| **FPS** | 120.6 (평균) | 61.9 (평균) | **Canvas 우세 (약 2배)** |
| **FPS 안정성** | 120-121 (최소-최대) | 60-69 (최소-최대) | **Canvas 우세 (안정적)** |
| **Input Latency** | 16.00ms | 16.00ms | 동일 |
| **Frame Sync Diff** | 0.00ms | 0.00ms | 동일 |

#### 메모리 비교 (Heap Snapshot 분석)
| 항목 | Canvas 방식 | CSS 레이어링 방식 | 비교 결과 |
|------|-------------|-------------------|-----------|
| **Total Heap Size** | ~45MB | ~52MB | **Canvas 우세 (13% 적음)** |
| **HTMLVideoElement** | 2MB | 8MB | **Canvas 우세** |
| **DOM Nodes** | 10MB | 25MB | **Canvas 우세** |
| **메모리 누수 징후** | 없음 | Detached DOM 증가 | **Canvas 우세** |

### ✅ 결론 및 기술 선택
**Canvas 방식 채택**
- FPS: 120.6 vs 61.9 → **약 2배 향상**
- 메모리: 45MB vs 52MB → **13% 절감**
- 장시간 실행 시 메모리 누수 없음
- DOM 노드 수 감소로 reflow/repaint 비용 절감

**적용 기술:** `src/components/photobooth/CompositionCanvas.tsx`에서 Canvas 기반 실시간 합성 구현

---

## 4. Canvas + MP4 렌더링 Frame Drop 문제 해결

### 📌 문제 상황
- **위치**: `src/components/photobooth/CompositionCanvas.tsx`
- **증상**: 배경 MP4 비디오에서 Frame Drop 지속 발생
  - 30초마다 평균 10-12프레임씩 선형적으로 누적
  - Canvas는 120 FPS로 안정적이지만 MP4만 Frame Drop 발생

### 📊 측정 데이터

| 시간 | BG Frame Drops | 증가량 |
|------|----------------|--------|
| 30s | 10 | +10 |
| 60s | 22 | +12 |
| 90s | 34 | +12 |
| 120s | 46 | +12 |
| 240s | 91 | +12 (평균) |

### 🔍 원인 분석

**병목 구조:**
```
비디오 디코더 성능 (30~50fps)
    ↓
MP4 비디오 프레임률 (60fps)
    ↓
Canvas 렌더링 속도 (120fps)
```

- Canvas는 충분히 빠름 (120fps)
- JS 코드도 문제없음
- **MP4 디코딩 성능이 가장 느린 병목**
- MP4 해상도/코덱/비트레이트가 높아 브라우저가 실시간 디코딩을 감당 못함

**FPS 120의 함정:**
- 120Hz 고주사율 모니터 사용 시 `requestAnimationFrame`이 120fps로 호출
- MP4 비디오는 60fps
- Canvas는 **같은 비디오 프레임을 2번씩 그림**
- 비디오 디코더가 60fps도 따라가지 못하면 → Frame Drop 발생

### ✅ 해결 방법

#### 1) MP4 파일 최적화 (ffmpeg 사용)
```bash
ffmpeg -i input.mp4 \
  -c:v libx264 \
  -preset fast \
  -crf 28 \
  -r 30 \
  output.mp4
```

#### 2) Canvas 렌더링 FPS 제한
```typescript
let lastRenderTime = 0;
const targetFPS = 60;
const frameInterval = 1000 / targetFPS;

function render() {
  const now = performance.now();
  const delta = now - lastRenderTime;

  if (delta < frameInterval) {
    requestAnimationFrame(render);
    return;
  }

  lastRenderTime = now;
  // 실제 drawImage 수행
}
```

### 📈 개선 결과
- **Frame Drop**: 30초당 10-12프레임 → 0프레임 **(100% 해결)**
- **메모리**: 약 1.3MB로 안정적 유지 (메모리 누수 없음)
- **Canvas FPS**: 120fps → 60fps로 제한하여 불필요한 중복 렌더링 제거

**검증 방법:**
- Chrome DevTools Performance: Media 패널에서 Video Frame Drops 모니터링
- Heap Snapshot: 메모리 누수 없음 확인 (total_self_size_bytes: 1,376,296 bytes)

---

## 5. 배경 제거 WebRTC 송출 문제 해결 — 크로마키 기법 적용

### 📌 문제 상황
- **위치**: `src/components/photobooth/CompositionCanvas.tsx`, `src/hooks/useBackgroundRemoval.ts`
- **증상**: 
  - MediaPipe로 배경 제거된 영상(투명 배경)이 WebRTC로 송출되지 않음
  - 상대방 화면에 빈 스트림 또는 검은 화면만 표시됨

### 🔍 핵심 문제: 투명(알파) 채널 전송 불가

**WebRTC의 구조적 한계:**
- 브라우저의 `canvas.captureStream()` → WebRTC로 전송하는 일반 비디오 트랙은 **알파 채널을 보존하지 못함**
- MediaPipe 배경 제거 결과는 Canvas에서 투명 배경(alpha)으로 합성 가능
- 하지만 이 투명 정보를 WebRTC로 그대로 전송하면 수신 측에서 "투명"으로 복원하기 어려움

### ✅ 해결 방법: 크로마키(Chroma Key) 우회 전송 기법

투명 배경 대신 **특정 단색 배경(녹색 #00FF00)**으로 치환하여 전송하고, 수신 측에서 해당 색상을 투명 처리하는 방식을 채택했습니다.

#### 전체 파이프라인

```
[송신 측]
사용자 카메라
    ↓
MediaPipe SelfieSegmentation (배경 제거)
    ↓
bgRemovalCanvas (투명 배경 전경)
    ↓
chromaKeySenderCanvas에 녹색 배경 + 전경 합성
    ↓
captureStream(30fps)
    ↓
WebRTC 전송 (녹색 배경 + 사람)

[수신 측]
peer video frame
    ↓
peerProcessingCanvas에 그림
    ↓
applyChromaKeyRemoval() → 녹색 픽셀 → alpha=0 (투명)
    ↓
투명 전경으로 최종 합성
```

#### 1) MediaPipe 배경 제거 (투명 전경 만들기)

**위치:** `src/hooks/useBackgroundRemoval.ts`

```typescript
// 1. 프레임 입력
processFrame(video) {
  inputCanvas.drawImage(video)
  SelfieSegmentation.send({ image: inputCanvas })
}

// 2. MediaPipe 결과 처리
handleSegmentationResults(results) {
  const mask = results.segmentationMask
  const image = results.image
  
  // 3. 마스크 후처리 (경계 안정화)
  // - Temporal smoothing (이전 프레임과 블렌딩)
  // - Blur 적용 (BLUR_RADIUS: 2.5px)
  // - Threshold 이진화 (THRESHOLD: 128)
  // - Brightness/Contrast 조정 (각 200)
  
  // 4. 최종 합성 (투명 배경)
  outputCtx.drawImage(image)  // source-over
  outputCtx.globalCompositeOperation = 'destination-in'
  outputCtx.drawImage(mask)   // 전경만 남김
}
```

**파라미터 튜닝 결과:**
- `modelSelection: 1` (Landscape model) - 배경 분리 품질 향상
- `BLUR_RADIUS: 2.5px` - 경계 자연스럽게
- `BRIGHTNESS: 200, CONTRAST: 200` - 경계 명확하게
- `THRESHOLD: 128` - 마스크 이진화로 녹색 번짐 방지
- `SMOOTHING_ALPHA: 0.7` - 프레임 간 떨림 제거

#### 2) 크로마키 송신용 스트림 생성 (녹색 배경 합성)

**위치:** `src/components/photobooth/CompositionCanvas.tsx`

```typescript
// 송신용 캔버스 생성 (640×480)
const chromaKeySenderCanvas = document.createElement('canvas')
chromaKeySenderCanvas.width = 640
chromaKeySenderCanvas.height = 480
const ctx = chromaKeySenderCanvas.getContext('2d')

// 매 프레임마다 녹색 배경 합성
function renderChromaKeyFrame() {
  // 1. 전체를 녹색으로 채움
  ctx.fillStyle = '#00FF00'
  ctx.fillRect(0, 0, 640, 480)
  
  // 2. 투명 전경(bgRemovalCanvas)을 위에 그림
  ctx.drawImage(bgRemovalCanvas, 0, 0, 640, 480)
  // 결과: 녹색 배경 + 사람 전경
}

// WebRTC 스트림 생성
const processedStream = chromaKeySenderCanvas.captureStream(30);
setMyStream(processedStream);
```

**핵심 원리:**
- 투명 대신 **완전한 단색 배경(#00FF00)**을 합성
- 표준 비디오 코덱으로 전송 가능
- 수신 측에서 색상 기반으로 투명 처리

#### 3) 수신 측 크로마키 제거 (녹색 → 투명)

**위치:** `src/components/photobooth/CompositionCanvas.tsx`

```typescript
// 원격 참가자 비디오 처리
function applyChromaKeyRemoval(ctx: CanvasRenderingContext2D, w: number, h: number) {
  const imageData = ctx.getImageData(0, 0, w, h)
  const data = imageData.data
  
  // 픽셀 순회하며 녹색 제거
  for (let i = 0; i < data.length; i += 4) {
    const r = data[i]
    const g = data[i + 1]
    const b = data[i + 2]
    
    // 녹색 계열 판별: g > 110 && r < 100 && b < 100
    if (g > 110 && r < 100 && b < 100) {
      data[i + 3] = 0  // alpha = 0 (투명)
    }
  }
  
  ctx.putImageData(imageData, 0, 0)
}

// 합성 루프에서 적용
peerProcessingCtx.drawImage(peerVideo, 0, 0, 320, 240)
applyChromaKeyRemoval(peerProcessingCtx, 320, 240)
// 투명 전경을 최종 캔버스에 합성
```

### 📈 개선 결과

✅ `isBgRemovalReady` 상태 확인 + 100ms 지연으로 스트림 등록 타이밍 보장  
✅ 크로마키 기법으로 투명 배경 성공적으로 WebRTC 전송  
✅ 수신 측에서 녹색 제거로 자연스러운 투명 전경 복원  
✅ 로컬/원격 양측에서 배경 제거된 영상 정상 표시  
✅ React Hook 구조 개선으로 불필요한 리렌더링 제거

### 🎯 크로마키 기법의 장단점

**장점:**
- WebRTC의 알파 채널 제약 우회
- 표준 비디오 코덱으로 전송 가능
- 브라우저 호환성 높음

**한계 및 튜닝 포인트:**
- **색상 충돌**: 사용자가 초록색 옷/소품 착용 시 전경이 함께 투명 처리될 수 있음
- **임계값 민감도**: RGB 조건(`g > 110 && r < 100 && b < 100`)이 단순하여 조명/노이즈에 민감
- **해상도 절충**: 수신 측은 320×240에서 크로마키 제거 후 확대 (성능과 품질의 절충)
- **경계 품질**: 현재는 단순 픽셀 제거만 수행, 추가 개선 시 HSV 기반 거리 계산, 소프트 키(알파 그라데이션) 적용 가능

### 💡 실제 구현 팁

**촬영 환경 권장사항:**
- 밝고 균일한 배경, 충분한 조명
- 단색 배경이 이상적
- 배경과 구분되는 색상의 옷 착용 (예: 흰 벽 → 검은 옷)

**마스크 후처리 최적화:**
- `modelSelection: 1` (Landscape model) 사용
- Temporal smoothing으로 프레임 간 떨림 제거
- Blur + Threshold 조합으로 경계 안정화

### 📌 추가 트러블슈팅: 스트림 등록 타이밍 문제

#### 문제 상황
```typescript
// Before: 잘못된 타이밍
useEffect(() => {
  if (bgRemovalOutputRef.current) {
    const processedStream = bgRemovalOutputRef.current.captureStream(30);
    setMyStream(processedStream);
  }
}, [bgRemovalOutputRef.current, setMyStream]);
```
- `bgRemovalOutputRef.current`가 존재해도 **캔버스에 실제 렌더링이 시작되기 전**에 실행
- `isBgRemovalReady`가 true가 되어도 첫 프레임이 렌더링되기 전에 `captureStream`이 호출되면 빈 스트림 전송

#### 해결 방법
```typescript
const streamRegisteredRef = useRef(false);

useEffect(() => {
  // 배경 제거가 준비되고, 스트림이 아직 등록되지 않았을 때만 실행
  if (isBgRemovalReady && bgRemovalOutputRef.current && !streamRegisteredRef.current) {
    console.log('[CompositionCanvas] Registering processed stream to WebRTC');
    
    // 100ms 지연 후 스트림 등록 (첫 프레임 렌더링 대기)
    const timer = setTimeout(() => {
      if (bgRemovalOutputRef.current) {
        try {
          const processedStream = bgRemovalOutputRef.current.captureStream(30);
          console.log('[CompositionCanvas] Captured stream tracks:', 
            processedStream.getVideoTracks().length);
          
          setMyStream(processedStream);
          streamRegisteredRef.current = true;
          
          console.log('[CompositionCanvas] ✅ Processed stream registered successfully');
        } catch (error) {
          console.error('[CompositionCanvas] Failed to capture/register stream:', error);
        }
      }
    }, 100);

    return () => clearTimeout(timer);
  }
}, [isBgRemovalReady, bgRemovalOutputRef, setMyStream]);
```

### 🔧 관련 파일
- `src/hooks/useBackgroundRemoval.ts` - MediaPipe 초기화 및 배경 제거
- `src/hooks/useMaskProcessing.ts` - 마스크 후처리
- `src/hooks/useTemporalSmoothing.ts` - 시간적 안정화
- `src/components/photobooth/CompositionCanvas.tsx` - 크로마키 송수신 구현
- `src/constants/segmentation.ts` - 설정 파라미터

---

## 6. WebSocket & WebRTC 실시간 동기화 아키텍처

### 📌 개요

본 프로젝트는 **WebSocket**과 **WebRTC**를 결합하여 실시간으로 여러 사용자가 한 방에서 촬영에 참여할 수 있도록 설계되었습니다.

### 🏗️ 전체 구조

#### WebSocket (Socket.IO 기반 시그널링)
- **역할**: 실시간 이벤트(입장, 퇴장, 준비, 이동, 촬영 등)와 방 상태 동기화
- **기능**: WebRTC Offer/Answer/ICE Candidate 교환, 방 참여자 상태 브로드캐스트

#### WebRTC (P2P 미디어 스트리밍)
- **역할**: 사용자 간 직접적인 영상/음성 스트림 전송
- **최적화**: 크로마키 기법으로 배경 제거된 영상 전송

### 🔄 실시간 동기화 흐름

#### 1. 방 입장 프로세스
```
사용자 입장 요청
    ↓
useRoomStore.joinRoom()
    ↓
WebSocket 'join' 이벤트 발송
    ↓
서버에서 방 정보 브로드캐스트
    ↓
useWebRTCStore.joinRoom() (미디어 연결 시작)
    ↓
PeerConnection 생성 및 Offer/Answer 교환
    ↓
실시간 스트림 동기화 완료
```

#### 2. Peer 연결 및 미디어 동기화
- PeerConnection 객체로 각 사용자 간 P2P 연결 관리
- Offer/Answer/ICE Candidate 교환은 WebSocket 시그널링을 통해 이루어짐
- 각 Peer의 미디어 트랙(영상/음성) 동기화 및 교체 가능

#### 3. 실시간 이벤트 동기화
WebSocket 이벤트 수신 → Store 액션 호출 → 컴포넌트 자동 리렌더링

```typescript
// 예시: 위치 업데이트
signalingService.on('move-updated', ({ userId, x, y }) => {
  useWebRTCStore.getState().updatePeerPosition(userId, x, y)
})

// 예시: 촬영 완료
signalingService.on('photo-captured', ({ imageUrl, participants }) => {
  useRoomStore.getState().setPhotoResult(imageUrl, participants)
})
```

### 🏛️ Store 중심 아키텍처

#### WebRTC Store 구조
```typescript
interface WebRTCStore {
  // Peer 연결 관리
  peers: Record<string, PeerState>
  addPeer: (userId: string, connection: RTCPeerConnection) => void
  removePeer: (userId: string) => void
  
  // 미디어 스트림 관리
  myStream: MediaStream | null
  setMyStream: (stream: MediaStream | null) => void
  
  // 방 관리
  joinRoom: (roomId: string) => Promise<void>
  leaveRoom: () => void
}
```

### 🎯 핵심 설계 원칙

**"모든 실시간 상태를 프론트엔드 Store에서 일관성 있게 관리하고, 컴포넌트는 Store만 바라보게 하여 유지보수성과 확장성을 높인다"**

- 서버와의 통신은 Store 내부에서만 처리
- UI는 Store 상태만 구독
- 데이터 흐름이 명확하여 복잡도 감소

### 📂 관련 파일
- `src/pages/PhotoBoothPage.tsx` - 메인 촬영 페이지
- `src/stores/useRoomStore.ts` - 방/참가자 상태 관리
- `src/stores/useWebRTCStore.ts` - WebRTC 연결 상태 관리
- `src/services/signaling/WebSocketSignalingService.ts` - WebSocket 시그널링
- `src/utils/webrtc/PeerConnection.ts` - P2P 연결 관리

---

## 🎯 종합 요약

### 핵심 아키텍처 패턴
```
사용자 카메라
    ↓
MediaPipe processFrame (비동기, 백그라운드)
    ├─ Temporal smoothing (프레임 간 안정화)
    ├─ Mask processing (blur, threshold, brightness/contrast)
    └─ 투명 배경 합성 (destination-in)
    ↓
bgRemovalCanvas (투명 전경)
    ↓
lastValidForeground에 복사 (Frame Holding)
    ↓
chromaKeySenderCanvas
    ├─ 녹색 배경 (#00FF00) 채우기
    └─ 투명 전경 합성
    ↓
captureStream(30fps)
    ↓
WebRTC 송출 (녹색 배경 + 사람)
```

### 적용 기술 스택
- **React 19** + TypeScript + Vite
- **상태 관리**: Zustand (실시간 상태) + TanStack Query (서버 상태)
- **MediaPipe SelfieSegmentation**: 실시간 배경 제거 (WASM)
- **Canvas API**: 고성능 영상 합성
- **WebRTC**: P2P 실시간 스트리밍
- **WebSocket (Socket.IO)**: 시그널링 및 실시간 이벤트 동기화
- **Performance API**: FPS 측정 및 모니터링
- **크로마키 기법**: 투명 배경 WebRTC 전송 우회

### 핵심 기술 선택 이유
1. **Vite**: ESM 기반 빠른 HMR, MVP 빠른 실험에 적합
2. **Zustand**: 실시간 상태 관리에 최적 (WebRTC 연결, 참여자 상태)
3. **TanStack Query**: 서버 상태 캐싱/동기화 전담
4. **Canvas 합성**: CSS 레이어링 대비 FPS 2배, 메모리 13% 절감
5. **크로마키 기법**: WebRTC의 알파 채널 제약 우회, 브라우저 호환성 확보

### 성과 수치 정리
| 개선 항목 | 개선 전 | 개선 후 | 개선율 |
|----------|---------|---------|--------|
| 영상 합성 FPS | 13-16 | 28-30 | **+87%** |
| Canvas vs CSS (FPS) | 61.9 | 120.6 | **+95%** |
| Canvas vs CSS (메모리) | 52MB | 45MB | **-13%** |
| Frame Drop | 10-12/30s | 0 | **-100%** |
| 깜빡임/잔상 | 심함 | 없음 | **-100%** |
| CPU 부하 | 높음 | 낮음 | **-30%** |
| 배경 제거 송출 | 실패 (빈 스트림) | 성공 (크로마키) | **해결** |

### 주요 튜닝 파라미터
- **MediaPipe**: `modelSelection: 1` (Landscape model)
- **마스크 처리**: `BLUR: 2.5px`, `THRESHOLD: 128`, `BRIGHTNESS/CONTRAST: 200`
- **Temporal smoothing**: `SMOOTHING_ALPHA: 0.7`
- **크로마키 임계값**: `g > 110 && r < 100 && b < 100`
- **Canvas FPS 제한**: 60fps (MP4 디코딩 병목 해결)

---

**프로젝트 소개 영상**: [Youtube - Re:meet 소개 영상](https://youtu.be/q-osTdBBtCA?si=wsrl1u0HlG-0WoQi)
