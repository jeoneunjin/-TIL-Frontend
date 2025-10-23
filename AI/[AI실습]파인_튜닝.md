# 📊 파인 튜닝(Fine-tuning) 학습 요약

## 📚 목차

1. Fine-tuning 개요
2. PEFT(Parameter-Efficient Fine-Tuning)
3. LoRA(Low-Rank Adaptation)
4. QLoRA(Quantized LoRA)
5. Unsloth 프레임워크

---

## 1. 🔧 Fine-tuning 개요

- 이미 학습된 **Foundation 모델**에 추가 데이터를 넣어  
  특정 목적에 맞는 나만의 모델로 만드는 과정임

### 1-1. Fine-tuning 종류

- **Full Fine-tuning** : 전체 Layer 파라미터 업데이트
- **Partial Fine-tuning** : 일부 Layer만 업데이트

> 모델이 클수록 Full Fine-tuning은 학습 비용이 너무 큼  
> → 그래서 **PEFT** 같은 효율적인 방법이 필요함

---

## 2. ⚙️ PEFT (Parameter-Efficient Fine-Tuning)

- 모델 전체가 아닌 일부 파라미터만 학습시켜  
  **메모리, 시간, 비용 절약**하는 방법임

### 2-1. 학습 비용 줄이는 방법

| 방법                      | 개념                                   | 특징                                    |
| ------------------------- | -------------------------------------- | --------------------------------------- |
| **양자화 (Quantization)** | 모델 가중치 비트를 16bit → 4bit로 줄임 | 메모리 절약, 속도 향상                  |
| **Pruning**               | 불필요한 파라미터 제거                 | 약간의 성능 손실 가능, 모델 경량화 효과 |

> 핵심은 “적은 자원으로 효율적인 Fine-tuning”임

---

## 3. 🧠 LoRA (Low-Rank Adaptation)

- 기존 모델의 가중치는 고정❄️
- **작은 어댑터(LoRA 모듈)**만 학습🔥하는 방식임

### 3-1. 동작 방식

1. **Forward (순전파)** : `Wx + (BA)x`
   - `W`: 기존 가중치 (Freeze❄️)
   - `A, B`: LoRA 어댑터 (Train🔥)
2. **Backward (역전파)** : `A, B`만 업데이트
3. **추론(Inference)** : 학습 끝나면 `W + BA`로 병합

### 3-2. 장점

- 학습 파라미터 1% 이하 → GPU 메모리 절약 💾
- 학습 속도 빠름 ⏩
- 원본 모델 손상 없음 ❄️
- 성능 유지력 높음 💪

> 한 줄 요약  
> 👉 원본 모델 그대로 두고, 작은 어댑터만 학습하는 효율적 미세조정 방식

---

## 4. ⚡ QLoRA (Quantized LoRA)

> **QLoRA = Quantization + LoRA**  
> → 모델을 양자화(4bit)한 뒤 LoRA 어댑터를 학습하는 방식

### 4-1. 양자화(Quantization) 개념

- 모델이 사용하는 실수를 16bit → 4bit로 줄이는 기술
- 연산 속도 빨라지고 메모리 절약됨
- 단, 정확도는 약간 손실 가능

| 비트 수 | 정밀도    | 특징                     |
| ------- | --------- | ------------------------ |
| 2bit    | 매우 거침 | 속도 빠름                |
| 4bit    | 제한적    | 적당히 정밀              |
| 8bit    | 정밀      | 대부분의 상황에서 충분함 |
| 16bit   | 매우 정밀 | 연산 느림                |

> 핵심 : 비트를 줄이면 메모리·연산량 감소,  
> 중요한 연산은 고정밀(16bit) 유지함

### 4-2. QLoRA 과정

1. 원본 16bit 모델을 4bit로 양자화
2. LoRA 어댑터만 학습
3. 적은 GPU 메모리로 대형 모델 파인튜닝 가능

| 구분          | LoRA               | QLoRA                 |
| ------------- | ------------------ | --------------------- |
| 핵심 아이디어 | 일부 어댑터만 학습 | 양자화 + 어댑터 학습  |
| 필요 리소스   | 중간 수준 GPU      | 저사양 GPU·Colab 가능 |
| 장점          | 빠르고 효율적      | 훨씬 더 가볍고 저비용 |
| 의미          | 효율적 미세조정    | 대형 모델의 대중화 🚀 |

> 한 줄 요약  
> 👉 **QLoRA = 4bit 양자화 + LoRA 어댑터 학습**  
> → 정확도 유지하면서 속도·메모리·비용 모두 절약하는 기술

---

## 5. 🚀 Unsloth 프레임워크

- 효율적인 LLM 파인튜닝 및 강화학습용 프레임워크
- **속도, 메모리, 컨텍스트 길이** 모두 최적화된 구조

### 5-1. 특징

- 기존 FlashAttention 2 대비 **최대 30배 빠름**,  
  **VRAM 30~75% 절감**, **컨텍스트 길이 13배 향상**
- **Triton 커널, torch.compile, RoPE Scaling, FlashAttention** 등 고성능 커널 사용
- **QLoRA, LoRA, RSLORA, LoftQ** 등 최신 기법 지원
- **Hugging Face Transformers 완전 호환**

### 5-2. 모델 예시

`unsloth/gemma-3-270m-it`  
→ Gemma 모델의 **경량화된 파인튜닝 특화 버전**

- 일반 사용자 : `google/gemma-3-270m-it` 그대로 사용
- Colab·로컬 GPU 사용 시 : `unsloth/gemma-3-270m-it` 권장

> 한 줄 요약  
> 👉 **Unsloth는 LLM을 더 빠르고, 더 작게, 더 싸게 학습시키는 프레임워크**  
> Colab에서도 대형 모델 파인튜닝 가능하게 만들어줌 💪
