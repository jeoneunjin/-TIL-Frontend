# [React] Canvas vs CSS 레이어링 — 실시간 영상 합성 방식 성능 비교 및 기술 선택

> **Date:** 2026-01-23  
> **Tag:** #Frontend #Canvas #WebRTC #Rendering #Performance #TIL

---

## 📋 목차

1. [문제 정의: 무엇을 선택해야 했는가](#1-문제-정의-무엇을-선택해야-했는가)
2. [비교 대상 및 접근 방식](#2-비교-대상-및-접근-방식)
3. [사전 가설 정리](#3-사전-가설-정리)
4. [실험 환경 및 조건](#4-실험-환경-및-조건)
5. [측정 지표 및 방법](#5-측정-지표-및-방법)
6. [실험 결과](#6-실험-결과)
7. [가설 검증 결과 정리](#7-가설-검증-결과-정리)
8. [최종 결론 및 기술 선택 이유](#8-최종-결론-및-기술-선택-이유)

---

## 1. 문제 정의: 무엇을 선택해야 했는가

실시간 WebRTC 영상 2개(로컬 + AI 스트림)를 합성하는 UI를 구현해야 했다.  
핵심 요구사항은 다음과 같았다.

- 지속적인 드래그 이동
- 실시간 렌더링
- 장시간 실행 안정성
- 향후 스트림 수 확장 가능성

이를 만족하기 위해 고려한 방식은 두 가지였다.

- **CSS 레이어링**: 여러 `<video>` 요소를 겹쳐서 렌더링
- **Canvas 합성**: hidden `<video>`를 Canvas에 drawImage로 합성

이론적으로는 Canvas가 유리해 보였지만,  
**실제 성능 차이가 체감·수치로 유의미한지 검증이 필요했다.**

---

## 2. 비교 대상 및 접근 방식

### 방식 A — CSS 레이어링

- 각 스트림을 개별 `<video>` 요소로 렌더링
- `position: absolute` + `z-index`로 합성
- 드래그 및 위치 제어는 DOM 이벤트 기반

### 방식 B — Canvas 합성

- 각 스트림을 hidden `<video>`에 연결
- 단일 `<canvas>`에서 `drawImage`로 프레임 단위 합성
- 렌더링은 `requestAnimationFrame` 루프 기반

---

## 3. 사전 가설 정리

실험 전 세운 가설은 다음과 같다.

- **Canvas 합성은 CSS 레이어링보다 FPS가 더 높고 안정적일 것이다**
- **Canvas 방식은 DOM 노드 수가 적어 reflow / repaint 비용이 낮을 것이다**
- **입력 지연(latency)과 프레임 동기화는 큰 차이가 없을 수도 있다**
- **draw order 제어로 Canvas에서도 z-index 개념을 충분히 대체할 수 있다**
- **스트림 수가 늘어날수록 Canvas 방식의 확장성이 더 좋을 것이다**

---

## 4. 실험 환경 및 조건

공통 조건

- Chrome 최신 버전
- 동일한 WebRTC 스트림 소스
- 동일 해상도 (192 × 144)
- 동일 네트워크 환경

실험 시나리오

- 로컬 스트림 + AI 스트림 2개 합성
- 지속적인 영상 이동(드래그)
- 30초 이상 연속 렌더링

---

## 5. 측정 지표 및 방법

- FPS: requestAnimationFrame 기반 측정
- Input Latency: 이벤트 타임스탬프 기준
- Frame Sync Diff: video.currentTime 비교
- 메모리: Chrome DevTools Heap Snapshot

> 절대값보다는 **Canvas vs CSS 간 상대 비교**에 초점을 둔다.

---

## 6. 실험 결과

### 6-1. 로그(.log) 분석 결과

| 항목 | Canvas 방식 (평균 / 최소 / 최대) | CSS 레이어링 방식 (평균 / 최소 / 최대) | 비교 결과 |
|---|---|---|---|
| **FPS** | 120.6 / 120 / 121 | 61.9 / 60 / 69 | **Canvas 우세** (약 2배 높음, 안정적) |
| **Avg Input Latency** | 16.00ms (모두 동일) | 16.00ms (모두 동일) | **동일** |
| **Frame Sync Diff** | 0.00ms (모두 동일) | 0.00ms (모두 동일) | **동일** |

---

### 6-2. Heap Snapshot(.heapsnapshot) 분석 결과

| 항목 | Canvas 방식 | CSS 레이어링 방식 | 비교 결과 |
|---|---|---|---|
| **Total Heap Size** | ~45MB | ~52MB | **Canvas 우세** (약 13% 적음) |
| **주요 객체 타입별 메모리** | HTMLVideoElement: 2MB<br>CanvasRenderingContext2D: 5MB<br>DOM Nodes: 10MB | HTMLVideoElement: 8MB<br>DOM Nodes: 25MB<br>Detached Trees: 3MB | **Canvas 우세** |
| **Retained Size** | 35MB | 42MB | **Canvas 우세** |
| **메모리 누수 징후** | 없음 | Detached DOM 증가 | **Canvas 우세** |

---

## 7. 가설 검증 결과 정리

| 사전 가설 | 검증 결과 |
|---|---|
| Canvas는 FPS가 더 안정적일 것이다 | ✅ **검증됨 (120fps vs 60fps)** |
| Canvas는 reflow / repaint 비용이 낮을 것이다 | ✅ **검증됨 (FPS + 메모리 차이)** |
| 입력 지연 차이는 크지 않을 수 있다 | ⚪ **시뮬레이션상 동일** |
| Canvas에서도 z-index 대체 가능하다 | ✅ **drawImage 순서로 검증** |
| 스트림 확장 시 Canvas가 유리할 것이다 | ✅ **메모리 효율로 간접 검증** |

---

## 8. 최종 결론 및 기술 선택 이유

### 종합 평가

| 방식 | FPS | 메모리 | 지연/동기화 | 평가 |
|---|---|---|---|---|
| **Canvas** | ✅ | ✅ | ⚪ | **실시간 영상 합성에 최적** |
| **CSS** | ❌ | ❌ | ⚪ | 구현은 단순하지만 성능 한계 명확 |

- FPS 차이(120 vs 60)는 명확한 **체감 차이**
- 메모리 차이(13%)는 **장시간 실행 시 의미 있음**
- 실험 결과, “차이가 미미하면 CSS”라는 전제는 성립하지 않음

👉 **결론**  
실시간 WebRTC 영상 합성 UI에서는 **Canvas 방식이 합리적인 선택**이며,  
이번 실험을 통해 그 선택을 데이터로 뒷받침할 수 있었다.
