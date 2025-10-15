# 📊 역전파 학습 요약

## 📚 목차

---

## 1. 역전파

## 1-1. 역전파(Backpropagation)

> 가중치 $$w 와 편향 $$b 를 계산하기위한 방법
$$y = $$w$$x + $$b

? MAE 보다 MSE를 쓰는 이유
-> MAE로 하면 볼록한 형태가 아닌 뾰족한 형태; 0이 되는 곳을 미분할 수가 없음

```python
# 초기값 x=2, y=4, w=3, b=1
x = torch.tensor([2,0])
y = torch.tensor([4,0])

weight  = torch.tensor([3,0], requires_grad=True) # w
b = torch.tensor([1,0], requires_grad=True) # b

print(weight, b)
```

- AutoGrad

  > `.backward()`메서드를 호출하면 그래프를 따라 자동으로 역전파를 수행하여 기울기를 구할 수 있음

- requires_grad
  > **"지금 변수를 미분할 수 있게 해줘"**라고 알려주는 속성
  > 모델의 가중치, 편향 또는 업데이트가 필요한 변수들에 `True`로 설정하여 사용할 수 있음

## 순전파(Forward Pass)

## 손실계산(loss)

## 역전파

> 연쇄 법칙(Chain Rule)을 이요ㅏ여 각 파라미터에 대한 기울기를 계산

- $$y^ 에 대한 편미분 :

$$$\frac{∂L}{∂w}=$\frac{∂L}{∂y^}×$\frac{∂y^}{∂w}$$

- $$b에 대한 편미분 :

$$$\frac{∂L}{∂b}=$\frac{∂L}{∂y^}×$\frac{∂y^}{∂b}$$

### 계산 단계

1. $$$\frac{∂L}{∂y^}$$ 계산

   > 손실 함수(MSE)를 y^ 에 대해 미분
   > $$y−$$y^ 를 $$u 로 치환하면  L=$$u^2

2. $$$\frac{∂y^}{∂w}$$ 계산

   > $$y^=wx+b$$ 를 $$w에 대해 미분

3. ∂y^∂b 계산

   > $$y^=wx+b$$ 를 $$b에 대해 미분

4. 최종 기울기 계산

> ∂L∂w=∂L∂y^×∂y^∂w=6×2=12
> ∂L∂b=∂L∂y^×∂y^∂b=6×1=6

### 파라미터 업데이트

> 학습률 &alpha; = 0.1$$을 사용하여 파라미터를 업데이트

- $$
  w 갱신
  wnew=w−α∂L∂w=3−0.1×12=1.8
  $$

- $$
  b 갱신
  bnew=b−α∂L∂b=1−0.1×6=0.4
  $$

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

- no_grad()
  > 기울기를 추적하지 않도록 함
  > 현재 상태에서의 기울기를 구한 값을 활용해야하기 때문에 이 메서드를 사용하지 않으면
  > 다음 기울기에 누저하여 잘못된 값을 도출하게 됨

## 검증

> 새롭게 업데이트된 파라미터를 기반으로 다시 계산 진행

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
  -> 지역 최솟값을 찾아서 이를 전역 최솟값이라 착각하면 모델을 만들 때 치명적 오류가 발생할 수 있음

### 해결?

1. 다양한 위치에서 시작(Random Initialization)
   > 시작점이 다르면 도달하는 최솟값도 달라질 수 있기 때문에, 여러 번 시도하여 그 중 가장 좋은 결과를 사용
2. 확률적 경사 하강법(SGD, Stochastic Gradient Descent)
   > 전체 데이터 셋 대신 하나의 데이터 샘플을 이용한 방법입니다.

- 매번 다른 샘플로 계산하기 때문에 노이즈가 발생하는데, 오히려 이 노이즈가 지역 최솟값에 빠지지 않게 함

3. 미니 배치 경사 하강법(MGD, Mini-batch Gradient Descent)

   > 전체 데이터를 활용하는 방법을 Batch Gradient Descent라 말하며,
   > 데이터의 일부(32, 64, 128개 ...)로 나누어 각 배치마다 기울기를 계산하는 방법

4. 모멘텀(Momentum)

   > 관성 개념을 적용하여 이전 방향을 기억하면서 이동하는 방법

5. 아담(Adam, Adaptive Moment Estimation)
   > 모멘텀과 RMSprop의 장점을 결합한 적응적 학습률 최적화 알고리즘

- 1차 모멘트(방향성), 2차 모멘트(크기)를 활용
- "이 방법이 가장 잘 되더라" 정도로만 알아두자
