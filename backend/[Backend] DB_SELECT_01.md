# 🗓️ DB SELECT 기본 학습 내용 요약

## 🎯 학습 목표
- 데이터베이스의 개념과 구조 이해
- SQL 문법과 주요 구문 활용
- SELECT, WHERE, ORDER BY, LIMIT 이해
- NULL, 함수, CASE 등 SQL 문법 이해

---

## 💡 주요 키워드
`DB`, `DBMS`, `RDBMS`, `SQL`, `SELECT`, `WHERE`, `CASE`, `LIMIT`, `NULL`, `함수`

---

## 🗄️ 데이터베이스 개념
> 다수의 사용자가 공유 가능한 데이터 집합으로 DBMS에 의해 관리됨

### 특징
- 실시간 접근 가능 🕒
- 데이터 독립성 🔗
- 동시 공유 👥
- 무결성 보장 ✅
- 중복 최소화 🔄
- 보안 강화 🔒
- 일관성 유지 📏
- 지속성 보장 💾

---

## 🖥️ DBMS
> 응용프로그램과 DB 사이에서 데이터 관리 및 제어 역할
- 질의 처리, 동시성 제어, 보안, 복구, 계정 관리 등

---

## 🏛️ RDBMS 구성 요소
| 용어 | 설명 |
|------|------|
| 개체(Entity) | 현실 세계의 독립적 실체 |
| 관계(Relationship) | 개체 간 상호작용/연결 (1:1, 1:N, N:M) |
| 속성(Attribute) | 개체의 세부 특성 |

+ 관련 용어: Table, Column, Row, Foreign Key

---

## 📝 SQL
> 관계형 DB를 관리하기 위한 특수 목적 언어

### SQL 구문 종류
| 구분 | 명령어 | 설명 |
|------|--------|------|
| DML | INSERT | 레코드 삽입 |
| DML | UPDATE | 레코드 수정 |
| DML | DELETE | 레코드 삭제 |
| DML/DQL | SELECT | 레코드 조회 |
| DDL | CREATE | DB 객체 생성 |
| DDL | ALTER | DB 객체 수정 |
| DDL | DROP | DB 객체 삭제 |
| DDL | RENAME | DB 객체 이름 변경 |
| TCL | COMMIT | 변경 내용 적용 |
| TCL | ROLLBACK | 변경 내용 취소 |
| TCL | SAVEPOINT | 트랜잭션 저장점 생성 |
| DCL | GRANT | 권한 부여 |
| DCL | REVOKE | 권한 회수 |

---

## 🔍 SELECT & WHERE
```sql
SELECT * | {[DISTINCT] {{컬럼명 | 표현식} {[AS] [별칭]},...}}
FROM 테이블명
```
> **별칭** 사용(AS)할 때 **큰 따옴표**로 감싸서 작성해야 하는 경우:
> 1. SQL 구문 분석 시에 문제가 될 수 있는 특수 문자들을 포함할 경우
> 2. 공백을 포함하는 경우

#### 중복 행 제거(distinct)
- 조회하려는 **모든 컬럼의 조합**이 일치하는 행을 제거
- 컬럼별로 중복 제거는 불가능
- SELECT절에 1번만 기술

### 선택적 데이터 조회(WHERE절)
```sql
SELECT * | {[DISTINCT] {{컬럼명 | 표현식} {[AS] [별칭]},...}}
FROM 테이블명
WHERE 조건식 [{AND|OR}, 조건식];
```
- 결과 집합을 filtering하기 위한 것
- 문자와 날짜 타입의 상수 값은 작은 따옴표('')로 묶어서 표현, 숫자는 그대로 사용
- 제한 조건을 여러 개 포함할 수 있으며, 각각의 제한 조건은 논리 연산자로 연결(and/or)

### 비교 연산자
| 연산자  | 설명                                | 
| --------- |------------------------------------|
| **=**      | 같다                                |
| !=, <>   | 같지 않다                            |
|<, <=,>,>= | 작다, 작거나 같다, 크다, 크거나 같다  |
|IS NULL   | NULL인지 비교                        |
|BETWEEN a AND b | a 이상 b 이하의 범위 비교 **경계값 포함**|
|IN(value1, value2...)| 목록 안에 일치하는 값이 있는 지 비교 |
| LIKE 패턴 | 특정 패턴을 만족하는지 비교, 패턴 표현을 위해 와일드 카드 사용, **%:0개 이상의 임의의 문자를 의미**  **_:1개의 임의의 문자를 의미**  |

---

### 논리 연산자
| 연산자 | 설명              |
| ------ | -----------------|
| AND, &&| 논리 곱          |
| OR, || | 논리 합          |
| XOR    | 배타적 논리합(두 논리 식의 결과가 서로 다를 때만 참)|
| NOT, ! | 논리 부정        |


### NOT 연산
> NOT 연산과 함께 사용하면 반대의 결과가 나옴
#### 🔹 예시 1: WHERE 절에서 NOT (일반적인 사용)
```sql
SELECT name, dept
FROM employee
WHERE NOT (dept = 'Sales');
```
➡ dept가 'Sales'가 아닌 직원만 조회

#### 🔹 예시 2: IN, BETWEEN, LIKE와 함께 사용
```sql
-- IN과 함께
SELECT name FROM employee
WHERE dept NOT IN ('Sales', 'HR');

-- BETWEEN과 함께
SELECT name FROM student
WHERE age NOT BETWEEN 20 AND 30;

-- LIKE와 함께
SELECT name FROM customer
WHERE name NOT LIKE 'K%';
```

---

### ⚠️ NULL 연산과 비교
> ✅ 핵심 개념NULL은 **값이 "없다"**라는 뜻, 0 또는 빈 문자열과 다름
> NULL이 포함된 연산의 결과는 대부분 NULL이 됨(=모르는 값과의 계산 결과도 모른다)

#### ⚠️ 주의해야 할 주요 경우

1. 산술 연산(더하기, 빼기 등)
```sql
SELECT 10 + NULL AS result;
```
➡ 결과: NULL

2. 비교 연산(=, >, < 등)
```sql
SELECT * FROM student WHERE age = NULL;
```
➡ 결과: 아무 행도 안 나옴
> 💡 이유:
> NULL은 어떤 값과도 같지 않음 
> 심지어 NULL = NULL 도 FALSE

✅ 올바른 방법은 IS NULL / IS NOT NULL을 사용하는 것:
```sql
SELECT * FROM student WHERE age IS NULL;
```

3. 논리 연산(AND, OR, NOT)
| 표현식              | 결과      |
| ---------------- | ------- |
| `TRUE AND NULL`  | `NULL`  |
| `FALSE AND NULL` | `FALSE` |
| `TRUE OR NULL`   | `TRUE`  |
| `FALSE OR NULL`  | `NULL`  |
| `NOT NULL`       | `NULL`  |
> `false and`와 `true or`제외한 나머지 결과는 모두 `null`

4. 집계 함수(SUM, AVG, COUNT 등)
```sql
SELECT SUM(salary), AVG(salary), COUNT(salary)
FROM employee;
```
- SUM, AVG  : NULL 값 무시됨(0처럼 계산 X)
- COUNT(salary) : **NULL인 행은 세지 않음**
- COUNT(*) : NULL과 관계없이 **모든 행을 셈**

5. 문자열 결합 
```sql
SELECT 'Hello ' || NULL AS result;
```
또는
```sql
SELECT CONCAT('Hello ', NULL);
```
➡ 결과: NULL
> 💡 문자열 중 하나라도 NULL이면 전체 결과가 사라짐
> (단, MySQL에서 CONCAT_WS()는 NULL을 무시)

---

### 🔢 비트 연산자
| 연산자 | 설명 |
|--------|------|
| &      | 두 비트가 모두 1이면 1 반환 (AND 연산) |
| \|     | 두 비트 중 하나라도 1이면 1 반환 (OR 연산) |
| ^      | 두 비트가 서로 다르면 1 반환 (XOR 연산) |
| ~      | 비트 반전: 1 → 0, 0 → 1 (NOT 연산) |
| <<     | 지정한 수만큼 비트를 왼쪽으로 이동 (Left Shift) |
| >>     | 지정한 수만큼 비트를 오른쪽으로 이동, 부호 유지 (Right Shift) |

---

### CASE 연산자
> ELSE는 생략 시 NULL
#### ✅ 사용 방법 1: 값의 동등 비교에 따른 값 반환
> `CASE 표현식 WHEN 값 THEN 결과 ...`
특정 컬럼 값이 **정확히 일치하는 경우**에 따라 결과를 반환
```sql
SELECT 
    name,
    dept,
    CASE dept
        WHEN 'Sales' THEN '영업부'
        WHEN 'HR' THEN '인사부'
        WHEN 'IT' THEN '개발부'
        ELSE '기타부서'
    END AS 부서명
FROM employee;
```

#### ✅ 사용 방법 2: 조건식 분기에 따른 값 반환
> `CASE WHEN 조건 THEN 결과 ...`
이 방식은 > , < , BETWEEN , LIKE 등 논리식(조건) 으로 분기
```sql
SELECT 
    name,
    score,
    CASE 
        WHEN score >= 90 THEN 'A'
        WHEN score >= 80 THEN 'B'
        WHEN score >= 70 THEN 'C'
        WHEN score >= 60 THEN 'D'
        ELSE 'F'
    END AS grade
FROM student;
```

---

### 정렬(ORDER BY절)
```sql
SELECT * | {[DISTINCT] {{컬럼명 | 표현식} {[AS] [별칭]},...}}
FROM 테이블명
WHERE 조건식 [{AND|OR}, 조건식]
ORDER BY 기준1[ASC|DESC] [,기준2[ASC|DESC],...];
```
- SELECT 구문 실행 결과를 특정 컬럼 값 기준으로 정렬할 때 사용
- 컬럼이름, 컬럼 별칭 또는 컬럼 기술 순서로 표현 가능(1,2,3,..)
- 정렬 조건
    - ASC(기본값) 오름차순 -> 생략 가능
    - DESC : 내림차순

---

### 행 결과 제한(LIMIT 절)

#### ✅ 기본 개념
- LIMIT → 가져올 행의 개수
- OFFSET → 건너뛸 행의 개수 (시작 위치)
> 주의할 점! OFFSET은 0부터 시작한다. 

```sql
SELECT * | {[DISTINCT] {{컬럼명 | 표현식} {[AS] [별칭]},...}}
FROM 테이블명
WHERE 조건식 [{AND|OR}, 조건식];
ORDER BY 기준1[ASC|DESC] [,기준2[ASC|DESC],...]
LIMIT 개수[,OFFSET n] | n, 개수;
```
- OFFSET n 생략 시 OFFSET 0으로 처리
    - 첫 번째 레코드부터 개수만큼의 의미
- **Top N 쿼리** 또는 페이징 처리 시 활용
- `LIMIT n OFFSET M` "m번째 행부터 n개를 가져온다"라는 뜻
- `LIMIT m, n` m은 OFFSET n은 LIMIT

---

## 내장 함수
    - 반복적으로 사용되는 부분들을 분리하여 DB에 미리 만들어 놓은 작은 프로그램
    - 함수 처리 과정에 따라 단일 행 함수, 다중 행 함수로 구분

### 단일 행 함수
> 각 행에 대해 하나씩 결과를 반환하는 함수(입력 1 -> 출력 1)
> EX. 숫자, 문자, 날짜, 변환, 기타 함수

#### ✅ 예시
```sql
SELECT name, UPPER(name), LENGTH(name)
FROM student;
```
> 💡 해석:
> 각 행마다 name 값을 받아 대문자로 바꾸거나 길이를 계산
> 모든 행에 대해 개별 계산

### 다중 행 함수
> 여러 행을 모아서 하나의 결과만 반환하는 함수(입력 N -> 출력 1)
> EX 집계, 윈도우 함수

#### ✅ 예시
```sql 
SELECT 
    AVG(salary) AS 평균급여,
    MAX(salary) AS 최고급여,
    MIN(salary) AS 최저급여,
    COUNT(*) AS 직원수
FROM employee;
```
> 💡 해석:
> - 전체 행을 하나의 그룹으로 보고 계산
> - 행이 여러 개라도 결과는 딱 1개
> - **GROUP BY**와 함께 쓰면 “부서별 평균” 등으로도 계산 가능

### 기타 함수
- IFNULL(값1, 값2) : 값1이 NULL이면 값2 NULL이 아니면 값1 반환
- NULLIF(값1, 값2) : 값1=값2이면 NULL 그렇지 않으면 1 반환

