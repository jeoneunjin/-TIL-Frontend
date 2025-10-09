# 🗓️ DB SELECT 응용 학습 내용 요약

## 🎯 학습 목표
- 집계 함수 활용 및 그룹화 이해
- GROUP BY, HAVING, ROLLUP, GROUPING 사용법 습득
- 피봇 테이블(Pivot Table) 작성 이해


## 💡 주요 키워드
`GROUP BY`, `HAVING`, `ROLLUP`, `GROUPING`, `집계 함수`, `피봇 테이블`

---

## 🧮 집계 함수 (다중 행 함수)
> NULL 값은 집계에서 제외됨 (COUNT(*) 제외)

| 함수 | 설명 |
|------|------|
| COUNT([DISTINCT|ALL] 컬럼) | NULL 제외 해당 컬럼 레코드 수 (COUNT(*)는 전체) |
| SUM([DISTINCT|ALL] 컬럼) | 그룹 내 합계 |
| AVG([DISTINCT|ALL] 컬럼) | 그룹 내 평균 |
| MAX([DISTINCT|ALL] 컬럼) | 그룹 내 최대값 |
| MIN([DISTINCT|ALL] 컬럼) | 그룹 내 최소값 |

---
## 📊 GROUP BY 절
- **SELECT 절의 집계 함수 외 컬럼은 GROUP BY에 포함 필요**
- WHERE 절은 그룹화 전에 필터링 적용(GROUP BY전에 WHERE이 우선 적용)

| 구분 | 예시 | 설명 |
|------|------|------|
| ❌ 잘못된 예 | `SELECT dept, name, SUM(salary) FROM employee GROUP BY dept;` | name 컬럼이 GROUP BY에 없어 오류 |
| ✅ 올바른 예 | `SELECT dept, name, SUM(salary) FROM employee GROUP BY dept, name;` | dept+name 기준 그룹화 |

---

```sql
SELECT * | {[DISTINCT] {{컬럼명 | 표현식} {[AS] [별칭]},...}}
FROM 테이블명
WHERE 조건식 [{AND|OR}, 조건식]
GROUP BY 컬럼명 | 표현식 [, 컬럼명 | 표현식, ...]
...
```

---


## 🏷️ HAVING 절
- GROUP BY 후 생성된 그룹에 조건 적용
```sql
SELECT * | {[DISTINCT] {{컬럼명 | 표현식} {[AS] [별칭]},...}}
FROM 테이블명
WHERE 조건식 [{AND|OR}, 조건식]
GROUP BY 컬럼명 | 표현식 [, 컬럼명 | 표현식, ...]
HAVING 조건
...
```

---

## 집계 함수 응용

### WITH ROLLUP
- **그룹별 소계 및 전체 총계**를 구할 때 사용
```sql
SELECT ...
FROM ...
WHERE ...
GROUP BY 컬럼명 | 표현식 [, 컬럼명 | 표현식, ...] WITH ROLLUP
HAVING 조건
...
```

- ROLLUP으로 생성된 `NULL`은 **집계 대상 컬럼이 더 이상 특정되지 않음**을 의미
- 단계별 그룹화와 NULL 발생:
  1. **세부 그룹 `(dept, position)`**: 부서+직책 기준 → NULL 없음
  2. **상위 그룹 `(dept)`**: 직책 구분 없음 → position = NULL (부서별 총합)
  3. **최상위 그룹**: 부서+직책 모두 구분 없음 → dept, position = NULL (전체 총합)

| dept/group | position/group | 설명 |
|------------|----------------|------|
| Sales      | Manager        | 세부 그룹 |
| Sales      | Staff          | 세부 그룹 |
| Sales      | NULL           | 부서별 총합 |
| HR         | Manager        | 세부 그룹 |
| HR         | Staff          | 세부 그룹 |
| HR         | NULL           | 부서별 총합 |
| NULL       | NULL           | 전체 총합 |

- `GROUPING` 함수 사용: ROLLUP NULL 구분
- CASE 사용 시 NULL 표시를 의미 있는 문자열로 대체 가능 (`전체 부서`, `전체 직책`)

---

### GROUPING 함수
- 조회되는 컬럼의 NULL값이 실제 NULL값인지 ROLLUP에 의해 생성된 NULL값인지 판단
    - 실제 NULL 값 또는 NULL 값이 아닌 값 -> 0 리턴
    - ROLLUP에 의한 NULL 값 -> 1 리턴

#### 사용 예시
```sql
SELECT 
    dept, 
    position, 
    SUM(salary) AS total_salary,
    GROUPING(dept) AS dept_grouping,
    GROUPING(position) AS position_grouping
FROM employee
GROUP BY dept, position WITH ROLLUP;
```
**➡ 🎯 결과 테이블**

| dept  | position | total_salary | dept_grouping | position_grouping |                               |
| ----- | -------- | ------------ | ------------- | ----------------- | ----------------------------- |
| Sales | Manager  | 5000         | 0             | 0                 |                               |
| Sales | Staff    | 3000         | 0             | 0                 |                               |
| Sales | NULL     | 8000         | 0             | 1                 | ← position이 합계용 NULL          |
| HR    | Manager  | 4000         | 0             | 0                 |                               |
| HR    | Staff    | 2000         | 0             | 0                 |                               |
| HR    | NULL     | 6000         | 0             | 1                 |                               |
| NULL  | NULL     | 14000        | 1             | 1                 | ← dept와 position 둘 다 합계용 NULL |

### 🔹 ROLLUP로 인한 NULL 없애는 방법(GROUPING + CASE 사용)
```sql
SELECT 
    CASE WHEN GROUPING(dept)=1 THEN '전체 부서' ELSE dept END AS dept_name,
    CASE WHEN GROUPING(position)=1 THEN '전체 직책' ELSE position END AS position_name,
    SUM(salary) AS total_salary
FROM employee
GROUP BY dept, position WITH ROLLUP;
```
**➡ 🎯 결과 테이블**
| dept_name | position_name | total_salary |               |
| --------- | ------------- | ------------ | ------------- |
| Sales     | Manager       | 5000         |               |
| Sales     | Staff         | 3000         |               |
| Sales     | 전체 직책         | 8000         | ← position 합계 |
| HR        | Manager       | 4000         |               |
| HR        | Staff         | 2000         |               |
| HR        | 전체 직책         | 6000         | ← position 합계 |
| 전체 부서     | 전체 직책         | 14000        | ← 전체 합계       |


---


## 🔹 피봇 테이블(Pivot Table)
- 데이터를 **행, 열, 값** 기준으로 재구성하여 요약
- 행/열 그룹화 + 값 집계(SUM, AVG, MAX 등)
- 분석 시 특정 기준(직무, 입사월 등)별 비교 용이

### 📊 예시 테이블(sales)
| year | quarter | revenue |
| ---- | ------- | ------- |
| 2025 | Q1      | 1000    |
| 2025 | Q2      | 1500    |
| 2025 | Q3      | 1200    |
| 2025 | Q4      | 1700    |
| 2026 | Q1      | 1100    |
| 2026 | Q2      | 1600    |

```sql
SELECT 
    year,
    SUM(CASE WHEN quarter='Q1' THEN revenue ELSE 0 END) AS Q1,
    SUM(CASE WHEN quarter='Q2' THEN revenue ELSE 0 END) AS Q2,
    SUM(CASE WHEN quarter='Q3' THEN revenue ELSE 0 END) AS Q3,
    SUM(CASE WHEN quarter='Q4' THEN revenue ELSE 0 END) AS Q4
FROM sales
GROUP BY year;
```
**➡ 🎯 결과 테이블**
| year | Q1   | Q2   | Q3   | Q4   |
| ---- | ---- | ---- | ---- | ---- |
| 2025 | 1000 | 1500 | 1200 | 1700 |
| 2026 | 1100 | 1600 | 0    | 0    |

---
