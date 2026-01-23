# [React] Canvas + MP4 실시간 렌더링 성능 분석 — Frame Drop 원인 규명

> **Date:** 2026-01-23  
> **Tag:** #Frontend #Canvas #Video #Rendering #Performance #FPS #TIL

---

## 📋 목차
1. [문제 정의: 어떤 성능 문제가 발생했는가](#1-문제-정의-어떤-성능-문제가-발생했는가)  
2. [관찰된 현상 요약](#2-관찰된-현상-요약)  
3. [측정 지표 및 로그 분석](#3-측정-지표-및-로그-분석-요약)  
4. [Heap Snapshot 분석 (메모리 관점)](#4-heap-snapshot-분석-메모리-관점)  
5. [FPS 120의 의미와 함정](#5-fps-120의-의미와-함정)  
6. [원인 분석 (Root Cause)](#6-원인-분석-root-cause)  
7. [해결 방안](#7-해결-방안)  
8. [최종 결론](#8-최종-결론)

---

## 1. 문제 정의: 어떤 성능 문제가 발생했는가

Canvas 기반 실시간 렌더링 환경에서  
**배경 MP4 비디오와 카메라 스트림을 동시에 렌더링**하는 구조를 사용 중이었다.

- Canvas는 **120 FPS로 안정적으로 렌더링**
- 카메라 스트림은 문제 없음
- 하지만 **배경 MP4 비디오에서 Frame Drop이 지속적으로 증가**

👉 *“렌더링 FPS는 높은데, 왜 프레임 드롭이 누적될까?”* 가 핵심 질문이었다.

---

## 2. 관찰된 현상 요약

### BG Frame Drops (배경 MP4)

| 시간 | Drops | 증가량 |
|----|----|----|
| 30s | 10 | +10 |
| 60s | 22 | +12 |
| 90s | 34 | +12 |
| 120s | 46 | +12 |
| 150s | 55 | +9 |
| 180s | 67 | +12 |
| 210s | 79 | +12 |
| 240s | 91 | +12 |

**특징**
- 30초마다 평균 **10~12프레임 드롭**
- 선형적으로 증가 → **지속적인 병목 존재**
- 시간이 갈수록 누적됨 (일시적 현상 아님)

---

### My Frame Drops (카메라 스트림)

- 모든 구간에서 **1로 고정**
- 초기 1프레임 드롭 이후 정상 동작

👉 **카메라 스트림은 매우 안정적**, 문제 원인 아님

---

### Sync Diff (동기화 차이)

```
0.00ms → 8.30ms → 24.90ms → 25.00ms → 8.20ms → 33.30ms
```

- 최대 약 **33ms**
- 약 **0.5프레임 수준**
- 불규칙하지만 허용 범위

👉 **동기화 자체는 심각한 문제 아님**

---

## 3. 측정 지표 및 로그 분석 요약

- Canvas 렌더링 FPS: **120 (항상 일정)**
- BG Frame Drops: **지속 증가**
- Camera Frame Drops: **정상**
- Sync Diff: **허용 범위**

📌 **렌더링 루프는 정상인데, 입력 소스(MP4)가 따라오지 못하는 상황**

---

## 4. Heap Snapshot 분석 (메모리 관점)

### 전체 메모리 사용량
- `total_self_size_bytes`: **1,376,296 bytes (~1.3MB)**
- `total_nodes`: **37,362**

👉 메모리 사용량 매우 낮음  
👉 **메모리 누수 없음**

---

### 메모리 분포

| Type | Size | 비율 |
|----|----|----|
| hidden | 483KB | 35% |
| object_shape | 204KB | 15% |
| native | 157KB | 11% |
| code | 154KB | 11% |
| array | 150KB | 11% |
| closure | 117KB | 8% |

- `array (150KB)` → fpsHistory 등, 크기 제한 정상
- `closure (117KB)` → render loop / callback, 정상
- 전체적으로 **아주 건강한 분포**

👉 **성능 문제는 메모리와 무관**

---

## 5. FPS 120의 의미와 함정

### 왜 120 FPS가 나오는가?
- 120Hz 고주사율 모니터 사용
- `requestAnimationFrame`이 **120fps로 호출**

### 문제점
- MP4 비디오: **60fps**
- Canvas: **120fps**

👉 Canvas는 **같은 비디오 프레임을 2번씩 그림**  
👉 비디오 디코더가 60fps도 따라가지 못하면 → **Frame Drop 발생**

---

## 6. 원인 분석 (Root Cause)

### 🔍 핵심 병목 구조

```
비디오 디코더 성능 (30~50fps)
↓
MP4 비디오 프레임률 (60fps)
↓
Canvas 렌더링 속도 (120fps)
```


- Canvas는 충분히 빠름
- JS 코드도 문제 없음
- **MP4 디코딩 성능이 가장 느린 병목**

📌 MP4 해상도 / 코덱 / 비트레이트가 높아  
브라우저가 **실시간 디코딩을 감당 못하는 상태**

---

## 7. 해결 방안

### A. MP4 파일 최적화 

```bash
ffmpeg -i input.mp4 \
  -c:v libx264 \
  -preset fast \
  -crf 28 \
  -r 30 \
  output.mp4
```

---

### B. Canvas 렌더링 FPS 제한

```javascript
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

---

### C. 비디오 프리로드

```javascript
videoBg.preload = "auto";
```

---

## 8. 최종 결론

### ✅ 정상인 것
- 메모리 관리 정상  
  - 전체 사용량 약 **1.3MB**
  - Heap Snapshot 기준 **메모리 누수 없음**
- 카메라 스트림 안정성  
  - 초기 1프레임 드롭 이후 지속적으로 정상 동작
- Canvas 렌더링 성능  
  - requestAnimationFrame 기준 **120fps 안정적 유지**
- 비디오 동기화 정확도  
  - Sync Diff **8~33ms 수준**
  - 체감 및 기능상 문제 없는 허용 범위

---

### ❌ 문제인 것
- 배경 MP4 비디오 디코딩 성능 부족
- **30초마다 평균 10~12프레임씩 지속적으로 드롭**
- 시간 경과에 따라 Frame Drop이 **선형적으로 누적**

---

### 🧠 종합 결론
> **코드와 렌더링 로직은 문제가 없다.**  
> 병목은 브라우저의 **MP4 비디오 디코딩 성능**이다.  
>  
> Canvas는 120fps로 충분히 빠르게 렌더링되지만,  
> MP4 비디오는 60fps(또는 그 이하)로 디코딩되어  
> Canvas가 실제 새 프레임을 받지 못하고  
> 같은 프레임을 반복 렌더링하면서 Frame Drop이 발생한다.  
>  
> **MP4 파일을 30fps로 재인코딩하거나**,  
> **Canvas 렌더링 FPS를 60으로 제한**하면  
> Frame Drop 문제는 근본적으로 해결된다.


