# 📊 이미지 생성 학습 요약

## 📚 목차

---

## 1. Positive/Negative 프롬프팅 기법

## 1-1. Stable Diffusion

> 텍스트 프롬프트를 입력받아 이미지를 생성하는 Text-to-Image 생성형 모델

### 1-2. 이미지 생성 절차

0. 노이즈 가득한 랜덤 이미지에서 시작
1. 텍스트 조건에 맞게 점진적으로 노이즈를 제거(diffusion process)
2. 최종적으로 프롬프트와 일치하는 이미지 생성
   - HuggingFace의 Diffusers 라이브러리를 사용하여, Stable Diffusion 파이프라인 로드
   - 원하는 이미지 핵심 컨셉을 정하고 이를 표현하는 프롬포트 작성
   - 이미지에서 배제하고 싶은 요소들은 **네거티브 프롬프트**로 지정(ex. 낮은 해상도, 블러처리, 왜곡된 형태 등의 키워드를 넣어 이미지 개선)

### 1-3. 주요 코드 - 프롬프트를 사용하여 이미지 생성

#### 파이프라인 객체 생성

```python
from diffusers import Stable DiffusionPipeline
import torch

# StableDiffusionPipline을 호출하여 pipeline 객체 생성
'''
- from_pretrained 메서드 : 미리 학습된 모델을 다운로드하고 로드
- torch_dtype=torch.float16 : 모델의 가중치를 16비트 부동소수점 형식으로 로드
    -> 메모리 사용량 줄이고 계산 속도를 높이(GPU에서 효과적)
'''
pipe = StableDiffusionPipeline.from_pretrained(
    model_id, torch_dtype=torch.float16
)
```

#### 프롬프트 설정

```python
'''
이미지 생성을 위한 프롬프트 설정
- positive_prompt : 생성하고 싶은 이미지 상세하게 기술
- negative_prompt : 생성할 이미지에 나타나지 않았으면 하는 요소들 기술
'''
positive_prompt = "A digital art of a snowy owl flying over a moonlit forest, detailed feathers, soft glowing light"

negative_promt = "low quality, blurry, distorted, text"

# pipe(파이프라인 객체) 호출하여 이미지 생성, 이미지 2장 생성, 결과 PIL 이미지 리스트로 변환
result = pipe(
    positive_prompt,
    negative_prompt=negative_prompt,
    # 생성된 이미지가 프롬프트를 얼마나 강하게 따를지 결정하는 값
    guidance_scale=7.5
    # 노이즈를 제거하는 단계 수
    num_inference_steps=50,
    # 한 번의 요청으로 생성할 이미지의 개수
    num_imges_per_prompt=2,
)
```

---

## 2. CLIP_Image-Test 유사도

### 2-1. CLIP

> 이미지와 텍스트를 같은 임베딩 공간에 투영하여 유사도를 계산하는 모델

#### 이미지 유사도 계산 과정

1. 생성한 이미지의 내용에 맞는 정답 레이블 한 개, 모델이 혼동할 법한 오답 레이블 몇 개를 정함
2. CLIP 모델에 이미지를 입력하고 정답/오답 텍스트 레이블들을 입력하면 이미지와 각 텍스트 간의 유사도 점수(dot product기반)를 얻을 수 있음
   -> 점수 비교하여 가장 높은 것이 모델이 예측한 레이블이 됨
   -> 별도의 학습 없이도 이미지가 어떤 텍스트 설명과 가장 어울리는지 알 수 있어, 생성된 이미지의 내용이 의도와 맞는지 평가할 수 있음

---

### 2-2. 주요 코드-이미지 유사도 비교

#### 정답/오답 레이블 설정

```python
labels = [
    "a watercolor painting of an owl sitting on a tree branch",   # 정답 (의도한 레이블)
    "a watercolor painting of a hawk sitting on a tree branch",   # 오답 1: 동물이 다름
    "an oil painting of an owl sitting on a tree branch",         # 오답 2: 화풍이 다름
    "a photo of an owl sitting on a tree branch"                  # 오답 3: 매체(스타일)가 다름
]
```

#### 이미지와 텍스트 전처리기

```python
# CLIPProcessor : 입력으로 넣을 이미지와 텍스트를 모델이 이해할 수 있는 숫자 형태(텐서)로 변환하는 역할
processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")

# CLIPModel : 실제 이미지와 텍스트의 특징을 추출하고 유사도를 계산하는 모델
model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32").to(device)

# 이미지와 텍스트 레이블 전처리
'''
return_tensors="pt" : 결과를 파이토치 텐서로 반환하라는 의미
padding=True : 문장들의 길이를 맞추기 위해 짧은 문장 뒤에 특수 토큰 추가
'''
inputs = processor(text=labels, images=image, return_tensors="pt", padding=True).to(device)
```

#### 이미지-텍스트 유사도 계산

```python
# torch.no_grad() : 모델의 가중치를 업데이트하지 않도록 하여(학습이 아님) 순수 추론만 수행
with torch.no_grad():
    # 모델에 전처리된 데이터 입력; **inputs는 딕셔너리 형태의 입력을 풀어 전달
    ouputs = model(**inputs)
    # 이미지 한 장당 각 텍스트 레이블과의 유사도 점수를 담고 있음
    logits_per_image = outputs.logits_per_image
    # 유사도 점수를 확률 값으로 변환, 모든 확률의 합은 1이됨
    probs = logits_per_image.softmax(dim=1)
```

## 3. ResNet-50\_이미지 분류

### 3-1. CLIP vs ResNet-50 비교 요약

| 구분               | **CLIP**                                                         | **ResNet-50**                                           |
| ------------------ | ---------------------------------------------------------------- | ------------------------------------------------------- |
| **학습 데이터**    | 이미지 + 텍스트 쌍                                               | ImageNet (1000개 클래스)                                |
| **출력 형태**      | 입력 텍스트(프롬프트)와의 유사도                                 | 1000개 고정 클래스 중 하나                              |
| **입력 라벨 정의** | 사용자가 임의로 정의 가능 (예: “a watercolor painting of a fox”) | 사전 정의된 1000개 클래스만 사용                        |
| **인식 방식**      | 텍스트 의미와 이미지 의미를 매칭 (문맥적 이해 가능)              | 픽셀 패턴을 기반으로 사물 인식 (문맥 이해 불가)         |
| **스타일 구분**    | 가능 (예: 수채화, 유화, 사진 등 구분함)                          | 불가능 (화풍과 상관없이 객체 중심)                      |
| **예시 결과**      | “수채화 여우 그림” → ✅ 정답 선택                                | “red fox”, “kit fox” 등 → ✅ 여우지만 화풍은 반영 안 됨 |
| **장점**           | 유연하고 문맥적인 판단 가능                                      | 고정된 클래스 내에서 정확한 객체 인식                   |
| **한계**           | 텍스트 표현에 따라 결과가 달라짐                                 | 스타일, 상황, 문맥 이해 불가                            |

### 3-2. 이미지 분류 과정

0. ImageNet의 클래스 이름 매핑을 위해 미리 준비된 레이블 리스트 사용
1. 데이터를 준비된 ResNet-50 모델에 맞게 변형 -> `torchvistion.transforms` 모듈 사용하여 데이터 전처리
   1-1. 리사이즈 : 데이터를 RestNet-50 입력 크기에 맞게 244x244으로 조정
   1-2. 정규화 : 데이터를 모델이 학습했던 데이터에 맞게 정규화
   (모델이 학습 ImageNet의 평균 : [0.485, 0.456, 0.406], 표준편차 : [0.229, 0.224, 0.225])
2. 준비된 데이터로 ResNet-50을 통해 입력 이미지에 대해 가장 높은 확률의 클래스를 예측

### 3-3. 주요 코드-이미지 분류

#### 사전 학습된 RestNet-50 모델 로드 & 모델을 평가 모드로 설정

```python
# weights=models.ResNet50_Weights.IMAGENET1K_V2: ImageNet 데이터셋으로 학습된 가중치를 함께 로드
resnet50 = models.resnet50(weights=models.ResNet50_Weights.IMAGENET1K_V2).to(device)

# .eval(): 모델을 평가(evaluation) 모드로 설정
resnet50.eval()
```

#### 이미지 전처리
