# 🗓️ DML + TX 학습 내용 요약

## 🎯 학습 목표
- 테이블 데이터 삽입, 수정, 삭제 방법 이해
- JOIN, 중복 처리, 자동 증가 컬럼 활용법 숙지

---

## 💡 DML(Data Manipulation Language)
|종류     | 의미                                   |
|---------|---------------------------------------|
|INSERT   | 테이블에 1개 또는 여러 개의 새로운 데이터 삽입 |
|UPDATE   | 테이블에 존재하는 1개 또는 여러 개의 기존 데이터 수정 |
|DELETE   | 테이블에 존재하는 1개 또는 여러 개의 데이터 삭제 |

---

## ✨ INSERT

### 단일 행 삽입
- 컬럼 리스트에 맞춰 값 입력
- 컬럼 생략 시 DB가 자동 처리

### 다중 행 삽입
- 생략 가능한 컬럼:
  1. NULL 허용 → 자동으로 NULL 입력
  2. DEFAULT 설정 → DEFAULT 값 입력
  3. AUTO_INCREMENT → 자동 증가 값 입력

**예시1 : VALUES 사용**
| id | name    | age | grade |
|----|---------|-----|-------|
| 1  | Alice   | 20  | A     |
| 2  | Bob     | 22  | B     |
| 3  | Charlie | 21  | C     |

**예시2 : 생략 가능한 컬럼**
- grade가 DEFAULT 'F'면 생략 가능
| id | name  | age | grade |
|----|-------|-----|-------|
| 4  | David | 23  | F     |
| 5  | Eva   | 20  | F     |

### 서브쿼리 삽입
- 다른 테이블/쿼리 결과를 삽입
- AUTO_INCREMENT 컬럼은 생략 가능

---

## ✨ UPDATE

### 기본 문법
```sql
UPDATE 테이블명
SET 컬럼명 = 값 | Sub Query [, 컬럼명=값,...]
[WHERE 조건];
```
- 수정 값은 직접 명시하거나 서브 쿼리의 결과를 이용할 수 있음
- 서브 쿼리의 결과를 이용할 경우 단일 행 서브 쿼리만 가능
- **WHERE 생략 시 전체 레코드 수정 ⚠️**

## UPDATE + JOIN
- 수정에 쓰일 데이터를 조인된 결과에서 가져와서 처리

### 기본 문법
```sql
UPDATE 테이블명 a
JOIN 테이블명 b ON (조인 조건)
SET a.컬럼명 = b.컬럼명 [, a.컬럼명=b.컬럼명...]
[WHRER 조건];
```


### 예시 테이블

- **employee 테이블**
| emp_id | name    | dept_id | salary |
|--------|---------|---------|--------|
| 1      | Alice   | 10      | 5000   |
| 2      | Bob     | 20      | 4500   |
| 3      | Charlie | 10      | 4800   |


- **department 테이블**
| dept_id | dept_name | raise_pct |
|---------|-----------|-----------|
| 10      | Sales     | 0.1       |
| 20      | HR        | 0.05      |


- **목표 : 부서별 인상율을 반영하여 empolyee.salary 갱신**

#### SQL 예시
```sql
UPDATE employee e
JOIN department d ON e.dept_id = d.dept_id
SET e.salary = e.salary * (1 + d.raise_pct);
```

#### ✅ 설명
1. `JOIN department d ON e.dept_id = d.depth_id`
    → employee와 department를 부서 기준으로 연결
2. `SET e.salary = e.salary * (1 + d.raise_pct)`
    → 각 사원의 현재 급여에 부서별 인상율 적용

**➡ 🎯 결과 테이블**
| emp_id | name    | dept_id | salary |               |
| ------ | ------- | ------- | ------ | ------------- |
| 1      | Alice   | 10      | 5500   | → 5000 × 1.1  |
| 2      | Bob     | 20      | 4725   | → 4500 × 1.05 |
| 3      | Charlie | 10      | 5280   | → 4800 × 1.1  |

---

## ✨ INSERT or UPDATE
- 기존 데이터가 있으면 수정 없으면 삽입으로 처리

### 기본 문법
```sql
INSERT INTO 테이블명 [(컬럼명 [, 컬럼명...])]
VALUES (값, [, 값...])
ON DUPLICATE KEY UPDATE 컬럼명=값 [, 컬럼명=값,...];
```

### 예시 상황
- `empolyee` 테이블
| emp_id | name | salary |
|--------|--------|--------|
| 1 | Alice | 5000 |
| 2 | Bob | 4500 |
- **목표 :**
    1. emp_id=2가 이미 존재하면 salary 갱신
    2. emp_id=3가 없으면 새로 삽입

### SQL 예시
```sql
INSERT INTO employee (emp_id, name, salary)
VALUES 
    (2, 'Bob', 4700),
    (3, 'Charlie', 4800)
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    salary = VALUES(salary);
```
#### ✅ 설명
- `(2, 'Bob', 4700)` → `emp_id=2` 존재 → `salary` 4700으로 갱신
- `(3, 'Charlie', 4800)` → `emp_id=3` 없음 → 새 행 삽입
- `VALUES(column)` → 삽입하려던 값 참조
> 중복이 가능한 컬럼 값을 `on duplicate key update` 안에 작성
> `컬럼명 = VALUES(값)` `INSERT` 부분의 값을 그대로 `UPDATE`에 반영하고 싶을 때 VALUES()사용 
> 특정 값으로 갱신하려면 `salary = salary + 500;`이런 형태도 가능

---

## ✨ DELETE
- **WHERE 생략 시 전체 레코드 삭제 ⚠️**
```sql
DELETE FROM 테이블명
[WHERE 조건]
```
---

## 트랜잭션(Transaction)
- 원자성을 가지는 하나의 논리적인 작업 단위(ex. 금융 거래, 재고 관리, 예약 시스템)
- 분할해서 실행할 수 없는 한번에 처리 해야 하는 작업들의 모음
- 여러 개의 SQL 문을 하나의 작업 단위로 묶어 처리
- 데이터의 무결성을 보장하는 핵심 개념
- 모든 작업이 성공적으로 완료되면 **커밋**, 하나라도 실패하면 **롤백** 수행

## 트랜잭션의 특징(ACID)
|특징 | 의미|
|--------------|--------------------------------|
| Atomicity(원자성) | 트랜잭션 내의 모든 연산이 성공하거나, 하나라도 실패하면 전체가 취소됨|
| Consistency(일관성) | 트랜잭션이 실행되기 전과 후의 DB 상태가 일관성을 유지해야 함, 데이터의 무결성(Integrity)및 비즈니스 규칙이 하상 유지되는 상태를 의미|
| Isolation(고립성) | 하나의 트랜잭션이 완료될 때까지 다른 트랜잭션이 접근하지 못함, 하나의 특정 트랙잭션이 완료될때까지 다른 트랜잭션이 특정 트랜잭션의 결과를 참조할 수 없음|
| Durability(지속성) | 트랜직션이 성공적으로 완료되면 그 결과가 영구적으로 반영됨 |

+ 잠금 : 동시성을 제어하기 위한 기능
+ 격리 수준 : 하나 또는 여러 트랜잭션 간의 작업 내용을 어떻게 공유하고 차단할 것인지를 결정하는 레벨

## 격리 수준
- READ UNCOMMITTED : 커밋되지 않은 데이터를 읽을 수 있음, Dirty Read 발생 가능
- READ COMMITTED : 커밋된 데이터만 읽을 수 있음, Non-Reaeatable 발생 가능
- REPEATABLE READ : 트랜잭션 내에서 같은 데이터를 조회하면 항상 같은 데이터 조회 보장, Phanatom Read 발생 가능
- SERIALIZABLE : 가장 엄격한 수준으로 동시 실행을 막음, 성능 저하 

## 트랜잭션 성능 최적화
- 트랜잭션 범위를 최소화하여 잠금 유지 시간 줄임
- 적절한 격리 수준 선택하여 동시성 개선
- 인덱스를 활용하여 성능 향상

## TCL(Transaction Control Language)
- START TRANSACTION(BEGIN TRANSACTION) 명시적인 트랜잭션 시작, 현 트랜잭션 처리를 위한 암묵적 자동 커밋 해제(session에 적용되는 것은 아님)
- COMMIT : 트랜잭션을 성공적으로 완료하고 변경 사항을 되돌림
- ROLLBACK [TO SAVEPOINT 명]: 트랜잭션을 취소하고 변경 사항을 되돌림 
- SAVEPONT SAVEPONT명 : 트랜잭션 내에서 특정 지점을 저장하여 부분 롤백을 가능하게 함