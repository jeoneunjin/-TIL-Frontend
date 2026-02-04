# [React] 배경 제거 WebRTC 송출 문제 해결 방안 — 스트림 등록 타이밍 및 렌더링 이슈 분석
Date: 2026-02-04  
Tag: #Frontend #React #WebRTC #Canvas #MediaPipe #Video #Streaming #Debugging #Performance #TIL  

---

## 📋 목차
- [1. 문제 분석](#1-문제-분석)
  - [1-1. 스트림 등록 타이밍 문제](#1-1-스트림-등록-타이밍-문제)
  - [1-2. 중복된 useEffect (문법 오류)](#1-2-중복된-useeffect-문법-오류)
  - [1-3. 배경 제거 캔버스 렌더링 문제](#1-3-배경-제거-캔버스-렌더링-문제)
  - [1-4. 배경 제거 프레임 처리 개선](#1-4-배경-제거-프레임-처리-개선)
- [🔍 디버깅 체크리스트](#-디버깅-체크리스트)
- [🎯 예상 결과](#-예상-결과)
- [🚀 추가 개선 사항 (선택)](#-추가-개선-사항-선택)
- [📝 요약](#-요약)

---


## 1. 문제 분석

### 1-1. 스트림 등록 타이밍 문제
**문제점:**
```typescript
useEffect(() => {
  if (bgRemovalOutputRef.current) {
    const processedStream = bgRemovalOutputRef.current.captureStream(30);
    setMyStream(processedStream);
  }
}, [bgRemovalOutputRef.current, setMyStream]);
```

- `bgRemovalOutputRef.current`가 존재해도 **캔버스에 실제 렌더링이 시작되기 전**에 실행될 수 있음
- `isBgRemovalReady`가 true가 되어도 첫 프레임이 렌더링되기 전에 `captureStream`이 호출되면 빈 스트림이 전송됨

**해결 방안:**
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

**개선 사항:**
- `isBgRemovalReady` 체크 추가
- 100ms 지연으로 첫 프레임 렌더링 보장
- `streamRegisteredRef`로 중복 등록 방지
- try-catch로 에러 핸들링
- 상세한 로그 추가

---

### 1-2. 중복된 useEffect (문법 오류)

**문제점:**
```typescript
const handleMouseMove = (e: React.MouseEvent<HTMLCanvasElement>) => {
  // ... 마우스 이동 처리 ...
  
  // ❌ 함수 내부에 useEffect가 있음!
  useEffect(() => {
    setVideoLayers((prev) => ...);
  }, [peers]);
};
```

- `useEffect`가 `handleMouseMove` **함수 내부**에 있어서 매 마우스 이동마다 재등록됨
- React Hook 규칙 위반
- 불필요한 리렌더링 발생

**해결 방안:**
```typescript
// handleMouseMove 함수 외부로 분리
useEffect(() => {
  setVideoLayers((prev) =>
    prev.map((layer) => {
      if (layer.type === "user" && peers[layer.id]) {
        const peer = peers[layer.id];
        if (typeof peer.width === "number" && typeof peer.height === "number") {
          return {
            ...layer,
            width: peer.width,
            height: peer.height,
          };
        }
      }
      return layer;
    })
  );
}, [peers]);
```

---

### 1-3. 배경 제거 캔버스 렌더링 문제

**문제점:**
```typescript
const videoEl = layer.id === myUserId
  ? userVideoRef.current  // ❌ 원본 비디오 사용
  : participantVideoRefs.current[layer.id];
```

- 내 스트림을 렌더링할 때 원본 `userVideo`를 사용
- 배경 제거된 `bgRemovalOutputRef` 캔버스를 활용하지 않음
- WebRTC로는 배경 제거된 스트림이 전송되지만, 로컬 화면에는 원본이 표시됨

**해결 방안:**
```typescript
// 내 스트림은 배경 제거된 캔버스 사용, peer는 원본 비디오 사용
const videoEl = layer.id === myUserId
  ? bgRemovalCanvas  // ✅ 배경 제거된 캔버스 직접 사용
  : participantVideoRefs.current[layer.id];

if (videoEl && (
  (videoEl instanceof HTMLCanvasElement) || 
  (videoEl.readyState >= 2 && videoEl.srcObject)
)) {
  offscreenCtx.save();
  offscreenCtx.translate(layer.x + layer.width, layer.y);
  offscreenCtx.scale(-1, 1);
  offscreenCtx.globalCompositeOperation = "source-over";
  
  if (videoEl instanceof HTMLCanvasElement) {
    // 배경 제거된 캔버스 직접 렌더링
    offscreenCtx.drawImage(
      videoEl,
      0, 0,
      videoEl.width,
      videoEl.height,
      0, 0,
      layer.width,
      layer.height
    );
  } else {
    // Peer 비디오 렌더링
    offscreenCtx.drawImage(
      videoEl,
      0, 0,
      videoEl.videoWidth,
      videoEl.videoHeight,
      0, 0,
      layer.width,
      layer.height
    );
  }
  
  offscreenCtx.restore();
}
```

**개선 사항:**
- 내 스트림은 `bgRemovalCanvas` 직접 사용
- `HTMLCanvasElement` 타입 체크 추가
- 캔버스와 비디오 요소 모두 처리 가능

---

### 1-4. 배경 제거 프레임 처리 개선

**문제점:**
```typescript
bgRemovalProcessFrame(userVideo)
  .then(() => {
    // 후처리
  })
  .finally(() => {
    isProcessingFrameRef.current = false;
  });
```

- 에러 핸들링 누락
- 처리 실패 시 로그 없음

**해결 방안:**
```typescript
bgRemovalProcessFrame(userVideo)
  .then(() => {
    // 배경 제거 완료 후 유효한 프레임 저장
    const prevFrameCtx = lastValidForegroundRef.current?.getContext("2d");
    if (prevFrameCtx && bgRemovalCanvas && 
        bgRemovalCanvas.width > 0 && bgRemovalCanvas.height > 0) {
      prevFrameCtx.clearRect(0, 0, compositionConfig.userWidth, compositionConfig.userHeight);
      prevFrameCtx.drawImage(
        bgRemovalCanvas,
        0, 0,
        compositionConfig.userWidth,
        compositionConfig.userHeight
      );
    }
  })
  .catch((error) => {
    console.error('[CompositionCanvas] Background removal error:', error);
  })
  .finally(() => {
    isProcessingFrameRef.current = false;
  });
```

**개선 사항:**
- `.catch()` 추가로 에러 핸들링
- 캔버스 크기 체크 추가 (width/height > 0)
- 상세한 에러 로그

---

## 🔍 디버깅 체크리스트

배경 제거 스트림이 제대로 송출되지 않을 때 확인할 사항:

### 1. 배경 제거 준비 상태
```typescript
console.log('isBgRemovalReady:', isBgRemovalReady);
console.log('bgRemovalOutputRef.current:', bgRemovalOutputRef.current);
```

### 2. 스트림 등록 확인
```typescript
console.log('[CompositionCanvas] Captured stream tracks:', 
  processedStream.getVideoTracks().length);
```
- 비디오 트랙이 1개 이상 있어야 함

### 3. 캔버스 렌더링 확인
```typescript
console.log('[CompositionCanvas] bgRemovalCanvas size:', 
  bgRemovalCanvas.width, bgRemovalCanvas.height);
```
- width, height가 0보다 커야 함

### 4. WebRTC 스트림 확인
```typescript
const myStream = useWebRTCStore.getState().myStream;
console.log('myStream:', myStream);
console.log('myStream tracks:', myStream?.getVideoTracks());
```

### 5. Peer 연결 확인
```typescript
console.log('peers:', peers);
Object.keys(peers).forEach(peerId => {
  console.log(`peer ${peerId} stream:`, peers[peerId]?.stream);
});
```

---

## 🎯 예상 결과

수정 후 기대되는 동작:

1. ✅ `isBgRemovalReady`가 true가 된 후 100ms 뒤 스트림 등록
2. ✅ 배경 제거된 캔버스가 WebRTC로 정상 송출
3. ✅ 로컬 화면에도 배경 제거된 영상 표시
4. ✅ Peer들이 배경 제거된 내 영상 수신
5. ✅ 불필요한 리렌더링 제거로 성능 개선

---

## 🚀 추가 개선 사항 (선택)

### 1. 스트림 품질 모니터링
```typescript
useEffect(() => {
  if (!myStream) return;
  
  const track = myStream.getVideoTracks()[0];
  if (track) {
    const settings = track.getSettings();
    console.log('Stream settings:', {
      width: settings.width,
      height: settings.height,
      frameRate: settings.frameRate,
    });
  }
}, [myStream]);
```

### 2. 스트림 재등록 기능
```typescript
const reRegisterStream = useCallback(() => {
  if (bgRemovalOutputRef.current) {
    streamRegisteredRef.current = false;
    const processedStream = bgRemovalOutputRef.current.captureStream(30);
    setMyStream(processedStream);
    streamRegisteredRef.current = true;
    console.log('Stream re-registered');
  }
}, [bgRemovalOutputRef, setMyStream]);
```

### 3. 스트림 상태 UI
```typescript
{/* 스트림 상태 표시 */}
<div className="absolute top-4 left-4 px-3 py-1.5 bg-black/70 text-white text-sm rounded">
  {streamRegisteredRef.current ? (
    <span className="text-green-400">● 송출 중</span>
  ) : (
    <span className="text-yellow-400">○ 대기 중</span>
  )}
</div>
```

---

## 📝 요약

| 문제 | 원인 | 해결 방안 |
|------|------|-----------|
| 빈 스트림 송출 | 캔버스 렌더링 전 `captureStream` 호출 | `isBgRemovalReady` 체크 + 100ms 지연 |
| 중복 useEffect | 함수 내부에 Hook 위치 | 함수 외부로 분리 |
| 로컬 화면 원본 표시 | 원본 비디오 렌더링 | 배경 제거 캔버스 직접 사용 |
| 에러 핸들링 부족 | catch 블록 누락 | try-catch 및 로그 추가 |

이 수정사항들을 적용하면 배경 제거된 영상이 WebRTC로 정상적으로 송출될 것입니다! 🎉
