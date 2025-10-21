# 📊 합성 데이터 학습 요약

## 📚 목차

---

## 1. 환경설정 및 관련 라이브러리

### 1-1. 환경 변수(OS Environment Variable)

- 운영체제가 프로그램 실행 환경에 제공하는 전역 설정값
- 프로그램은 이 환경 변수를 통해 중요한 정보(ex. API 키, DB 비밀번호)를 읽을 수 있음
- .env 파일을 불러서 환경변수로 설정함

#### 사용하는 이유?

1. 보안 :
   코드 안에 직접 비밀번호나 API Key를 적으면 키가 유출될 수도 있음, 환경 변수는 운영체제 메모리에만 저장되므로 코드 외부에서 완전하게 관리 가능
2. 유연성 :
   같은 코드라도, 환경 변수만 바꾸면 다른 API 키나 설정 사용 가능, 코드 수정 없이 환경 변수만 바꾸면 됨

---

### 1-2. httpx

- Python용 차세대 HTTP 클라이언트 라이브러리
- 유명한 requests 라이브러리의 API 스타일 계승
- **비동기(Async)**와 HTTP/2를 지원하도록 설계됨
- `async with httpx.AsyncClient() as client:`을 사용하여 RESET API 방식으로 비동기적 호출

#### 사용하는 이유?

> **비동기 통신**을 하기 위함

1. 네트워크 요청을 기다리는 동안 프로그램이 멈추지 않고 다른 작업을 병렬로 처리할 수 있음
2. 비동기 API 호출을 하게 되면 코드상에서 여러 API를 호출할 수 있음

```python
import httpx

async def call_chat_completion(url: str, headers: dict, payload: dict):
        async with httpx.AsyncClient(timeout=30.0) as client :
            response = await client.post(url, headers=headers, json=payload) # await! 응답 받아올 때까지 기다리자
            response.rasie_for_status()
            data = response.json()

            return data["choices"][0]["message"]["content"]
```

### 1-3. response_format - JSON

- 프롬프트에 결과물의 일관성을 유지하기 위해 output_format을 설정

  | 방식                                     | 설명                                        | 장점                          | 단점                        |
  | ---------------------------------------- | ------------------------------------------- | ----------------------------- | --------------------------- |
  | **프롬프트에 JSON 형식 지시**            | “이런 JSON 형태로 답해줘”라고 말로 지시     | 설정이 간단함                 | 형식 깨질 수 있어 파싱 필요 |
  | **`response_format`에 JSON schema 지정** | 모델이 지정된 구조의 JSON만 출력하도록 강제 | 결과가 안정적이고 파싱 불필요 | 초기 설정이 조금 복잡       |

#### 📌 정리:

> 모델이 JSON을 안정적으로 출력하길 원한다면 **response_format**을 쓰는 것이 좋음
> 빠르게 테스트할 때는 프롬프트 지시 방식도 가능

```python
from openai import OpenAI
import json

client = OpenAI()

messages = [
    {
        "role": "system",
        "content": "You are an expert in news information extraction. Extract key information and summarize the article."
    },
    {
        "role": "user",
        "content": """
        <article>
            <h1>Apple launches new iPhone 16 with AI features</h1>
            <p>Apple has unveiled the iPhone 16, featuring advanced AI-driven camera capabilities and improved battery life.
            The device will be available for pre-order on October 25, with prices starting at $999.</p>
            <p>CEO Tim Cook highlighted that this release marks a major step forward in integrating AI into everyday mobile experiences.</p>
        </article>
        """
    }
]

response_format = {
    "type": "json_schema",
    "json_schema": {
        "name": "news_article_summary",
        "strict": True,
        "schema": {
            "type": "object",
            "properties": {
                "headline": {
                    "type": "string",
                    "description": "The main title of the news article."
                },
                "summary": {
                    "type": "string",
                    "description": "A concise summary of the article content."
                },
                "published_date": {
                    "type": "string",
                    "description": "The date when the article or event is mentioned (if available)."
                },
                "price_start": {
                    "type": "number",
                    "description": "The starting price mentioned in the article, if any."
                },
                "key_points": {
                    "type": "array",
                    "items": {
                        "type": "string",
                        "description": "Main takeaways from the article."
                    }
                }
            },
            "required": ["headline", "summary", "key_points"]
        }
    }
}

response = client.chat.completions.create(
    model="gpt-4.1-mini",
    messages=messages,
    response_format=response_format
)

structured_output = response.choices[0].message.content
print("현재 응답의 형식:", type(structured_output))
print(structured_output)

structured_dict = json.loads(structured_output)
print("구조화된 응답의 형식:", type(structured_dict))
print(structured_dict)
```

#### 📘 결과 비교 정리

- `structured_output` :
  👉 모델이 반환한 JSON 형태의 텍스트(string)
  (따옴표로 감싸진 글자 덩어리)
- `structured_dictionary` :
  👉 그 텍스트를 json.loads()로 변환해서 얻은 실제 JSON 데이터 구조(dict)
  (이제 키로 접근 가능 — 예: structured_dictionary["headline"])

---

## 2. 프롬프트 엔지니어링(Prompt Engineering)

### 2-1. 프롬프트 엔지니어링

- AI 모델에게 원하는 답을 정확하고 효율적으로 이끌어내기 위해서 입력(프롬프트)을 설계-최적화하는 기술을 말함

#### 특징

1. 모델의 성능에 크게 영향을 줌
   - AI 모델은 질문(프롬프트)에 따라 같은 데이터라도 완전히 다른 답을 내놓음
2. 프롬프트의 기본 구조 하나의 프롬프트는 보통 다음 요소들로 구성됨
   - 역할(Role) 설정 : AI의 **_"정체성"_**과 **_"관점"_**을 지정
   - 목표(Task) 명확화 : 무엇을 해야 한느지 구체적으로 지시
   - 조건(Constraints) 부여 : 답변 길이, 형식, 언어, 스타일 등 제한사항을 명시
   - 예시(Examples) 제공 : 원하는 답변 패턴을 보여주면 AI가 비슷하게 답변
3. 좋은 프롬프트 작성 원칙 :
   - 명확성
   - 맥락 제공
   - 출력 형식 지정
   - 예시 제시
   - 점직전 요청(복잡한 문제는 단계별로 쪼개서 요청)

```python
RECIPE_SYSTEM_PROMPT = """당신은 전 세계 요리에 정통한 셰프 '쿠킹 마스터'입니다.
사용자의 요청에 따라 상황에 맞는 요리를 하나만 추천합니다.

{rule}

추천 시 반드시 요리 이름, 주요 재료, 추천 이유를 포함해야 합니다.
"""

# TODO: 아래 두 가지 규칙 중 하나를 선택하여 지시사항을 완성하세요.
# 규칙 A (전문 셰프 모드): 전문 요리 용어를 사용하고, 재료의 조화나 조리법의 과학적 원리를 함께 설명해주세요.
# 규칙 B (친구 모드): 편안하고 유쾌한 말투로, 왜 이 요리가 맛있는지 쉽게 설명해주세요.
rule = "규칙 B (친구 모드): 편안하고 유쾌한 말투로, 왜 이 요리가 맛있는지 쉽게 설명해주세요."

from openai import OpenAI

client = OpenAI(
    api_key=UPSTAGE_API_KEY,
    base_url="https://api.upstage.ai/v1"
)

response = client.chat.completions.create(
    model="solar-pro2",
    messages=[
        {
            "role": "system",
            "content": RECIPE_SYSTEM_PROMPT.format(rule=rule)
        },
        {
            "role": "user",
            "content": "오늘같이 비 오는 날 먹기 좋은 요리 추천해줘"
        }
    ]
)

output = response.choices[0].message.content
print(output)
```

#### 규칙 A 결과

```text
[21]
5초
# 규칙 B (친구 모드): 편안하고 유쾌한 말투로, 왜 이 요리가 맛있는지 쉽게 설명해주세요.
rule = "규칙 A (전문 셰프 모드): 전문 요리 용어를 사용하고, 재료의 조화나 조리법의 과학적 원리를 함께 설명해주세요."

from openai import OpenAI

client = OpenAI(
    api_key=UPSTAGE_API_KEY,
    base_url="https://api.upstage.ai/v1"
)


**요리명: 오리 콩피(Canard à la Confit)와 레드 와인 감자 퓌레**

### **주요 재료**
- **오리 다리** (피부 포함, 지방과 젤라틴이 풍부한 부위)
- **오리 지방** (자체 추출 또는 거위 지방 대체)
- **허브 믹스** (로즈마리, 타임, 세이지, 월계수 잎)
- **마늘** (통마늘, 캐러멜라이징 효과)
- **레드 와인 감자 퓌레** (감자, 생크림, 레드 와인 리덕션)
- **양파, 당근** (브라즈링용, 미렌포아)

---

### **추천 이유 (과학적/요리학적 근거)**
1. **비 오는 날과 오리 콩피의 시너지**
   - 오리 지방은 **저온 장시간 조리(65~75°C)**로 분해되며, **올레오산**이 풍부해 입안에 부드러운 감촉을 남깁니다. 이는 습도 높은 날 우울감을 완화하는 **촉각적 만족감**을 제공합니다.
   - 레드와인 감자 퓌레의 **폴리페놀**과 오리 고기의 **철분**은 체내 열생성을 촉진해 체온 유지에 도움을 줍니다.

2. **조리법의 화학적 원리**
   - 오리를 지방에 **진공 조리(Sous-vide)**하면 **미오신 단백질**이 서서히 변성되어 연도가 극대화됩니다.
   - 레드 와인 리덕션은 **당-아미노산 반응(메일라드 반응)**으로 깊은 우마미를 생성하며, 감자의 **아밀로펙틴**과 결합해 크리미한 텍스처를 완성합니다.

3. **향미 조화의 심리학**
   - 오리의 **풍부한 지방산**과 허브의 **테르펜 화합물**은 비 냄새(페트리코 화합물)와 상쇄되는 향미를 제공합니다.
   - 따뜻한 요리에서 발생하는 **수증기**가 후각 수용체를 자극해 식욕을 촉진합니다.

---

**추가 TIP**
- 오리 콩피는 **24시간 숙성**하면 근육 내 **칼팩신 효소**가 단백질을 추가로 분해해 더 부드러워집니다.
- 감자 퓌레에 **카옌 페퍼** 한 꼬집을 첨가하면 미량의 **캡사이신**이 체온을 일시적으로 올려 쾌적함을 유지합니다.

비 오는 날의 무기력함을 타파할 **풍미 가득한 프랑스 남부식 요리**로 추천합니다. 🌧️🍷
```

#### 규칙 B 결과

```text
**추천 요리: 감자전**

**주요 재료:**
감자, 부침가루, 소금, 파, 식용유

**추천 이유:**
오늘은 비 오는 날이니까~ **감자전**처럼 바삭하고 따끈한 요리가 딱이에요! 🌧️🥟
- 감자는 비 오는 날 허한 속을 채워주는 든든한 탄수화물 친구랍니다.
- 얇게 부쳐서 바삭한 식감과 쫄깃한 속살이 조화되어, 비 냄새와 어울리는 고소함을 선사해요.
- 파를 송송 썰어 넣으면 향긋함이 더해져서 한 장씩 집어먹기 좋아요.
- 게다가 만드는 법도 간단해서, 비 올 때 집에서 편하게 만들기 딱이죠!

"비 오는 날에는 감자전 한 장, 김치랑 같이 싸먹으면 세상 행복해져요!" ☔️😊
```

#### 📘 결과 비교 정리

> 규칙에 따라 말투와 답변의 퀄리티, 내용 등이 달라지는 것을 볼 수 있음

---

### 2-2. 합성 데이터(Synthetic Data)

- 실제 세상에서 수집한 데이터가 아니라, 인공적으로 만들어낸 데이터
- 합성 데이터는 실제 데이터와 유사하게 만들어야 함
- 합성 데이터를 무수히 만들더라도 비슷한 데이터만 많이 만들면 학습의 효율성이 떨어짐
  -> 다양성을 높여주는 `temperature`값을 조절함(`temperature=0.0`이면 다양성이 매우 낮은 것을 의미)
  -> 중복되는 데이터 등 불필요한 데이터가 생길 수 있으므로 필터링 함수 추가로 사용

#### 목적

1. 데이터 부족 문제 해결
   - 새로운 AI 모델을 만들 때, 필요한 데이터가 부족할 수 있음
2. 비용 절감
   - 실제 데이터를 수집-가공하는 데는 많은 시간과 돈이 듦
   - 합성 데이터는 컴퓨터로 빠르고 저렴하게 생성 가능
3. 개인정보 보호
   - 의료 기록, 금융 거래처럼 민감한 정보는 직접 사용하기 어렵
   - 합성 데이터는 실제 사람의 개인정보를 포함하지 않으면서도, 통계적으로 비슷한 특성을 가질 수 있음
4. 다양한 상황 테스트
   - 실제로는 드물게 발생하는 사건을 AI가 학습할 수 있도록 미리 만들어 학습-테스트 가능

#### 주의

> 원본 데이터의 패턴을 참고해 비슷한 구조의 가짜 데이터(합성 데이터)’를 만들어 달라고 프롬프트에서 요청하는 방식으로 생성
> 즉, 그냥 "원래 데이터로 합성 데이터 만들어줘"라고만 하면 안 되고
> **“패턴을 참고하되 실제 데이터는 포함하지 말라”**고 명확히 적어야 함

```python
# TODO: SYSTEM PROMPT를 작성하세요
SYNTHETIC_DATA_SYSTEM_PROMPT = """
You are a structured synthetic data generator.
Your task is to always output JSON strictly following this schema:
{{
  "intent": "영화 리뷰 생성",
  "items": [
    {{
      "title": "<영화 제목>",
      "description": "<짧은 리뷰 내용>",
      "additional_info": "<선택적 추가 정보 (예: 평점, 장르 등)>"
    }}
  ]
}}

{rule}
- Never include text outside of JSON.
- Always provide at least 5 items.
- Ensure valid JSON syntax.
- The data must be synthetic (not copied from real examples).
- Follow the structure and style of typical movie reviews.
"""

rule = "Generate synthetic movie review data with fictional movie titles and reviews."

response = client.chat.completions.create(
    model="solar-pro2",
    messages=[
        {
            "role": "system",
            "content": SYNTHETIC_DATA_SYSTEM_PROMPT.format(rule=rule)
        },
        {
            "role": "user",
            "content": "Please create 5 synthetic movie reviews following the given structure."
        }
    ],
    temperature=1.0,
)

output = response.choices[0].message.content

# JSON 문자열을 파이썬 객체로 변환
structured_dictionary = json.loads(output)

print("구조화된 응답의 형식: ", type(structured_dictionary))
print(json.dumps(structured_dictionary, indent=2))
```

---

### 2-3. 합성 데이터 평가(LLM as a Judge)

- 사람 대신 LLM 모델을 이용하여 정석적인 결과물을 평가하는 것

#### 사용 이유

- 자동 채점 : 사람이 직접 채점하지 않아도 LLM이 먼저 평가를 도와줄 수 있음
- 피드백 제공 : 단순히 점수만 제공하는 것이 아니라 설명까지 덧붙일 수 있음
- 일관성 있는 평가 : 빠르고 일관성 있게 답을 평가할 수 있음

#### 사용 원칙

> LLM도 완벽하지 않아서 잘못된 평가를 할 수 있음! 따라서 다양한 원칙을 지켜야 함

1. 평가자 모델은 추론에 사용한 모델보다 더 큰 모델을 사용해야 함
2. 추론에 사용한 모델과 평가자 모델이 같을 경우 호의적인 평가를 내리는 bias가 발생하므로 다른 모델로 사용해야 함
3. 평가 결과물은 점수(score)와 이유(reason)로 구성됨, reason을 생성하게 함으로써 모델이 좀더 정확한 평가를 하게 만듦
4. 평가 결과물이 일관되도록 temperature를 0으로 설정합니다.

````python
# 자동 평가용 시스템 프롬프트 (음식 추천 버전)
JUDGE_SYSTEM_PROMPT_FOOD = """당신의 역할은 모델 답변 자동 평가자입니다. 입력 프롬프트와 모델 답변을 보고, 평가 기준에 따라 모델 답변을 평가합니다.

## 1. 입력 형식
    - 입력 프롬프트: [instruction]
    - 모델 답변: [output]
    - 평가 기준: [criteria]

## 2. 작업 지시
    - [instruction]에 따른 모델 결과물인 [output]을 평가합니다.
    - [output]은 [criteria]를 충족하는지 평가합니다.

## 3. 채점 원칙 (각 기준별 1–5점, 정수만)
    - 5점 (탁월): 기준을 완전히 충족. 오류·누락 없음. 구체적이고 실행가능.
    - 4점 (우수): 대체로 충족. 사소한 흠만 있음.
    - 3점 (보통): 핵심은 맞지만 눈에 띄는 약점 존재.
    - 2점 (미흡): 중요한 요구를 여러 곳에서 놓침 또는 오류/비논리 다수.
    - 1점 (부적합): 전반적으로 요청과 어긋남, 의미있는 도움/근거 없음.

## 3. 출력 형식 (엄격 준수)
    - "score"는 1–5점의 정수로 평가한다.
    - "comment"는 한국어 1–3문장으로 평가한다. 구체적이고 실행 가능하게 작성한다.
    - 출력 형식은 JSON 형식인 <output_format>을 준수한다.

<output_format>
```json
{{
    "score": [모델의 답변 평가 점수],
    "comment": [평가 주석]
}}
```"""
USER_PROMPT = """- 입력 프롬프트: {instruction}
- 모델 답변: {output}
- 평가 기준: {criteria}
"""

instruction = STRUCTURED_GENERATOR_SYSTEM_PROMPT.format(rule=rule)
# TODO: 기준을 정의해주세요
criteria = "요청의 충실도"

print(USER_PROMPT.format(instruction=instruction, output=output, criteria=criteria))

messages = [
        {
            "role": "system",
            "content": JUDGE_SYSTEM_PROMPT
        },
        {
            "role": "user",
            "content": USER_PROMPT.format(instruction=instruction, output=output, criteria=criteria)
        }
]

response = client.chat.completions.create(
    model="solar-pro2",
    messages=messages
)

llm_as_judge_output = response.choices[0].message.content
llm_as_judge_structured_dictionary = json_parsing(llm_as_judge_output)
print("구조화된 응답의 형식: ", type(llm_as_judge_structured_dictionary))
print(llm_as_judge_structured_dictionary)
````
