# 📊 데이터 정규화 학습 요약

## 🎯 학습 목표

- 데이터의 특성을 이해하고 전처리하기
- 표준화(Z-score) 방법 익히기
- 선형 회귀 모델의 수학적 이해 (정규방정식, 최소제곱법, SVD)
- 경사 하강법 및 손실 함수 이해

---

## 🧾 키워드

- 표준화(Standardization), Z-score
- 정규방정식(Normal Equation)
- 특이값 분해(SVD)
- 최소 제곱법(Least Squares)
- 경사 하강법(Gradient Descent)
- 손실 함수(Loss Function)

---

## 📊 데이터 분석

| 함수         | 설명                                      |
| ------------ | ----------------------------------------- |
| `info()`     | 데이터 요약 (열, 타입, 결측치 등)         |
| `describe()` | count, mean, std, min, max 등 통계값 확인 |

```python
import seaborn as sns
dataframe = sns.load_dataset('dataset')
#info()
dataframe.info()
#describe()
dataframe.describe()
```

### 데이터 타입

- `float64`, `int64` 👉 **연속형 변수**
- `category` 👉 **범주형 변수**

---

## 🧪 데이터 분리

```python
# 연속형 변수만 추출
continuous_features = dataframe.select_dtypes(include=['number']).columns.tolist()

# 범주형 변수만 추출
categorical_features = dataframe.select_dtypes(include=['category']).columns.tolist()
```

---

### ⚖️ 표준화 (Z-score)

> 각 특성의 단위를 같게 만들어 학습 효율 향상

- `평균`은 `0` `분산`은 `1`로 만들어 줘야 함

| 용어            | 함수     | 설명                     |
| --------------- | -------- | ------------------------ |
| 평균 (Mean)     | `mean()` | 값을 0 기준으로          |
| 분산 (Variance) | `var()`  | 평균에서의 거리의 제곱   |
| 표준편차 (Std)  | `std()`  | 평균으로부터의 평균 거리 |
| Z-score         | 계산식   | `Z = (X - mean) / std`   |

#### 평균(Mean)

`mean()`

- 데이터를 표준화하기 위해 **평균**을 **0**으로 만드는 작업이 필요

```python
# continuous_features의 각 열 마다의 평균;
continuous_mean = coninuous_features.mean(axis=0)
```

> `axis=0` : 열 기준
> `axis=1` : 행 기준

---

#### 분산(Variance)

`var()`

> 데이터가 **평균**에서 얼마나 떨어져있는가의 제곱

```python
# continuous_features의 각 열 마다의 분산
continuous_variance = continuous_features.var(axis=0)
```

#### 표준편차(Standard Deviation)

`std()`

> 데이터가 **평균**으로부터 **평균적**으로 얼마나 퍼져있는가

```python
# continuous_features의 표준편차
continuous_stardard_deviation = continous_features.std(aix=0)
```

#### Z-score 계산

> 데이터가 **평균**으로부터 **표준편차**의 몇 배만큼 떨여져있는가

```python
z_score = (continuous_features - continous_mean) / continous_standard_deviation
```

---

## 정규 방정식

> 선형 회귀 해를 구하는 공식적 방법

✅ 왜 행렬?

- 컴퓨터는 숫자(행렬)만 이해함
- 대용량 데이터를 효율적으로 처리
- AI 연산은 행렬 기반

### 전치행렬(Transposed Matrix)

> 행의 데이터를 열로, 열의 데이터를 행으로 바꾼 행렬

- 필요한 이유?
  `직사각행렬`은 `역행렬`이 존재하지 않기 때문에, 원래의 행과 `전치 행렬`을 곱하여 `정사각행렬`로 변환하여 계산하기 위함
- X.T : 행 ↔ 열 변환 (예: X_with_bias.T)

```python
# X 행렬에 절편 계산을 위한 1 추가
import numpy as np
X = np.array([0.2, 0.4, 0.6]).reshape(-1, 1)
X_with_bias = np.c[X, np.ones(X.shape[0])]

#X_with_bias의 전치행렬
X_transposed = X_with.bias.T
```

### 정규방정식(Normal Equation)

- 행렬 곱
  `행렬 A @ 행렬 B`

```python
# 행렬 곱은 @로
XT_X = X_transposed @ X_with_bias
```

- 역행렬
  `np.linalg.inv(행렬)`

```python
XT_X_inverse = np.linalg.inv(XT_X)
```

### 특이행렬(Singular Matrix)

> 정사각행렬이지만, 역행렬이 없는 행렬을 말함

- 위 정규방정식에서 `XT_X`가 특이행렬이면 역행렬을 계산할 수 없음
- `행렬식`이 **0**이 되는 경우(또는 **0에 가까운** 경우)가 발생할 수 있기 때문에 **정규방정식의 한계**가 발생
  - 역행렬을 구하려고 하면 : `LinAlgError : Singular matrix`에러가 발생

**정규방정식의 한계를 해결하기 위해 최소 제곱법과 특이값 분해를 알아야 함**

---

## 🧩 최소 제곱법(Least Squares)과 특이값 분해(SVD)

### ⚠ 데이터 누수(Data Leakage)

- ex. `price`의 예측 값을 구하려면 `price`를 제외한 나머지 컬럼 값으로 구해야 함
- 예측 대상 컬럼(예: price)을 입력에 사용하면 모델이 '정답'을 보고 학습함
- 실제 환경에서는 예측 성능이 떨어짐

### 최소 제곱법(Least Squares)

> 실제 데이터와 예측한 데이터의 차이(잔차, Residual)를 제곱하여 더한 값(잔차 제곱합)을 말함

- 이 값을 최소화하는 값을 찾는 방법

- `lstq()`
  내부적으로 특이값 분해(SVD)를 통해 유사 역행렬을 계산하여 결과를 도출

```python
theta, residuals, rank, singular_values = np.linalg.lstsq(X_with_vias, y, recond=None)
```

### 🔍 특이값 분해(SVD)

`X=UΣVT`

> 임의의 행렬 X를 **3개의 행렬 곱**으로 분해 하는 방법

- 특이값 분해
  `np.linalg.svd()`

```python
U, S, Vt = np.linalg.svd(X_with_bias, full_matrices=False)
```

### 🔁 유사 역행렬(pseudo Inverse Matrix)

- 정규방정식의 한계(역행렬을 구할 수 없는 문제)에서 **가장 비슷한 정답**을 찾을 수 있도록 하는 도구
- `특이값 분해(SVD)`를 활용하여 역행렬이 존재하지 않더라도 역행렬을 구할 수 있는 방법

```python
# 시그마 행렬의 유사역행렬
S_plus = np.diag(1.0/S)
```

---

## 🧗 경사 하강법(Gradient Descent)

- 선형대수 기반의 정규방정식, 특이값 분해는 데이터 개수가 많아지면 메모리와 연산량이 증가하는 단점이 있음
- 경사 하강법은 다양한 방식으로 데이터를 나누어 처리하여 메모리를 효율적으로 사용할 수 있음

### 학습률(Learning Rate) 🔧

> **기울기가 0인 지점**을 찾아갈 때, **얼만큼 이동**하면서 찾아갈지에 대한 값

- f(x)에 대해 미분하면 f′(x)이고, 이것이 기울기임
- 학습률과 기울기를 이용하여 다음 위치로 이동

| 학습률 | 설명                                    |
| ------ | --------------------------------------- |
| 높음   | 빠르게 수렴, 과도하게 클 경우 발산 위험 |
| 낮음   | 천천히 수렴, 정확도 높음                |

- 학습률을 낮게 설정하면 찾아가는 단계의 수 많아짐(계산량 증가) 하지만 정확한 위치를 찾을 수 있음
- 시작 지점에 따라 기울기가 달라지기 때문에 찾아가는 단계의 수가 달라짐

* adan 방식

### 초개매변수(Hyperparameter) 🎛️

> 학습률 처럼 **사용자가 직접 설정하는 값**을 **하이퍼파라미터**라 부르며,
> 이 값을 잘 설정하여 최고의 모델을 만드는 과정을 **하이퍼파라미터 튜닝**이라고 함

### 💥 손실 함수(Loss Function)

> 모델의 예측값과 실제값 사이의 차이를 측정하는 함수

| 종류                      | 설명                   |
| ------------------------- | ---------------------- |
| MSE (Mean Squared Error)  | 잔차의 **제곱** 평균   |
| MAE (Mean Absolute Error) | 잔차의 **절댓값** 평균 |

> ✔ 어떤 손실 함수가 더 좋다기보단 데이터의 특성에 따라 다름
