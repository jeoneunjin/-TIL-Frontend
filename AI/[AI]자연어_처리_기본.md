# 🤖 자연어 처리 기본 학습 요약

## 📚 목차

1. [워드 임베딩](#1-워드-임베딩)  
2. [순차적 데이터](#2-순차적-데이터)  
3. [RNN](#3-rnn)  
4. [LSTM](#4-lstm)  
5. [언어 모델(Language Model)](#5-언어-모델-language-model)  
6. [Seq2Seq](#6-seq2seq)  
7. [Attention](#7-attention)  
8. [Transformer](#8-transformer)  

---

## 1. 워드 임베딩

### 1-1. 원-핫 인코딩
- ❌ 문제점
  - 단어 간 직교 → 유사도 측정 불가
  - 차원의 저주 발생
  - 의미적 정보 부족

### 1-2. 워드 임베딩
- **개념:** 단어 의미를 밀집 벡터(Dense vector)로 표현
- **예시:** "정부는 가계부채 문제를 해결하기 위해 **은행** 대출 규제를 강화했다."  
  → 주변 단어로 **은행** 의미 추정
- **대표 기법:** `Word2Vec`
  - **Skip-Gram (SG)**: 중심 단어 → 주변 단어 예측
    - 장점: 희귀 단어, 구 표현 강함  
    - 단점: 학습 느림
  - **CBOW:** 주변 단어 → 중심 단어 예측
    - 장점: 학습 빠름, 자주 등장 단어 강함  
    - 단점: 희귀 단어 약함

| 모델      | 장점                  | 단점           |
| --------- | ------------------- | -------------- |
| **Skip-Gram** | 희귀 단어 강함       | 학습 느림      |
| **CBOW**      | 학습 빠름            | 희귀 단어 약함 |

---

## 2. 순차적 데이터
- **정의:** 데이터 순서가 중요하며 시퀀스 간 관계가 존재
- **특징**
  1. 순서 중요
  2. 장기 의존성(Long-term dependency)
  3. 가변 길이(Variable length)

---

## 3. RNN

### 3-1. 특징
- 시퀀스 데이터 처리에 적합
- `hidden state`를 통해 이전 정보 저장
- 수식:

$$
h_t = \tanh(W_{hh} h_{t-1} + W_{xh} x_t)
$$


- 한 번에 하나씩 요소 처리

### 3-2. 한계
- 🔺 기울기 소실(Vanishing Gradient) 문제

---

## 4. LSTM

### 4-1. 개념
- RNN의 한계 극복 → 장기 의존성 처리 가능
- **상태**
  - `hidden state (h_t)`: 단기 정보
  - `cell state (C_t)`: 장기 정보

### 4-2. 게이트 구조

| 게이트        | 역할                                                   |
| ------------- | ------------------------------------------------------ |
| Forget Gate   | 이전 cell state에서 유지/삭제 여부 결정               |
| Input Gate    | 새 정보 중 cell state에 얼마나 반영할지 결정          |
| Output Gate   | cell state에서 hidden state로 얼마나 내보낼지 결정    |

---

## 5. 언어 모델 (Language Model)

- **정의:** 단어 시퀀스 확률 모델, 자연스러움 평가
- **확률 계산**
  
$$
P(w_1, w_2, ..., w_n) = \prod_{t=1}^{n} P(w_t | w_1,...,w_{t-1})
$$

- **N-gram 모델**
  - 연속 n개의 단어 조합
  - 발생 빈도 수집 → 다음 단어 예측
  - 통계적 한계: n ≥ 4 이상 → 등장 확률 낮음

---

## 6. Seq2Seq

- **Neural Machine Translation(NMT)** → Encoder-Decoder 구조
- **Encoder:** 입력 문장 → 잠재 벡터(Encoding)
- **Decoder:** 잠재 벡터 → 출력 문장(Decoding)
- **학습:** Teacher Forcing
- **출력 방법**
  - Greedy Inference: 각 단계 확률 최고 단어 선택 (오답 수정 불가)
  - Beam Search: k개의 후보 유지, 로그 확률 합 기준 선택

---

## 7. Attention

- **문제:** RNN → 입력 문장 전체를 하나의 벡터로 요약 → 정보 손실(Bottleneck)
- **효과**
  1. NMT 성능 향상
  2. Bottleneck 문제 완화
  3. Vanishing Gradient 완화
- **구성**
  | 벡터       | 역할                              |
  | --------- | -------------------------------- |
  | Query     | 참조할 정보 요청                   |
  | Key       | 정보 특성 표현                     |
  | Value     | 실제 정보 내용                     |
- 출력: Values 가중합으로 계산

---

## 8. Transformer

- **RNN 한계 극복**
  1. 장기 의존성(Long-term dependency)
  2. 병렬 처리 가능
- **Self-Attention**
  - 각 단어 → Query, Key, Value 벡터
  - Attention Score 계산: softmax(Query·Key^T / √d_k)
  - 최종 출력 = Score × Value
- **장점**
  1. 연산 병렬화
  2. 최대 상호작용 거리 O(1)
- **한계**
  1. 순서 정보 부재 → Positional Encoding 필요
  2. 비선형성 부족 → Feed-forward layer 사용
  3. 미래 참조 문제 → causal mask 필요 (언어 생성 시)
