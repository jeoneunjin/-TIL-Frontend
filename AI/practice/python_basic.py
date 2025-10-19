# 1. 값 두 개가 얼마나 가까운지
''' 
설정한 오차율보다 차이가 크면 False 반환, 작으면 True
abs(a - b) → 실제 두 값의 차이
rel_tol * max(abs(a), abs(b)) → 허용 가능한 최대 차이(상대 오차 한계)
'''
from math import isclose

is_close = isclose(3, 2, rel_tol=2)
print(is_close) #true

# 2. 특정 문자로 끝나는지 확인(str)
str = "eunjin"
print(str.endswith("n")) #true

# 3. typehint 
'''
파이선은 동적 타이핑; 변수 선언 시 타입을 명시 X
변수에 자료형을 알려주는 typehint 사용
강제성은 없음 실제로 대입하는 값과 type이 달라도 문제 발생 X
***개발자 참고용***
'''
res : int = "hello"


#. list(순서 O, 변경 O)
l = [1,2,True,"apple"]

# 특정 값 포함 여부
print(3 in l)

# 짝수 인덱스 추출(인덱싱)
print(l[1::2])

# 홀수는? 
print(l[::2])

# 두 list 붙이기 
double_list = l + l

# reverse() vs reversed()
l.reverse() # 리턴값 None, 객체 자체를 바꿈 

rev = reversed(l) # 원본 변경 X, 결과 iterator list()나 tuple로 감싸야 실제 데이터를 볼 수 있음 
print(list(rev))
# +) iterator = "반복 가능한 객체에서 하나씩 값을 꺼내는 장치"

# tuple (순서 O, 변경 X)
tuple = (10, 20, 'cherry')

# set (순서 X, 중복 X)
set = {1,2,3}

# 이어 붙이기? -> 불가능; 집합은 덧셈 연산이 불가하다
try:
    result = set = set
except TypeError:
    print("set는 덧셈 연산 불가능")

# 인덱싱? -> 불가능; 집합은 인덱싱이 불가하다
try:
    subset = set[1:3]
except TypeError:
    print("set는 인덱싱 불가능")
    
# 교집합
set2 = {3,4,5}
print(set & set2)

# 합집합
print(set | set2)

# 차집합
print(set - set2)

# 대칭차집합
# 두 집합에서 겹치지 않는 원소만 남김
print(set ^ set2)

# dict (순서 X)
'''
dict에 부가적인 기능을 탑재한 빌트인 딕셔너리가 추가로 존재
1. defaultdict: 기본값을 지정할 수 있는 딕셔너리, 키가 존재하지 않을 때 KeyError 대신 지정된 기본값을 자동으로 생성
2. Counter : 리스트나 문자열 등 반복 가능한 객체의 요소 개수를 자동을 세어주는 딕셔너리, 가장 많이 등장한 요소를 쉽게 파악할 수 있음
'''

from collections import defaultdict, Counter

# defaultdict
dd = defaultdict(int)
# 아직 eunjin이라는 key값에 해당하는 value가 없지만 +=1 연산이 가능
dd['eunjin'] += 1
print(dict(dd)) # {'eunjin' : 1}

# Counter 
fruits = ['apple', 'apple', 'banana']

counter = Counter(fruits)
print(dict(counter)) # {'apple' : 2, 'banana' : 1}

# dict 합치기
d1 = {
    1 : "apple",
    2 : "banana"
}
d2 = {
    1 : "cherry",
    3 : "dragonfruit"
}
# 같은 키가 있으면 오른쪽(d2)값으로 덮어씀
print(d1 | d2)

