# 📊 탐색적 데이터 분석 학습 내용 요약

## 🎯 학습 목표

- 데이터의 구조와 특성을 이해하고, 시각화 및 통계적 요약을 통해 패턴을 파악
- **이상치·결측치·상관관계** 등을 탐색하여 모델링 전 데이터 품질 개선
- 전처리부터 평가까지 전체 **파이프라인**을 체계적으로 구축

---

## 🧾 키워드

- EDA(Exploratory Data Analysis)
- 상관관계(Correlation)
- 이상치(Outlier)
- 결측치(Missing Value)
- 파이프라인(Pipeline)
- 표준화(Standardization)
- 교차 검증(Cross Validation)

---

## 📚 목차

1. [탐색적 데이터 분석(EDA)](#1-탐색적-데이터-분석eda-exploratory-data-analysis)
2. [상관관계 분석](#2-상관관계-분석correlation-analysis)
3. [이상치 처리](#3-이상치outlier-처리)
4. [결측치 처리](#4-결측치missing-value)
5. [파이프라인](#5-파이프라인pipeline)

---

## 1. 탐색적 데이터 분석(EDA, Exploratory Data Analysis)

> 데이터의 특성과 패턴을 이해하기 위해 **시각화와 통계적 요약을 통해 데이터를 탐색**하는 과정

- 모델링 전 단계에서 데이터의 품질과 문제점을 확인
- **구조·크기·결측치·이상치·분포 특성** 등을 파악하여 전처리 방향을 설정

### 1-1. Quality 특성 분석

#### 단일 특성 분석

| 분석 항목   | 코드 예시                           | 설명                   |
| ----------- | ----------------------------------- | ---------------------- |
| 특성 추출   | `pd.Series(car_dataframe['price'])` | 개별 변수(Series) 추출 |
| 클래스 개수 | `.nunique()`                        | 고유값의 개수 확인     |
| 클래스 분포 | `.value_counts().sort_index()`      | 각 값의 빈도 정렬 확인 |

#### 다중 특성 분석

- 여러 변수 간의 관계를 확인하기 위해 `describe()`, `corr()`, `pairplot()` 등을 활용
- 예: `sns.pairplot(car_dataframe)` → 변수 간 분포 및 상관 관계 시각화

---

## 2. 상관관계 분석(Correlation Analysis)

> 두 특성 간의 **선형 관계**를 수치와 시각화로 파악

- **타겟 변수와의 관계**를 파악하여 모델링에 유의미한 특성 선택
- **상관관계 ≠ 인과관계**
  - 상관관계는 단순한 수학적 관계이며, 인과관계를 의미하지 않음
  - ⚠️ **혼재변수(confounding variable)** 존재 가능

### 2-1. 피어슨 상관계수(Pearson Correlation Coefficient)

| 구분      | 설명                   |
| --------- | ---------------------- |
| 값의 범위 | `-1 ~ 1`               |
| +1 근처   | 강한 **양의 선형관계** |
| -1 근처   | 강한 **음의 선형관계** |
| 0 근처    | 거의 **무상관 관계**   |

### 2-2. 상관계수 행렬

> 여러 변수 간의 선형 관계 정도를 한눈에 파악할 수 있도록 상관 계수를 **행렬 형태**로 시각화한 것

- 주대각선(diagonal): 자기 자신과의 상관 → 항상 1
- 주대각선을 기준으로 한쪽의 데이터만 필요하다(대칭)
- 불필요한 데이터를 지우기 위해 **상삼각행렬(mask)**을 이용함

---

## 3. 이상치(Outlier) 처리

> 다른 데이터와 현저히 다른 값 (오류이거나 극단적인 실제값)
> 측정 오류, 입력 실수이거나 실제로 측정된 값이지만 극단적인 경우를 말함

- 데이터 품질 향상 및 모델 성능 저하 방지를 위해 처리 필요
- 학습 데이터에 극단값 추가 실습을 통해 이상치 탐지 기법을 익힘

### 3-1. 사분위수 범위(IQR, Interquartile Range)

> 1사분위수와 3사분위수를 활용하여 범위를 벗어난 데이터를 이상치로 판단하는 방법
> `IQR = Q3 = Q1`

- 이상치 판단 기준 :

  - Q1 - 1.5 \* IQR 미만
  - Q3 + 1.5 \* IQR 초과

- boxplot를 이용하여 이상치 확인
  ![Boxplot 설명](./img/ai/boxplot_explanation.png)
  | 구성 요소 | 의미 |
  | ----- | --------- |
  | 박스 하단 | Q1 |
  | 박스 중앙 | Q2 (중앙값) |
  | 박스 상단 | Q3 |
  | 박스 높이 | IQR |
  | 선 | 1.5 × IQR |
  | 점 | 이상치 |

### 3-2. Z-Score

> 데이터가 평균으로보터 **표준편차의 몇 배만큼 떨어져 있는지** 측정

- Z-Score의 절댓값이 3 초과 → 이상치로 간주

```python
from scipy import stats
z = np.abs(stats.zscore(df['price']))
outliers = df[z > 3]
```

---

## 4. 결측치(Missing Value)

> 데이터가 누락된 경우 (기록 오류, 수집 실패 등)

- 결측치가 많으면 모델 학습에 심각한 영향을 줌 → 적절한 대체 필요

### 4-1. 결측치 대체

| 데이터 유형 | 처리 방법                          | 예시                                          |
| ----------- | ---------------------------------- | --------------------------------------------- |
| 연속형      | 평균(`mean()`), 중앙값(`median()`) | `df['age'].fillna(df['age'].mean())`          |
| 범주형      | 최빈값(`mode()[0]`)                | `df['gender'].fillna(df['gender'].mode()[0])` |

### 4-2. SimpleImputer

#### 3. SimpleImputer

> `sklearn` 라이브러리에서 제공하는 메서드로 결측치를 자동으로 대체해줌

```python
from sklearn.impute import SimpleImputer
imputer = SimpleImputer(strategy='median')
X = imputer.fit_transform(df)
```

---

## 5. 파이프라인(Pipeline)

> 데이터 **전처리 → 학습 → 평가**의 전체 과정을 일관되게 관리하는 흐름

- `sklearn` 라이브러리를 통해 파이프라인을 쉽게 구현할 수 있음

## 5-1. 데이터 분할(Train/Test Split)

> 학습용과 테스트용 데이터를 나누어 **일반화 성능 평가**

```python
# 평가용(Test) 데이터의 비율을 40%로 설정하여 데이터를 분리
X_train, X_test, y_train, y_test = train_test_split(
    X,y,
    test_size=0.4,      # 전체 데이터 중 40%를 테스트용으로
    random_state=42,    # 재현 가능한 결과를 위해 난수 시드 고정
    stratify=y          # 클래스 비율을 유지하며 분할
)
```

> `stratify=y`: 클래스 비율을 유지하도록 분할
> 이를 사용하지 않으면 한쪽 데이터셋에 특정 클래스만 몰릴 수 있음

## 5-2. 데이터 표준화(Standardization)

> 평균을 0, 표준편차를 1로 맞추어 학습 안정화

```python
# StandardScaler를 이요하여 데이터 표준화
scaler = StandardScaler()
scaler.fit(X_train)

# Train과 Test 데이터 모두 변환
X_train_norm = scaler.transform(X_trian)
X_test_norm = scaler.transform(X_test)

# X_train_norm = scaler.fit_transform(X_train)

```

## 5-3. 모델 학습(Training)

> `LogisticRegression`은 확률을 기반으로 분류하는 모델

```python
# LogisticRegression 모델 선언
from sklearn.linear_model import LogisticRegression
model =  LogisticRegression(max_iter=1000)

# 모델 학습
model.fit(X_train_norm, y_train)

# 모델 예측
y_pred = model.predict(X_test_norm)
print(y_pred)
print(y_test)
```

## 5-4. 모델 평가(Evaluation)

- 다양한 방법으로 평가 진행, 혼동 행렬, 분류 리포트, ROC-AUC 곡선을 이용하여 평가
  | 평가 지표 | 의미 |
  | -------------------- | ------------------------ |
  | Accuracy | 전체 예측 중 맞춘 비율 |
  | Precision | 양성 예측 중 실제 양성 비율 |
  | Recall (Sensitivity) | 실제 양성 중 맞춘 비율 |
  | F1-score | Precision과 Recall의 조화 평균 |

### 1️⃣ 혼동 행렬(Confusion Matrix)

> 정밀도, 재현율을 계산하기 위한 행렬

| 구분 | 긍정      | 부정                      |                         |
| ---- | --------- | ------------------------- | ----------------------- |
| 긍정 | TP        | FN                        | Sensitivity 또는 Recall |
| 부정 | FP        | TN                        | Specificity             |
|      | Precision | Negative Predictive Value | Accuracy                |

- 혼동 행렬 계산

```python
from sklearn.metrics import confusion_matrix

CM = confusion_matrix(y_test, y_pred)
print(CM)
```

### 2️⃣ 분류 리포트(Classification Report)

> 정밀도, 재현율, F1-score 등 여러 지표를 요약해서 보여주는 표

```python
from sklearn.metrics import classification_report
print(classificatinon_report(y_test, y_pred))
```

### 3️⃣ ROC-AUC

> **TPR(민감도)**와 **FPR(오탐율)** 관계를 나타내는 곡선

- **AUC**는 면적값 (1에 가까울수록 우수)
- 0.5는 무작위 분류기 수준을 의미

```python
from sklearn.metrics import roc_auc_score, roc_curve
roc_auc_score(y_test, y_pred)
```

### 4️⃣ 교차 검증(Cross Validation)

> 여러 폴드로 나누어 반복적으로 학습 및 평가
> 모델의 일반화 성능을 높이는 데 사용

```python
from sklearn.model_selection import cross_val_score
scores = cross_val_score(model, X_train_norm, y_train, cv=5)
print(scores.mean())
```

---

## ✅ 정리 요약

| 구분       | 주요 목적           | 대표 기법                                        |
| ---------- | ------------------- | ------------------------------------------------ |
| 탐색(EDA)  | 데이터 구조 이해    | `describe()`, `pairplot()`                       |
| 상관관계   | 변수 간 관계 확인   | `corr()`, `heatmap()`                            |
| 이상치     | 극단값 탐지 및 처리 | `IQR`, `Z-Score`, `boxplot()`                    |
| 결측치     | 누락값 보정         | `mean`, `median`, `SimpleImputer`                |
| 파이프라인 | 전체 프로세스 관리  | `train_test_split`, `StandardScaler`, `Pipeline` |
