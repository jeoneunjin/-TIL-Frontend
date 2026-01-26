# [React] 실시간 배경 제거와 영상 합성 기능 통합 과정에서의 개선 작업 정리

> **Date:** 2026-01-26  
> **Tag:** #Frontend #Canvas #RealTimeRendering #VideoComposition #TIL

---

## 📑 목차

- [🎯 초기 상황](#-초기-상황)
- [🔧 개선 단계별 정리](#-개선-단계별-정리)
  - [1단계: 인터페이스 호환성 분석 및 통합](#1단계-인터페이스-호환성-분석-및-통합-)
  - [2단계: Canvas DOM 렌더링 문제 해결](#2단계-canvas-dom-렌더링-문제-해결-)
  - [3단계: 잔상(Ghosting) 제거](#3단계-잔상ghosting-제거-)
  - [4단계: 깜빡임(Flickering) 완전 제거](#5단계-깜빡임flickering-완전-제거-)
- [📊 전체 개선 효과 비교](#-전체-개선-효과-비교)
- [🎯 핵심 아키텍처 패턴](#-핵심-아키텍처-패턴)
- [💡 적용된 패턴](#-적용된-패턴)
- [✅ 최종 결과](#-최종-결과)

---

## 🎯 초기 상황

**목표:** 기존 MediaPipe 배경 제거 + 새로운 webm 영상 합성 통합

**문제:**
- 기존 `useBackgroundRemoval`과 새로운 `VideoComposition` 인터페이스 불일치
- 통합 방법을 모름
- 실제로 작동하는지 확인 필요

---

## 🔧 개선 단계별 정리

### **1단계: 인터페이스 호환성 분석 및 통합** ✅

**문제:**
```typescript
// [VideoComposition.tsx](http://_vscodecontentref_/0) (잘못된 사용)
const { 
  startSegmentation,  // ❌ 존재하지 않는 함수
  stopSegmentation,   // ❌ 존재하지 않는 함수
} = useBackgroundRemoval({
  outputCanvas: canvas,  // ❌ 인자를 받지 않음
});
```
**해결:**
```
// 올바른 사용
const {
  inputCanvasRef,     // ✅ Hook 내부에서 생성한 ref
  maskCanvasRef,      // ✅ Hook 내부에서 생성한 ref
  outputCanvasRef,    // ✅ 배경 제거 결과
  processFrame,       // ✅ 수동 호출 필요
  isReady,           // ✅ MediaPipe 초기화 상태
} = useBackgroundRemoval();
```

**도움:**
- ✅ 기존 코드를 수정하지 않고 통합 가능
- ✅ `useBackgroundRemoval`의 실제 동작 방식 이해
- ✅ Canvas ref 관리 방법 파악

---

### **2단계: Canvas DOM 렌더링 문제 해결** ✅
**문제:**
```
[VideoComposition] 필수 요소 누락: {
  bgRemovalCanvas: false  // ❌ Canvas ref가 null
}
```

**원인:**
- `useBackgroundRemoval`이 반환한 Canvas ref는 DOM에 렌더링되지 않음
- `ref.current`가 `null`이 됨

**해결:**
```
{/* 숨겨진 요소로 Canvas 렌더링 */}
<div className="hidden">
  <canvas ref={inputCanvasRef} />
  <canvas ref={maskCanvasRef} />
  <canvas ref={bgRemovalOutputRef} />
</div>
```

**도움:**
- ✅ Canvas ref가 실제 DOM 요소를 참조
- ✅ MediaPipe 처리 결과가 Canvas에 정상 렌더링
- ✅ 합성 루프가 정상 작동 시작

---

### **3단계: 잔상(Ghosting) 제거** ✅

**문제:**
- 손을 움직이면 잔상이 여러 개 남음
- 이전 프레임이 지워지지 않고 계속 쌓임

**원인:**
```
// Before
ctx.drawImage(bgVideo, 0, 0);     // 배경만 그림
ctx.drawImage(fgCanvas, x, y);    // 전경 위에 덧그림
// ❌ clear를 안 해서 계속 누적!
```

**해결:**
```
// After
ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);  // ✅ 먼저 클리어
ctx.drawImage(bgVideo, 0, 0);
ctx.drawImage(fgCanvas, x, y);
```

**도움:**
- ✅ 잔상 완전 제거
- ✅ 깨끗한 렌더링
- ✅ 시각적 품질 대폭 개선

---

### **4단계: 깜빡임(Flickering) 완전 제거** ✅

**문제:**
- FPS 13-16 (목표: 30)
- 전경(카메라) 영상이 깜빡임
- 특정 프레임에서 사라졌다가 나타남

**원인 (Race Condition):**
```
Frame 1: processFrame 시작 (50ms 소요)
Frame 2: drawImage(bgRemovalCanvas) ← 아직 처리 중! 빈 Canvas 그림 → 깜빡임!
Frame 3: processFrame 완료
Frame 4: drawImage(bgRemovalCanvas) ← 정상
```

---

**해결 1: 비동기 처리 (블로킹 제거)**
```
// Before: 동기 처리 (렌더링 멈춤)
await processFrame(userVideo);  // ❌ 50ms 블로킹

// After: 비동기 처리 (논블로킹)
processFrame(userVideo).finally(() => {
  isProcessingFrameRef.current = false;
});
// ✅ 렌더링 계속 진행
```

---

**해결 2: Frame Holding (이전 프레임 보존)**
```
// 새 프레임이 준비되면 저장
processFrame(userVideo).then(() => {
  lastValidForeground.copyFrom(bgRemovalCanvas);
});

// 렌더링에서는 항상 이전 유효 프레임 사용
ctx.drawImage(lastValidForeground, x, y);
// ✅ 처리 중이어도 이전 프레임 표시 → 깜빡임 없음
```

---

**해결 3: Double Buffering (원자적 합성)**
```
// 오프스크린에서 완전히 합성
offscreenCtx.drawImage(bgVideo, 0, 0);
offscreenCtx.drawImage(lastValidForeground, x, y);

// 완성된 프레임만 메인에 복사
ctx.drawImage(offscreenCanvas, 0, 0);
// ✅ 합성 중간 단계가 보이지 않음
```

---

**도움:**
- ✅ 깜빡임 완전 제거
- ✅ FPS 13 → 28-30 (2배 이상 향상)
- ✅ 부드러운 렌더링
- ✅ CPU 사용률 감소
- ✅ 실무급 안정성

---

## 전체 개선 효과 비교

| 항목        | 초기        | 최종        | 개선율 |
|-------------|-------------|-------------|--------|
| 통합 상태   | 불가능      | ✅ 완료     | -      |
| Canvas 렌더링 | 실패        | ✅ 정상     | -      |
| 잔상        | 심함        | ✅ 없음     | 100%   |
| FPS         | 13–16       | 28–30       | +87%   |
| 깜빡임      | 심함        | ✅ 없음     | 100%   |
| CPU 부하    | 높음        | 낮음        | -30%   |

---

## 🎯 핵심 아키텍처 패턴
```
사용자 카메라
    ↓
processFrame (비동기, 백그라운드)
    ↓
bgRemovalCanvas 업데이트
    ↓
lastValidForeground에 복사 (Frame Holding)
    ↓
offscreenCanvas에서 합성 (Double Buffering)
    ├─ 배경: webm 영상
    └─ 전경: lastValidForeground
    ↓
outputCanvas에 원자적 복사
    ↓
화면 표시 (깜빡임 없음!)
```

---

## 💡 적용된 패턴

### Frame Holding Pattern
- 이전 유효 프레임 보존
- 처리 지연 시에도 빈 화면 방지

### Double Buffering Pattern
- 오프스크린에서 완전히 합성
- 메인 Canvas에 원자적 업데이트

### Async Processing Pattern
- 렌더링 루프 논블로킹
- 배경 제거를 별도 실행

### Throttling Pattern
- React state 업데이트 최소화
- 성능 오버헤드 감소

---

## ✅ 최종 결과

- ✅ 성공적으로 통합 완료
- ✅ 기존 MediaPipe 배경 제거 활용
- ✅ webm 배경 영상과 실시간 합성
- ✅ 30fps 안정적인 렌더링
- ✅ 잔상 / 깜빡임 완전 제거
- ✅ MediaRecorder로 녹화 가능




