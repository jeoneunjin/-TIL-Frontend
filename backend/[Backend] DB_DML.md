# 🗓️ DML 학습 내용 요약

## 🎯 학습 목표

---

## 💡 주요 키워드

---

## DML(Data Manipulation Language)
- 테이블의 데이터를 조작하기 위한 명령어

|종류 | 의미 |
|-------|--------------------------------|
|INSERT | 테이블에 1개 또는 여러 개의 새로운 데이터 삽입|
|UPDATE | 테이블에 존재하는 1개 또는 여러 개의 기존 데이터 수정|
|DELETE | 테이블에 존재하는 1개 또는 여러 개의 데이터 삭제|

### INSERT
#### 단일 행 삽입
```sql
INSERT INTO 테이블명 [(컬럼명, [, 컬럼명...])]
VALUE ({값 | Sub Query} [,값...]);
```
- INTO절 컬럼 리스트에 명시한 컬럼에 맞게 VLUES 절에서 대응하는 컬럼 값 입력(순서대로 입력)
- INTO절 컬럼 리스트를 명시하지 않으면 테이블 생성시 정의한 모든 컬럼을 생성 순서와 동일한 순서로 입력

#### 다중 행 삽입
! 생략 가능한 컬럼 
1. NULL이 허용된 컬럼
2. DEFAULT가 설정된 컬럼
3. AUTO_INCREMENT가 설정된 컬럼

#### 방법 1(VALUES 사용)
```sql
INSERT INTO 테이블명 [(컬럼명, [, 컬럼명...])]
VALUE ({값 [,값...]}),
    ({값 [,값...]}),
    ...
    ({값 [,값...]});
```
**예시1 :**
```sql
INSERT INTO student (id, name, age, grade)
VALUES 
    (1, 'Alice', 20, 'A'),
    (2, 'Bob', 22, 'B'),
    (3, 'Charlie', 21, 'C');
```


**예시2(생략 가능한 컬럼) :**
- 만약 `grade` 컬럼이 `DEFAULT 'F'`로 설정되어 있다면 생략 가능
```sql
INSERT INTO student (id, name, age)
VALUES 
    (4, 'David', 23),
    (5, 'Eva', 20);
```
➡ grade는 DEFAULT 값 'F'로 자동 입력


#### 방법 2(서브쿼리로 삽입)
```sql
INSERT INTO 테이블명 [(컬럼명, [, 컬럼명...])]
Sub Query
```
**예시1 :**
```sql
INSERT INTO honor_students (id, name, grade)
SELECT id, name, grade
FROM student
WHERE grade = 'A';
```


**예시2(생략 가능한 컬럼) :**
- `id` 컬럼이 `AUTO_INCREMENT`로 설정되어 있으면 생략 가능
```sql
INSERT INTO honor_students (name, grade)
SELECT name, grade
FROM student
WHERE grade = 'A';
```
➡ id는 자동으로 증가(기본적으로 1부터 시작)

? 생략한다? 
값이 아예 안 들어간다 X
INSERT 시 값을 생략하면 DB가 알아서 값을 채워준다는 의미 O
| 컬럼 설정             | 생략 시 값               |
| ----------------- | -------------------- |
| NULL 허용           | 자동으로 **NULL** 입력     |
| DEFAULT 값 설정      | 지정된 **DEFAULT 값** 입력 |
| AUTO_INCREMENT 설정 | **자동 증가 값** 입력       |


---

## UPDATE
```sql
UPDATE 테이블명
SET 컬럼명 = {값 | Sub Query} [,컬럼명 = 값,...]
[WHRER 조건];
```
- 수정 값은 직접 명시하거나 서브 쿼리의 결과를 이용할 수 있음
- 서브 쿼리의 결과를 이용할 경우 단일 행 서브 쿼리만 가능
- **WHERE절 생략 시 전체 레코드가 수정 되니 유의해야 함**

### UPDATE와 JOIN
- 수정에 쓰일 데이터를 조인된 결과에서 가져와서 처리
```sql
UPDATE 테이블명 a
JOIN 테이블명 b ON (조인 조건)
SET a.컬럼명 = b.컬럼명 [, a.컬럼명=b.컬럼명...]
[WHRER 조건];
```

---




