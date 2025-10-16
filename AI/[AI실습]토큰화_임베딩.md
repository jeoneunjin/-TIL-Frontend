# 🤖 토큰화/임베딩 학습 요약

## 📚 목차

- ***

---

## 1. 토크나이저

### 1-1 토크나이저(Tokenizer) 학습

#### 토크나이저란?

> 자연어 텍스트를 토큰이라는 작은 단위로 분해하는 전처리 도구

#### 토크나이저 학습

- 학습을 위한 요구사항

  1. 토크나이저 객체(클래스)
  2. 학습 데이터

- 학습 과정

1. 데이터셋 불러오기

```python
import pandas as pd
import os

file_list = os.listdir()
for file in file_list:
    if "ratings.txt"==file:
        review_dataframe = pd.read_table((os.getswd()+'/'+file), encoding='utf-8')
        reivew_dataframe = review_dataframe.dropna(how='any') # 널값 없애기
```

2. 필요한 데이터 값 추출하기

```python
# document 열만 가져와서 저장
with oen((os.getcwd() + '/' + 'naver_reivew.txt'), 'w', encoding='utf-8') as f:
    f.write('/n'.join(review_dataframe['document']))
```

3. 학습 되어 있지 않은 빈 tokenizer를 생성

- 파라미터:
  - `strip_accents` : 입력 텍스트의 악센트를 제거할지 여부를 결정하는 옵션
    > 한국어를 학습할 때는 `False`로 설정
  - `lowercase` : 영어를 모두 소문자로 바꿈
    > `False`로 설정하면 대문자 유지

```python
from tokenizers import BertWordPieceTokenizer
# 빈 tokenizer 생성; vocabulary_size는 0
tokenizer = BertWordPieceTokenizer(
    lowercase = False,
    strip_accents = False,
)
```

4. 토크나이저 학습

- 파라미터 :
  - `data_file` : 데이터 경로를 지정; 리스트 형태로 여러개의 파일을 지정할 수 있음
  - `vocab_size(default: 30000)` : 단어 사전 크기를 지정할 수 있음. 어떠한 값이 가장 좋다는 것은 X, 값이 클수록 많은 단어의 의미를 담을 수 있음
  - `initial_alphabet` : 꼭 포함됐으면 하는 initial alphabet을 학습 전에 추가
    - initial은 학습하기 이전에 미리 단어를 vocab에 넣는 것을 의미
    - special token들도 initial에 vocab에 추가됨
  - `limit_alphabet(default: 1000)` : initial tokens의 개수 제한
  - `min_frequency(default : 2)` : 최소 빈도수 의미; 어떤 단어가 1번 나오면 vocab에 추가하지 않음
  - `special_tokens` : 특수 토큰을 넣을 수 있음; BERT에는 다음과 같은 토큰이 들어가야 함
    - `[PAD]` : 패딩을 위한 토큰
    - `[UNK]` : OOV 단어를 위한 토큰
    - `[CLS]` : 문장의 시작을 알리고 분류 문제에 사용되는 토큰
    - `[SEP]` : 문장 사이사이를 구별해주는 토큰
    - `[MASK]` : MLM 태스크를 위한 마스크 토큰
  - `wordpiece_prefix(default: '##')` : sub-word라는 것을 알려주는 표시
    - BERT는 기본적으로 '##' 사용
    - 예를 들어, SS, ##AF, ##Y 처럼 sub-word를 구분하기 위해 사용
  - `show_progress` : 학습 과정을 보여줌

```python
tokenizer.trian(
    files = data_file,
    vocab_size = 30000
    min_frequency =2
    initial_alphabet = []
    limit_alphabet = 6000
    special_tokens = ["[PAD]", "[UNK]", "[CLS]", "[SEP]", "[MASK]"]
    wordpieces_prefix = "##"
    show_progress = True
)

vocab = tokenizer.get_vocab()
print("vocab size : ", len(vocab))
print(sorted(vocab, key=lambda x : vocab[x])[:20])

```

---

### 1-2 토크나이저를 이용한 토큰 ID 시퀀스 반환

- ID 시퀀스 변환이 필요한 이유 :
  모델이 “텍스트”를 직접 이해하지 못하고, 오직 숫자만 처리할 수 있기 때문에
  정수값으로 반화하는 과정이 필요

```python
text = "My name is Eunjin"
encoded = tokenizer.encode(text)

print('토큰화 결과 : ', encoded.tokens)
print('정수 인코딩 :', encoded.ids)
print('디코딩 : ', tokenizer.decode(encoded.ids))
```

---

### 1-3. 임베딩 벡터

> 어휘 사전을 통해 생성된 토큰 ID를 연속적인 벡터 공간에 매핑하는 과정

#### 임베딩 벡터의 역할

| 역할             | 설명                                           |
| ---------------- | ---------------------------------------------- |
| 🔹 **의미 보존** | 유사한 단어끼리 벡터 공간에서 가깝게 유지      |
| 🔹 **일반화**    | 단어가 달라도 비슷한 맥락이면 모델이 인식 가능 |
| 🔹 **연산 가능** | “왕 - 남자 + 여자 ≈ 여왕” 같은 의미 연산 가능  |

#### 4️⃣ 쉽게 비유하자면

- 정수 인코딩: 단어마다 학생 번호만 붙여준 느낌 (이름표)
- 임베딩 벡터: 학생의 성격, 관심사, 행동 패턴을 수치로 표현한 느낌
  즉, 임베딩 벡터는 단어의 **‘의미’**를 수학적으로 담은 표현

#### 초기화 파라미터

1. `num_embeddings` : 임베딩 사전의 크기(고유한 토큰의 총 개수)
2. `embedding_dim` : 각 임베딩 벡터의 차원(각 단어(또는 토큰)가 표현되는 벡터의 길이)
   보통 2의 제곱수 차원 사용(단, 무조건인 건 아님; 딱히 규칙은 없음)

#### 초기화

```python
embedding_vector : Tensor2D[VocabSize, EmbeddingSize] = nn.Embedding(vocab_size, 768)
```

### 3-2. 코사인 유사도(Consine Similarity)

> 두 벡터간의 각도를 측정하여 유사도를 나타내는 방법

$sim(A, B) = \cos(\theta) = \frac{A \cdot B}{\|A\|\|B\|}$

---

## 2. RNN/LSTM

### 2-1. RNN(Recurrent Neural Network)

> 순차적으로 정보를 처리하는 방식, 이전 시점의 hidden state를 현재 시점에 전달하는 순환 구조를 가진 신경망 모델

#### 특징

- 입력은 순차적으로 처리
- 같은 가중치를 반복적으로 사용
- 재귀적인 구조를 가짐

#### 은닉 상태(hidden state)?

> 시퀀스 입력을 처리하면서 현재까지의 정ㅂ조를 요약해서 저장한 벡터를 말함

- 즉, 과거 입력 정보 + 현재 입력 정보를 담고 있는 모델 내부 메모리 같은 역할

#### 과정

1. 텍스트 -> 워드 임베딩으로 변환
2. 워드 임베딩 차원에 맞게 RNN 구현
3. 입력 텍스트 데이터 토크나이저 사용해서 토큰화 후, 특정 컬럼(ex.ids)만 꺼냄
4. 변환된 input을 워드 임베딩으로 넣고 워드 임베딩을 RNN의 입력으로 넣어 두 output을 얻음
   - `hidden states` : 각 time step에 해당하는 hidden state를의 묶음
   - `h_n` : 모든 sequence를 거리고 나온 마지막 hidden state.hidden_states의 마지막과 동일

#### hidden state를 얻어서 어떤 작업을 하나?

> 은닉 상태(hidden state)는 문장의 정보들을 압축적으로 저장

- 문맥 벡터(context vector)로 사용됨

#### 문맥 벡터(context vector)

> 입력 문장의 정보들을 벡더상에 압축하여 저장한 것

- RNN의 마지막 Hidden State(h_n)을 Context Vector라 표현함

- 이 문맥 벡터를 이용하여 여러 task를 수행할 수 있게 됨
  1. 문장 분류
  2. 질의응답
  3. 기계 번역
  4. 텍스트 생성

#### Seq2Seq(Sequence to Sequence)

> RNN의 인코더, 디코더 구조를 여러 개로 묶어 가변 길이의 입력을 통해 가변 길이의 출력을 생성

#### EX. Seq2Seq 기반 번역 task

1. 입력 문장을 토큰 단위로 나누고 임베딩 벡터로 변환
2. 임베딩 벡터를 Encoder RNN/LSTM에 넣어 시퀀스를 처리
3. Encoder의 마지막 hidden state를 문맥 벡터(context vector)로 압축
4. 문맥 벡터를 Decoder RNN/LSTM의 초기 상태로 전달
5. Decoder가 이전 토큰과 문맥 벡터를 이용해 다음 토큰을 예측
6. 예측된 토큰을 반복 입력하며 전체 번역 문장 생성
7. 최종적으로 토큰 시퀀스를 원문과 유사한 텍스트 형태로 변환

> **결과값이 이상한 경우**
> 데이터로 충분히 학습 하지 않아서 그럼.

### 2-2. LSTM

#### RNN vs LSTM

- LSTM에는 cell state가 추가됨
- 장기 기억을 담당하는 cell state를 통해 성능을 높일 수 있음

| 항목      | RNN                                                           | LSTM                                               |
| --------- | ------------------------------------------------------------- | -------------------------------------------------- |
| 기억 능력 | 단기 의존성만 잘 기억                                         | 장기 의존성도 잘 기억                              |
| 상태      | hidden state(h)만 있음                                        | hidden state(h) + cell state(c)                    |
| 장점      | 구조 단순, 연산량 적음                                        | 긴 시퀀스에서도 문맥 정보 유지, 번역 성능 우수     |
| 단점      | 긴 문장 처리 시 **기울기 소실(vanishing gradient)** 문제 발생 | 구조 복잡, 파라미터 많아 연산량과 메모리 사용 증가 |

#### EX. Seq2Seq 기반 번역 task

1. 입력 임베딩
   - 입력 문장을 토큰 단위로 나누고 임베딩 벡터로 변환(RNN과 동일)
2. Encoder
   - LSTM 셀로 시퀀스를 처리
   - LSTM은 hidden state(h) + cell state(c) 두 가지를 유지 → 장기 의존 정보 저장 가능
   - 시퀀스 끝에서 마지막 hidden state와 cell state를 context vector로 사용
3. Decoder
   - Decoder LSTM 초기 상태를 Encoder의 hidden + cell state로 설정
   - 이전 토큰과 문맥 벡터를 입력으로 받아 다음 토큰 예측
   - 반복적으로 시퀀스를 생성
4. 출력
   - 최종 토큰 시퀀스를 텍스트로 변환

---

## 3. Attention Mechanism

### 3-1. Attention

- 문맥 벡터 하나로 모든 정보를 요약하는 문제를 해결하기 위해 고안됨
- 인력 시퀀스의 각 단어가 얼마나 중요한지 가중치를 계산
- Decoder가 다음 토큰을 생성할 때 필요한 부분만 집중해서 참고하도록 함

### 3-2. Attention을 사용한 seq2seq2 모델 구현

- 전체적인 Seq2Seq 모델의 구조 동일
- Encoder에서 context vector를 얻을 때, LSTM을 사용하는 Encoder 모듈을 그대로 사용
- Decoder에서 output token을 생성할 때, attention mechanism을 추가

1. Dot Attention(Luong attention) 구현 -> Decoder layer에 적용

- 기본 아이디어 :

  - Decoder의 현재 hidden state와 Encoder의 각 hidden state를 내적(dot product)으로 비교해서 중요도 계산
  - 내적 결과가 attention score가 되고, softmax를 통해 가중치로 변환

- 특징 :
  - 계산이 단순, 빠르고 효율적
  - 길이간 긴 문장에서도 Decoder가 중요 단어에 집중 가능
  - Luong Attention은 주로 "global attention"형태, Encoder모든 hidden state를 고려

? 근데 여기서도 Context Vector 사용, 왜 ?
Attention Mechan식을 쓰면, Decoder가 시점(t)마다 다른 context vector를 사용

- 방식: 1. Encoder의 모든 hidden state를 저장 2. Decoder 현재 시점 hidden state와 비교하여 각 입력 단어의 중요도(attention weight) 계산 3. 가중합(weighted sum) → Decoder 시점 t의 Context Vector
  => 즉, **Context Vector가 “매 시점마다 달라지는 벡터”**가 됨

#### 🔹 쉽게 비유하면

> - 과거: Encoder 마지막 hidden state = “문장 전체 요약본 한 장”
> - Attention: Decoder 시점마다 필요한 부분만 참고 → “문장 요약본 여러 장, 필요할 때 꺼내 쓰기”

2. Seq2Seq 모델에 적용

---

## 4. Huggingface 라이브러리

---

## 5. 아키텍처별 모델

1. Only Encoder 모델 : BERT 같은 모델; RAG 등 문서 검색에 주로 사용
2. Only Decoder 모델 : Chat-GPT같은 모델, 대화 , 번역, 챗봇 등 현재 가장 많이 사용되고 있음.
3. Encoder - Decoder 모델 : 최근에는 잘 사용하지 않음
