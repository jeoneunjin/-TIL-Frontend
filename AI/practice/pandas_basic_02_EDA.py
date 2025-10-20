'''
[학습 개념]
- EDA : 데이터 분포, 중앙값-사분위수-상관관계 등을 시각화를 통해 탐색하는 기법
- StandarScaler : 피처별 평규능ㄹ 0, 분산을 1로 표준화하여 머신러닝 모델 학습을 안정화하는 전처리 기법
- Logistic Regression : 선형 방정식을 기반으로 한 회귀 계수 해석을 통해 확률적 분류를 수행하는 지도 학습 알고리즘
- PCA : 고차원 데이터를 주성분으로 변환하여 정보를 최대한 보존하면서 차원을 축소하는 기법
- KMeans : 데이터 포인트 간 거리를 기준으로 미리 지정한 개수의 클러슽로 크룹화하는 비지도 학습 방법
'''

# 1. 데이터 불러오기
# sklearn.datasets : 데이터셋 제공 모듈
'''
Args : 
    as_fram(boolean) : pandas Datafram으로 변환
    return_X_y : X(특성), y(레이블)만 반환; 튜플 형태
''' 
from sklearn.datasets import load_wine
df, y = load_wine(as_fram=True, return_X_y = True)

# quality==0인 prline값들 list로 구하기
print(df[df['quality']==0]['proline'].tolist())

# 2. shape -> tuple값 반환 (row 수, column 수)
# 2-1. 총 샘플 수 구하기
print(df.shape[0])

# 2-2. 특성 수 구하기
print(df.shape[1])

# 3. 타겟 변수(y)가 몇 개의 클래스로 구분되는지
cnt = df['quality'].nunique()
print(cnt)

# 4. 각 클래스 별 샘플 수 Serires 형태로 구하기
# sort_index() : quality 값 순서대로 정렬
# value_counts() : 등장 횟수 반환
dis = df['quality'].value_counts().sort_index()

# 5. 'alcohol' 평균 값이 가장 높은 클래스 번호 구하기
# idxmax()
top_class = df.groupby('quality')['alcohol'].mean().idxmax()
print(top_class)

# 6. 'malic acid'특성의 표준편차
malic_std = df['malic_acid'].std()
print(malic_std)

# 7. 'Color intensity'가 10 이상인 샘플의 비율(%) 구하기 
high_color_ratio = (
    df['color_intensity'] >= 10
).mean() * 100

# 8. 'Ash' 특성에서 최소값을 가진 샘플의 클래스 구하기
# df.loc[row_idx, 'quality'] : row_idx에 해당하는 행에서 'quality' 컬럼 값 가져오기 
min_ash = df.loc[df['ash'].idxmin(), 'quality']

# 9. 'proline'분포에서 가장 높은 피크를 보이는 클래스 번호 구히가
proline_peak = df.groupby('quality')['proline'].max().idxmax()

# 10. magnesium 값이 상위 10%인 샘플들의 평균 proline 값 구하기
'''
- quantile(float f)
    데이터를 오름차순으로 정렬했을 때, 특정 비율 위치에 있는 값

- 예를 들어, 데이터 100개가 있으면:
    - 0.5 분위수 → 중앙값(median), 데이터의 절반이 이 값 이하
    - 0.25 분위수 → 1사분위(Q1), 데이터 하위 25% 경계
    - 0.75 분위수 → 3사분위(Q3), 데이터 상위 25% 경계
'''
high_magnesium_mean = df.loc[df['magnesium'] >= df['magnesium'].quantile(0.9), 'proline'].mean()

# 11. Alcohol과 가장 상관관계가 높은 특성 이름 구하기
'''
numeric_only : 숫자 컬럼만 사용
.drop() : 자기 자신과의 상관계수(0.1) 제거
.abs(), .idxmax(), 절댓값 기준으로 가장 큰 컬럼 인덱스 구하기
'''
top_corr_class = (
    df.corr(numeric_only=True)['alcohol']
    .drop('alcohol')
    .abs()
    .idxmax()
)