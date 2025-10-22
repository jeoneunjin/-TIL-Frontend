# 📊 Customer Service AI 에이전트 개발 : RAG 기반 학습 정리

## 📚 목차

1. [환경 설정](#1-환경-설정)
2. [LangChain](#2-langchain)
3. [RAG & Tool](#3-rag--tool)
4. [Agent & Tool](#4-agent--tool)

---

## 1. 환경 설정

### 1-1. 청킹/파싱

#### 개념 요약

| 구분                | 개념                                                                                                | 비유                                |
| ------------------- | --------------------------------------------------------------------------------------------------- | ----------------------------------- |
| **청킹 (Chunking)** | 긴 문서를 LLM이 이해할 수 있도록 **작은 단위(청크)**로 나누는 과정                                  | 긴 책을 **챕터별로 나눠 읽기**      |
| **파싱 (Parsing)**  | 복잡한 문서(PDF, 웹페이지 등)에서 LLM이 처리할 수 있는 **순수 텍스트와 메타데이터를 추출하는 과정** | 문서에서 **중요한 내용만 필기하기** |

#### 🧠 LLM이 문서를 이해하려면 왜 청킹(Chunking)과 파싱(Parsing)이 필요할까?

#### 1️⃣ 청킹이 필요한 이유

- LLM은 한 번에 처리할 수 있는 텍스트 양이 제한되어 있음
  → 이 한계를 **컨텍스트 창(Context Window)**이라고 함.
- 긴 문서를 그대로 넣으면 초반부만 이해하거나 후반부는 잊어버림
- 따라서, 문서를 **청크(chunk)** 단위로 잘라서 순차적으로 이해하게 함

#### 2️⃣ 파싱이 필요한 이유

- 문서에는 텍스트 외에도 **표, 이미지, 링크, 서식 등 비텍스트 요소**가 많음
- LLM은 이런 비텍스트 정보를 직접 처리하기 어려움
- 따라서 문서를 **파싱(Pasing)**해서 LLM이 읽기 좋은 형태(텍스트 + 메타데이터)로 변환해야 함

### 1-2. 토크나이징/청킹

긴 텍스트를 다루는 두 가지 핵심 방법
→ **토크나이징(Tokenizing)** 과 **청킹(Chunking)**

#### 🧠 1️⃣ 토크나이징 (Tokenizing)

> **정의 :**
> 텍스트를 LLM이 이해하는 가장 작은 단위인 **토큰(token)**으로 쪼개는 과정

#### ✅ 주요 특징

- 단어, 구두점, 공백 등으로 텍스트를 세분화
- `tiktoken` 등의 라이브러리를 사용해 모델별 토큰화를 수행
- **문자 수 ≠ 토큰 수** → 언어 구조나 인코딩 방식에 따라 달라짐
- LLM은 토큰 단위로 입력을 읽고 이해함
- 모델마다 최대 토큰 수(컨텍스트 창) 제한이 있음

#### 📘 예시

> “안녕하세요!” → ["안녕", "하세요", "!"]
> 한글 5자라도, 실제로는 3~5개 토큰으로 쪼개질 수 있음

#### 🧩 2️⃣ 청킹 (Chunking)

> **정의 :**
> 긴 문서를 의미적으로 연관 있는 **청크(Chunk)** 단위로 나누는 과정

#### ✅ 주요 특징

- LLM이 한 번에 다 읽을 수 없는 긴 문서를 여러 조각으로 분리
- `RecursiveCharacterTextSplitter` 같은 도구를 사용해 **줄바꿈, 문단, 구두점 기준으로 재귀적 분할**

#### ⚙️ 주요 파라미터

| 파라미터        | 설명                                                                                |
| --------------- | ----------------------------------------------------------------------------------- |
| `chunk_size`    | 각 청크의 최대 문자 수. 너무 크면 불필요한 내용이 섞이고, 너무 작으면 정보가 흩어짐 |
| `chunk_overlap` | 청크 간 겹치는 문자 수. 문맥이 끊기지 않도록 일정 부분을 중복 포함시킴              |

#### 🔍 설정에 따른 변화

- chunk_size가 작을수록 → 청크 개수 많아짐, 세밀한 분할
- chunk_overlap이 클수록 → 문맥 유지 잘됨, 중복 데이터 증가

> **💡 핵심 차이:**
>
> - 토크나이징은 LLM의 언어 처리 단위
> - 청킹은 RAG 시스템의 정보 검색 단위

### 1-4. 코드 예시

```python
from langchain_text_splitters import RecursiveCharacterTextSplitter
import tiktoken

# 예시 텍스트
text = """
AI 온라인 서점입니다. 고객님의 소중한 문의에 감사드립니다.
배송 정책에 대해 안내해 드리겠습니다.
일반 도서의 경우 오후 3시 이전 주문 시 당일 발송됩니다.
주말 및 공휴일은 배송이 어렵습니다.
제주 및 도서 산간 지역은 추가 배송비가 발생할 수 있습니다.
주문 번호 order-123의 배송 상태를 조회하시려면 마이페이지에서 확인 부탁드립니다.
"""

# 1. 토크나이징 (tiktoken 사용)
# 모델에 따라 인코딩 방식이 다를 수 있습니다. 여기서는 'cl100k_base'를 사용합니다.
encoding = tiktoken.get_encoding('cl100k_base')
tokens = encoding.encode(text)

print("--- 토크나이징 결과 ---")
print(f"원문 텍스트 길이: {len(text)} 문자")
print(f"토큰 수: {len(tokens)} 토큰")
print(f"토큰 예시: {tokens[:10]}...") # 첫 10개 토큰 예시
print("-" * 20)


# 2. 청킹 (RecursiveCharacterTextSplitter 사용)

# 다양한 chunk_size와 chunk_overlap으로 비교
chunking_configs = [
    {"chunk_size": 100, "chunk_overlap": 0},
    {"chunk_size": 100, "chunk_overlap": 20},
    {"chunk_size": 50, "chunk_overlap": 0},
    {"chunk_size": 50, "chunk_overlap": 10},
]

print("\n--- 청킹 결과 비교 ---")

for config in chunking_configs:
    chunk_size = config["chunk_size"]
    chunk_overlap = config["chunk_overlap"]

    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=chunk_size,
        chunk_overlap=chunk_overlap,
        length_function=len, # 길이를 문자로 계산
        is_separator_regex=False,
    )

    chunks = text_splitter.create_documents([text])

    print(f"\n[Chunk Size: {chunk_size}, Chunk Overlap: {chunk_overlap}]")
    print(f"생성된 청크 개수: {len(chunks)}")
    for i, chunk in enumerate(chunks):
        print(f"  청크 {i+1} (길이: {len(chunk.page_content)}):")
        print(f"  '{chunk.page_content[:50]}...'") # 각 청크의 앞부분만 출력

print("-" * 20)
```

---

## 2. LangChain

### 2-1. 🧩 LangChain 개요

> LangChain은 대규모 언어 모델(LLM)을 활용해 챗봇, QA 시스템 등 AI 애플리케이션을 쉽게 만들 수 있게 도와주는 프레임워크임.  
> 프로그래밍 경험 없어도 템플릿과 도구를 통해 빠르게 개발 및 배포 가능함.  
> 복잡한 개발 과정 단순화하여 AI 앱 개발 진입 장벽 낮춰줌.

#### 🏗️ LangChain 구성요소

#### 🔹 1. LangChain Library

LangChain 기능 모아둔 패키지 모음임.

- **`langchain-core`**: LangChain 기본 문법 제공
- **Integrated Packages**: 외부 도구와 LangChain 연결 쉽게 함
  > 예: `langchain-upstage`, `langchain-chroma`
- **`langchain`**: 체인, 에이전트, 검색 전략 등 애플리케이션 두뇌 역할

#### 🔹 2. LangChain Templates

작업별 템플릿 제공함.  
빠르게 설정하고 실행 가능하게 도와줌.

#### 🔹 3. LangServe

LangChain으로 만든 애플리케이션을 **REST API 형태로 배포**할 수 있게 해주는 도구임.

#### 🔹 4. LangSmith

애플리케이션 **디버그, 테스트, 모니터링** 지원하는 플랫폼임.

#### 🔹 5. LangGraph

LLM의 여러 상태 관리하면서 **Agent 구조화** 가능하게 하는 프레임워크임.

---

### 2-2.💡 코드 구성 요약

#### 1️⃣ 선호하는 LLM 정의

- `langchain_upstage`에서 `ChatUpstage` 가져옴
- `ChatUpstage()` 인스턴스 생성 후 `llm` 변수에 저장

#### 2️⃣ 입력 프롬프트 정의

- `langchain_core.prompts`의 `ChatPromptTemplate` 사용
- `from_messages()`로 시스템 프롬프트, 예제, 사용자 입력 등 포함한 템플릿 생성

#### 3️⃣ 출력 파서 정의

- `langchain_core.output_parsers`의 `StrOutputParser` 사용
- 출력 형식 정의
- 더 자세한 내용은 **LangChain Guide 참고**

#### 4️⃣ 체인 정의

- `rag_with_history_prompt`, `llm`, `StrOutputParser()`를 파이프(`|`) 연산자로 연결해 체인 구성

#### 5️⃣ 체인 호출

- `chain.invoke({})`로 실행
- 결과 출력

### 2-3. 코드

```python
# LLM Chain 구성하는 법
# 1. llm 정의, 2. prompt 정의, 3. chain 정의, 4. chain 호출

# 1. define your favorite llm, solar
from langchain_upstage import ChatUpstage

llm = ChatUpstage()

# 2. define chat prompt
from langchain_core.prompts import ChatPromptTemplate  # '대화' 형태로 prompt template 생성

prompt = ChatPromptTemplate.from_messages(
    [
        ("system", "모든 답변은 친절하고 공손한 말투로 해주세요."),

        # few-shot prompting
        ("human", "지구에서 가장 큰 바다는 어디인가요?"),  # human request
        ("ai", "네, 태평양이 가장 큰 바다입니다."),         # LLM response
        ("human", "그럼, 가장 긴 강은 어디인가요?"),
        ("ai", "가장 긴 강은 나일강입니다."),

        # User Query
        ("human", "그렇다면, 가장 높은 산은 어디인가요?"),
    ]
)

# 3. define chain
from langchain_core.output_parsers import StrOutputParser  # 문자열(text, string)만 나오게 하는 출력 파서

# chain = prompt | llm  # without output parser

chain = prompt | llm | StrOutputParser()  # with output parser

# 4. invoke the chain
c_result = chain.invoke({})
print(c_result)
```

---

## 3. RAG & Tool

### 3-1. 🔍 RAG & Tool 이해하기

### 🧠 RAG (Retrieval-Augmented Generation)

**RAG**는 LLM이 답변을 생성할 때 외부 지식을 활용하도록 돕는 기술입니다.  
즉, **"검색(Retrieval) + 증강(Augmented) + 생성(Generation)"**의 조합입니다.

#### ✅ 구성 요소

1. **Retrieval (검색)**

   - 사용자의 질문과 관련된 정보를 **Vector Store**(벡터 데이터베이스)에서 검색합니다.

2. **Augmented (증강)**

   - 검색된 정보를 LLM 입력에 추가해, 답변 품질과 정확성을 높입니다.

3. **Generation (생성)**
   - LLM이 검색된 정보와 질문을 바탕으로 자연어 형태의 답변을 생성합니다.

#### 💡 RAG의 필요성

- **정보 최신성 확보**: 학습 이후 생긴 최신 정보를 반영할 수 있습니다.
- **환각(Hallucination) 감소**: 실제 문서를 근거로 답변을 생성하므로 신뢰성이 향상됩니다.
- **도메인 지식 활용**: 특정 분야의 문서만 Vector Store에 저장해 효율적으로 활용할 수 있습니다.

#### 🧩 문서 처리 파이프라인

RAG는 문서를 LLM이 이해 가능한 형태로 변환하기 위한 세 단계를 거칩니다.

1. **파싱 (Parsing)**

   - PDF나 웹페이지 등에서 **텍스트와 메타데이터를 추출**하는 과정입니다.
   - 복잡한 문서를 LLM이 읽기 좋은 순수 텍스트로 변환합니다.

2. **청킹 (Chunking)**

   - 추출된 긴 텍스트를 **의미 단위의 작은 조각(청크)** 으로 나눕니다.
   - LLM의 컨텍스트 한계와 검색 효율성을 고려합니다.

3. **인덱싱 (Indexing)**
   - 각 청크를 **임베딩(벡터)** 으로 변환하고 **Vector Store**에 저장합니다.
   - 이 과정을 통해 빠르고 정확한 **유사도 검색**이 가능합니다.

---

### 3-2. 🧰 Retriever & Tool

1. **Vector Store 생성**

   - 텍스트 청크를 벡터로 변환해 저장합니다.
   - 예: `Chroma`, `Pinecone`, `FAISS` 등

2. **Retriever 생성**

   - 사용자의 질문과 의미적으로 가장 유사한 청크를 검색합니다.

3. **Tool로 등록**
   - 에이전트가 Retriever를 **도구(Tool)** 로 활용해 필요한 정보를 찾아 답변을 생성할 수 있습니다.

### 3-3. ⚙️ 전체 흐름 요약

```yaml
원본 문서
↓ (파싱)
정제된 텍스트
↓ (청킹)
텍스트 청크 리스트
↓ (인덱싱)
Vector Store 저장
↓ (Retrieval)
질문과 유사한 정보 검색
↓ (Augmented + Generation)
LLM이 근거 기반 답변 생성
```

---

## 4. Agent & Tool 이해하기

### 4-1. 🧠 Agent (에이전트)란?

**AI 에이전트**는 목표 달성을 위해 **계획을 세우고**, **도구를 사용하며**,  
**관찰 결과를 바탕으로 다음 행동을 결정하는 자율적인 시스템**입니다.  
LLM은 에이전트의 ‘두뇌’ 역할을 하며, 에이전트는 단순 대화 생성 이상의 능력을 가집니다.

#### 🧩 Agent의 핵심 구성 요소

1. **LLM (Large Language Model)**

   - 에이전트의 중심 지능.
   - 사용자 요청을 이해하고, 어떤 도구를 사용할지 판단하며 답변을 생성합니다.

2. **Planner (계획자)**

   - 복잡한 목표를 여러 단계(Sub-goal)로 나누고,  
     각 단계에서 어떤 도구를 사용할지 계획합니다.

3. **Tool (도구)**

   - 에이전트가 특정 작업을 수행하기 위해 사용하는 외부 기능.
   - 예: 검색 엔진, 계산기, 데이터베이스 조회 등

4. **Memory (메모리)**

   - 과거 대화나 작업 결과를 저장해,  
     이후 의사결정과 행동에 반영합니다.

5. **Observation (관찰)**
   - 도구 사용 후 얻은 결과를 해석하고,  
     다음 행동이나 계획 수정에 활용합니다.

#### 🚀 Agent를 사용하는 이유

1. **최신 정보 접근**

   - 학습 이후의 데이터는 LLM이 알 수 없지만,  
     Agent는 Tool을 통해 실시간 정보나 외부 DB에 접근할 수 있습니다.  
     → 📖 _RAG 개념의 확장판_

2. **환각(Hallucination) 감소**

   - 외부의 실제 정보원을 근거로 답변하므로 신뢰성이 향상됩니다.

3. **특정 작업 수행 및 자동화**

   - 계산, API 호출, 파일 조작 등 실제 작업을 수행할 수 있습니다.
   - 단순 Q&A를 넘어 실질적인 업무 자동화가 가능합니다.

4. **복잡한 문제 해결**

   - 문제를 여러 단계로 분리해 각 단계별 도구를 사용하며 추론합니다.

5. **외부 환경과의 상호작용**
   - 단순 텍스트 생성이 아닌,  
     외부 시스템(API, 데이터베이스 등)과 직접 상호작용이 가능합니다.

#### 🧭 요약

> **Agent = LLM + Planner + Tool + Memory + Observation**

LLM의 한계를 보완하여  
더 **정확하고 실용적인 문제 해결 능력**을 가진  
**지능형 시스템**을 만드는 개념입니다.

---

### 4-2. 🛠️ Tool (도구)란?

#### 정의

- 에이전트가 특정 작업을 수행하거나 외부 리소스에 접근하기 위해 사용하는 기능입니다.
- LangChain 등에서는 `@tool` 데코레이터를 사용해 **Python 함수**를 도구로 등록합니다.

#### 사용 이유

- LLM의 정보/기능 한계 극복
- 실시간 데이터 및 외부 리소스 활용
- 계산, 검색, API 호출 등 다양한 작업 수행으로 에이전트의 기능 확장

#### 🧩 예시

| 목적             | 예시 Tool          | 설명                               |
| ---------------- | ------------------ | ---------------------------------- |
| 실시간 정보 검색 | Google Search Tool | 최신 뉴스나 자료 검색              |
| 데이터 조회      | SQL Database Tool  | DB에서 조건에 맞는 데이터 가져오기 |
| 계산 수행        | Calculator Tool    | 수학 연산 처리                     |
| RAG 확장         | Retriever Tool     | Vector Store에서 관련 문서 검색    |

---

### ⚙️ 관계 요약

```yaml
사용자 질문
↓
LLM (판단 및 계획)
↓
필요한 Tool 선택 및 실행
↓
Observation (도구 결과 해석)
↓
최종 답변 생성
```
