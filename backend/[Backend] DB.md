# 🗓️ DB 학습 내용 정리

## 🎯 학습 목표


## 💡 주요 키워드
-

---

## DataBase 정의
> 특정 시스템 내에서 기능을 운영하기 위해 다수의 사용자들이 공유할 수 있도록 통합시키고 컴퓨터 저장 장치에 저장시킨 데이터의 집합
> DBMS에 의해 관리되는 구조화된 상호 관련이 있는 데이터의 집합

* ***DBMS*** : 데이터 베이스를 관리해주는 시스템 

## DB의 주요 특징
- 실시간 접근 가능(Real-time Accessibility)
- 데이터의 독립성(Data Independence)
- 데이터 동시 공유(Concurrent Sharing)
- 데이터 무결성(Data Integrity)
- 데이터 중복 최소화(Minimization of Data Redundancy)
- 데이터 보안(Data Security)
- 데이터 일관성(Data Consistency)
- 데이터의 지속성(Data Persistence)

## DBMS(DataBase Management System)
> 응용프로그램과 데이터베이스의 중개자로 데이터의 독립성 제공
```text
Application 
    ↓ ↑
DBMS
    질의 처리
    동시성 제어
    보안
    복구
    계정관리
    ...
    ↓ ↑
DB
```
## RDBMS(관계형 데이터베이스)
> 관계형 모델을 지원하는 DBMS
> 다양한 종류의 정보를 효울적으로 저장하고 처리하기 위해 사용

### 구성 요소
이름 | 설명
개체(Entity) | 현실 세계에서의 유/무형의 실체, 서로 독립적으로 존재하면서 분멸할 수 있는 특성을 갖고 있음
관계(Relationship) | 2개 이상의 개체 간의 상호 작용이나 연결을 정의한 것(ex. 1:1, 1:N, N;M관계)
속성(Attribute) | 개체가 포함하고 있는 하나 하나의 성질, 객체를 설명하는 세부 항목이나 특성

+ 용어 
> Table, Foreign Key, Column, row(record)

---

## SQL(Structed Query Language)
> RDBMS의 데이터를 관리하기 위해 설계한 특수 목적의 프로그래밍 언어
> ANSI SQL을 사용하면 이 표준을 따른 모든 DBMS에서 사용 가능

```text
Program <- SQL -> DBMS <--> DB
```

### SQL 구문의 종류
구분 | SQL 구분 | 설명
***DML*** | INSERT | 레코드 삽입
DML | UPDATE | 레코드 수정
DML | DELETE | 레코드 삭제
DML | SELECT | 레코드 조회, DQL
***DDL*** | CREATE | DB 객체 생성
DDL | ALTER | DB 객체 수정
DDL | DROP | DB 객체 삭제
DDL | RENNAME | DB 객체 이름 수정
TCL | COMMIT | 변화된 내용이 있다면 적용하며 트랜잭션 종료
TCL | ROLLBACK | 변화된 내용이 있어도 적용하지 않으며 트랜잭션 종료
TCL | WAVEPOINT | 트랜잭션의 일부만 콜백할 수 있는 저장점 생성
DCL | GRANT | 데이터베이스와 객체들에 대한 다양한 권한 부여
DCL | REVOKE | 데티어페이스와 객체들에 대해 부여된 권한 회수

### 데이터 검색(Select)

