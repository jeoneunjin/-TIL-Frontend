# User Model, Service, Dao 구현(+ Mybatis까지 작성)

## 📑 목차

---

## 0. User DTO
| 컬럼 | 타입 | 
|---------|-------------------|
|id|bigint `primary key`|
|email|varchar(255) `NN`|
|password_hash|varchar(255) `NN`|
|name|varchar(100)|
|brith_date|date|
|gender|varchar(20)|
|home_country_code|varchar(2)|
|created_at|datetime `NN`|
|updated_at|dateTime `NN`|

### **왜 `created_at`와 `updated_at`이 필요한가?**
이 두 필드는 DB 레코드의 메타데이터로 사용되며, 개발 및 운영 전반에 걸쳐 유용함

#### 용도 및 사용 시점

|필드 | 용도 및 사용 시점 |
|---------|----------------------------------|
|`created_at`|**데이터가 처음 생성된 시점.** 레코드가 DB에 **INSERT**될 때 한 번만 설정, 이후 변경되지 않음| 
|`updated_at`|**데이터가 마지막으로 수정된 시점.** 레코드가 DB에서 **UPDATE**될 때마다 현재 시간으로 갱신|

#### 활용 예시

1. **감사(Audit) 및 이력 추적:** 사용자의 정보가 언제 변경되었는지 파악하여 버그 추적이나 보안 이슈 발생 시 이력을 확인할 수 있음

2. **캐시 무효화:** 데이터가 마지막으로 업데이트된 시점을 기준으로 캐시(Cache)를 무효화할지 결정할 수 있음

3. **정렬 및 통계:** 최신 가입자, 가장 최근에 정보를 수정한 사용자 등을 조회하거나 통계 자료를 생성할 때 유용

4. **데이터 무결성:** 특정 시점 이후에 생성되거나 수정된 데이터만 처리할 때 필터링 기준으로 사용

---


## 1. 프로젝트 구조(User 기능 중심으로)
``` text
└── src/main/java/com/example/demo
    ├── auth                // 인증/인가 관련 패키지
    │   ├── controller      // AuthController.java (로그인/로그아웃 요청 처리)
    │   └── service         // AuthService.java (인증 로직, JWT 처리)
    │
    ├── user                // 사용자 데이터/CRUD 관련 패키지
    │   ├── model           // User.java (DB 모델)
    │   ├── dao             // UserMapper.java (DB 인터페이스)
    │   └── service         // UserService.java/UserServiceImpl.java (CRUD 비즈니스 로직)
    │
    └── controller          // 일반적인 웹/API 컨트롤러 (필요 시 생성)
    └── config 
        └── ...                  (MyBatis 설정, Security 설정 등)
    └── controller 
        └── UserController.java  (웹 요청 처리)
```

---

## 2. DB_MySQL

### 2-1. 새로운 계정 생성 + 새로운 커넥션

#### 🔑 새로운 계정을 생성해야 하는 이유

1. 보안
    - **최소 권한 원칙 :** 애플리케이션에 필요한 최소한의 권한만 가진 계정을 사용하는 것이 보안의 기본
    - **리스크 차단 :** 만약 모든 프로젝트가 하나의 root 또는 master 계정을 사용한다면, 한 프로젝트의 계정 정보가 유출되었을 때 모든 데이터베이스(다른 프로젝트의 DB 포함)가 위험에 노출됨
    - **분리 :** 프로젝트별로 계정을 분리하면, 해당 계정이 유출되더라도 접근 가능한 데이터베이스는 해당 프로젝트의 스키마로만 제한됨

2. 권한 관리 및 역할 분리
   - **애플리케이션 계정 :** Spring Boot 애플리케이션이 런타임에 데이터를 읽고 쓰는(CRUD) 데 필요한 권한만 부여
     - 테이블 구조를 변경하거나 데이터베이스를 삭제하는 권한은 부여하지 않음
   - **개발자/관리자 계정 :** 개발자가 Workbench를 통해 테이블을 생성, 수정하거나 백업/복구 등 관리 작업을 수행할 때 사용

3. 환경 분리(Dev vs Prod)
새로운 계정을 생성할 때 아예 개발용 계정과 운영용 계정을 분리하는 것이 가장 좋음
   - **개발 :** 넓은 권한(테스트 데이터 수정/스키마 변경 가능)
   - **운영 :** 제한적인 권한(CRUD만 사용, DROP/ALRTER 금지) 

---

### 2-2. Test용 SQL 작성

```sql
CREATE TABLE user (
    -- 기본 정보
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,          -- 로그인 ID (UNIQUE)
    password_hash VARCHAR(255) NOT NULL,         -- 암호화된 비밀번호
    name VARCHAR(100) NOT NULL,
    
    -- 추가 정보
    birth_date VARCHAR(10),                      
    gender CHAR(1) NOT NULL,                     
    home_country_code VARCHAR(10),               
    
    -- 메타 데이터 (생성 및 수정 시각)
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at DATETIME NOT NULL DEFAULT NOW() ON UPDATE NOW() -- 수정 시 자동 갱신
);
```

#### findByEmail을 위한 인덱스 추가(최적화)

DB에서 원하는 데이터를 **빠르게 찾을 수 있도록 돕는 구조**
- **검색 속도 향상 :** 데이터베이스가 테이블의 모든 행을 처음부터 끝까지 읽지 않고, 인덱스를 통해 바로 해당 데이터가 저장된 위치를 찾아가게 해줌
- **정렬 속도 향상 :** 인덱스는 이미 정렬된 형태로 저장되므로, 데이터를 정렬(ORDER BY)하여 조회할 때도 성능이 향상됨

```sql
-- 이메일 기반 검색을 위한 인덱스 추가
CREATE INDEX idx_user_email ON user (email);
```
#### 추가 설명

> **인덱스**는 **데이터베이스 서버 자체의 기능**으로, 
> 테이블에 저장된 데이터를 물리적으로 빠르게 찾을 수 있도록 돕는 구조
> `UserMapper.xml`에 정의된 `SELECT` 쿼리가 실행될 때,데이터베이스는 `WHERE email = #{email}` 조건을 처리
> 이때 `email` 컬럼에 인덱스가 있으면, DB 엔진은 인덱스를 이용하여 해당 이메일의 위치를 즉시 찾아냄

---