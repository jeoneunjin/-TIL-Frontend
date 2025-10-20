import seaborn as sns
import pandas as pd

'''
기본적인 데이터 전처리
'''

df = sns.load_dataset('tips')
df.head()

# 1. groupby + pivot
'''
pandas를 사용하여 `tip` 데이터를 성별과 요일별 평균으로 정리,
마지막에는 행렬 형태(pivot table)로 바꾸기
'''
    # 1. groupby -> sex, day 컬럼을 기준으로 그룹화 
    # 2. ['tip'] 그룹화 후 tip 컬럼만 선택 
    # 3. .mean() 각 그룹별 tip의 평균 계산
    # 4. .reset_index() Multiindex -> 일반 컬럼으로 변환
grouped = df.groupby(['sex', 'day'])['tip'].mean().reset_index()

    # 행이 되는 컬럼 : sex, 열이 되는 컬럼 day, 교차점(cell)에 들어갈 값 tip
pivot_table = grouped.pivot(index='sex', colums='day', values='tip')

    # 2. 컬럼 추가
df['tip_percent'] = df['tip'] / df['total_bill']
    # 3. 필터링
    # 팁 비율이 20퍼 이상인 경우만 필터링
high_tippers = df[dp['tip_percent'] >= 0.2]

# 2.long-format으로 변환
'''
- Wide-format(넓은 형식)
    - 여러 개의 측정값/변수가 각각 열로 표현됨
    - 읽기는 편하나 groupby나 시각화에 비효율적
    
    size   total_bill    tip
    2           16.99   1.01
    3           10.34   1.66
   
- Long-format(길게 늘어진 형식)
    - 측정 항목을 하나의 열에 모으고, 값은 별도 열로 표현
    - groupby, pivot 시각화 등에 유용하게 사용
    
    
    size        item   amount
    2     total_bill    16.99
    2            tip     1.01
    3     total_bill    10.34
    3            tip     1.66

'''
    # total_bill, tip 컬럼만 녹이기 
    # size 컬럼은 그대로 유지 id_vars=['size] 사용
    # 다음 컬럼을 가진 결과 만들기['size', 'item', 'amount']
'''
출력 예시

    size          item  amount
    0     2 total_bill  16.99
    1     3 total_bill  10.34
    2     3 total_bill  21.01
    3     2 total_bill  23.68
    4     4 total_bill  24.59
    ...
    483 3          tip  5.92
    484 2          tip  2.00
    485 2          tip  2.00
    486 2          tip  1.75
    487 2          tip  3.00
'''

tips = sns.load_dataset("tips")
melted = pd.melt(
    tips,
    id_vars=['size'], #그대로 유지할 컬럼
    value_vars=['total_bill', 'tip'], #녹일 컬럼
    var_name='item', #녹인 후의 컬럼 이름
    value_name='amount' #값이 들어갈 컬럼 이름
)

# size별 평균 팁 비율 구하기
    # 1. size 컬럼을 기준으로 tip_percent 평균 계산
    # 2. size=1부터 size=6까지 평균 비율 구하기 
    
lf = sns.load_dataset("tips")
lf['tip_percent'] = lf['tip'] / lf['total_bill']
size_group = df.groupby('size')['tip_percent'].mean()

'''
주의할점! 
pivot()은 한 컬럼(Series)만 values로 받아야 함 = groupby 결과는 Series이어야 함

아까 grouped = df.groupby(['sex', 'day'])['tip'].mean().reset_index()
에서 reset_index()를 해도 괜찮은 이유? 
→ ['tip']으로 단일 컬럼만 선택했기 때문에 결과가 Series였음
→ 그 뒤에 reset_index()를 해도 DataFrame이지만 values로 지정한 tip은 여전히 단일 컬럼임
따라서 pivot()에 values='tip'으로 지정해도 에러 없이 동작

+ 위의 예시에도 사실 .reset_index()를 해도 된다. 단일 컬럼이기 때문
'''

