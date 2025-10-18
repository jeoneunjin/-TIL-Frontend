# 📊 역전파 학습 정리

## 📚 목차

1. [역전파](#1-역전파)  
2. [단순 선형회귀 모델 학습](#2-단순-선형회귀-모델-학습)  
3. [경사하강법](#3-경사하강법)  

---

## 1. 역전파

### 🔹 순전파 (Forward Pass)
- 초기 입력값 → 각 층의 가중치($w$, $b$)와 연산 → 활성함수 통과 → 최종 출력
- **목적:** 예측값 계산 후 실제 레이블과 비교하여 오차 계산

### 🔹 역전파 (Backpropagation)
- 순전파 과정 **역행**
- **목적:** 오차 기반으로 가중치($w$)와 편향($b$) 업데이트
- **방법:** 연쇄 법칙(Chain Rule) + 경사하강법(Gradient Descent)

#### 🧮 수식 예시
$$
y = w \cdot x + b
$$
- **MSE 사용 이유:** MAE는 미분 불가 지점 존재, 최적화 어려움

```python
# 초기값 x=2, y=4, w=3, b=1
x = torch.tensor([2,0])
y = torch.tensor([4,0])

weight  = torch.tensor([3,0], requires_grad=True) # w
b = torch.tensor([1,0], requires_grad=True) # b

print(weight, b)
```

### 🔹 AutoGrad

  > `.backward()` 메서드를 호출하면 그래프를 따라 자동으로 역전파를 수행하여 기울기를 구할 수 있음

- **requires_grad**
  > **"지금 변수를 미분할 수 있게 해줘"**라고 알려주는 속성
  > 모델의 가중치, 편향 또는 업데이트가 필요한 변수들에 `True`로 설정하여 사용할 수 있음

### 🔹 손실 계산 (Loss)

> MSE(Mean Squared Error)로 실제 값($y$)과 예측값($\hat{y}$)의 손실을 계산

```python
loss = (y - y_pred)**2
print(loss.item())
```

### 🔹 역전파 계산

- $w$ 에 대한 편미분 :

    $\frac{\partial L}{\partial w} = \frac{\partial L}{\partial \hat{y}} \times \frac{\partial \hat{y}}{\partial w}$

- $b$ 에 대한 편미분 :

    $\frac{\partial L}{\partial b} = \frac{\partial L}{\partial \hat{y}} \times \frac{\partial \hat{y}}{\partial b}$

#### 계산 과정

> 초기값 $x=2$, $y=4$, $w=2$, $b=1$

1.  $\frac{\partial \hat{y}}{\partial w} = x$
    계산

    > 손실 함수(MSE)를 $\hat{y}$ 에 대해 미분
    > $y$−$\hat{y}$ 를 $u$ 로 치환하면 $L=\{u}^2$

2.  $\frac{\partial \hat{y}}{\partial b} = 1$
    계산

    > $\hat{y} = w x + b$ 를 $w$ 에 대해 미분

3.  $\frac{\partial \hat{y}}{\partial b}$ 계산

    > $\hat{y} = w x + b$ 를 $\b$에 대해 미분

4.  최종 기울기 계산
   
    $\frac{\partial L}{\partial w} = \frac{\partial L}{\partial \hat{y}} \times \frac{\partial \hat{y}}{\partial w} = 6 \times 2 = 12$
    
    $\frac{\partial L}{\partial b} = \frac{\partial L}{\partial \hat{y}} \times \frac{\partial \hat{y}}{\partial b} = 6 \times 1 = 6$

####  코드 수행

```python
loss.backward()

print(f"∂L/∂w: {weight.grad.item()}")
print(f"∂L/∂b: {b.grad.item()}")
```

### 🔹 파라미터 업데이트

> 학습률 $\alpha = 0.1$을 사용하여 파라미터를 업데이트

- $w$ 갱신:  
    $w_{\text{new}} = w - \alpha \frac{\partial L}{\partial w} = 3 - 0.1 \times 12 = 1.8$

- $b$ 갱신:  
    $b_{\text{new}} = b - \alpha \frac{\partial L}{\partial b} = 1 - 0.1 \times 6 = 0.4$

```python
# 학습률 0.1, 파라미터 업데이트
l_rate = 0.1
with torch.no_grad():
    weight -= learning_rate * weight.grad
    b -= learning_rate * b.grad

    weight.grad.zero_()
    b.grad.zero_()

print(weight.item())
print(b.item())
```

#### 코드 수행

```python
learning_rate = 0.1

with torch.no_grad(): #업데이트 시 추적 비활성화
    weight -= learning_rate * weight.grad
    b -= learning_rate * b.grad

    weight.grad.zero_()
    b.grad.zero_()

print(weight.item())
print(b.item())
```

- **no_grad()**
  > - **기울기를 추적하지 않도록 함**
  > - 현재 상태에서의 기울기를 구한 값을 활용해야하기 때문에 이 메서드를 사용하지 않으면
  >   다음 기울기에 **누적**하여 잘못된 값을 도출하게 됨

#### 검증

- 새롭게 업데이트된 파라미터를 기반으로 다시 계산 진행(손실 계산 후 오차 계산)

---

## 2. 단순 선형회귀 모델 학습

> 여러번 역전파를 수행하면서 적절한 결과를 얻어야 함

```python
epochs = 200
# epoch 마다 역전파를 수행
for epoch in range(epochs):

    optimizer.zero_grad() # 미분값 초기화 다음 미분 계산에 영향을 주지 않도록

    y_prediction = model(X) # 예측 수행

    loss = MSE_loss(y_prediction, y) # 손실 계산

    loss.backward() # 새로운 기울기 계산

    optimizer.step() # 새로운 기울기로 업데이트

    # 20번 수행마다 loss 출력
    if (epoch + 1) % 20 == 0:
        print(f"Epoch [{epoch+1}/{epochs}], Loss: {loss.item():.4f}")
```

- model(X)
  > `model`은 `torch.bb.Linear`의 인스턴스

```python
# 입력 차원 1, 출력 차원 1인 선형호귀 모델을 선언
model = torch.nn.Linear(in_features=1, out_features=1)
```

## 3. 경사하강법

### 전역 최솟값(Global Minimum), 지역 최솟값(Local Minimum)

- **전역 최솟값:** 함수 전체에 대해서 가장 최소가 되는 위치
- **지역 최솟값:** 특정 부근에서의 최솟값
> → 지역 최솟값을 찾아서 이를 전역 최솟값이라 착각하면 모델을 만들 때 치명적 오류가 발생할 수 있음

### 🔹 해결?


| 방법 | 🔑 설명 | ⚡ 특징 |
| ---- | ------ | ------ |
| Random Initialization | 여러 시작점 설정 | 최솟값 다양성 확보 |
| SGD (Stochastic GD) | 데이터 한 샘플 사용 | 노이즈 발생 → 지역 최솟값 회피 |
| Mini-batch GD | 일부 배치 사용 | 전체 데이터보다 계산 효율 ↑ |
| Momentum | 이전 방향 기억 | 관성 적용 → 안정적 수렴 |
| Adam | 모멘텀 + RMSprop | 적응적 학습률, 1차/2차 모멘트 활용 |

### 🔹 팁
- Adam 최적화 알고리즘이 현재 가장 많이 사용됨 ✅  
- 배치 크기, 학습률 조정 → 학습 안정성과 속도 향상  
- 지역 최솟값 회피 위해 초기값 다양화 🌟