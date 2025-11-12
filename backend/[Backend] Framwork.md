# 🤖 Framework & Testing 학습 요약

## 📚 목차
1. [Framework](#1-framework)
2. [SLF4J와 Logging](#2-slf4j와-logging)
3. [JUnit과 단위 테스트](#3-junit)

---

## 1. Framework

### 1-1. Framework란?
- 비즈니스 로직이 빠진 **뼈대만 갖춘 반제품 형태의 애플리케이션**
- 개발자는 **비즈니스 로직 구현**에 집중, 반복적/공통적 작업은 프레임워크가 담당

#### 🌟 Spring Framework
- **자바 애플리케이션 개발용 경량 Framework**
- 특징
  - POJO 기반
  - DI(Dependency Injection)
  - AOP(Aspect-Oriented Programming)
  - PSA(Portable Service Abstraction)

---

### 1-2. 주요 특징

| 개념 | 설명 | 효과 |
|------|------|------|
| **DI** | 의존성 주입. 의존 객체를 외부에서 주입 | 유지보수성 향상, 결합도 감소 |
| **AOP** | 핵심 로직과 부가 관심사(로그, 트랜잭션 등) 분리 | 핵심 로직 집중, 코드 재사용성 ↑ |
| **PSA** | 서비스 추상화 → 구현체 교체 자유 | 특정 구현에 종속되지 않고 재사용 가능 |
| **POJO** | 평범한 자바 객체 | 비즈니스 로직 구현 단순화 |

> 💡 **DI 예시**
```java
@Component
class MovieService {
    private final MovieRepository repo;
    @Autowired
    public MovieService(MovieRepository repo){
        this.repo = repo;
    }
}
```

> 💡 AOP 예시
```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example..*Service.*(..))")
    public void logBefore(JoinPoint joinPoint){
        System.out.println("Method called: " + joinPoint.getSignature());
    }
}
```

---

## 2. SLF4J와 Logging

### 2-1. Logging의 필요성
- **디버깅** 🐞  
- **장기간 동작하는 시스템 상태 기록** 📜  
- 문제 발생 시 원인 추적 및 시스템 분석 용이

---

### 2-2. SLF4J (Simple Logging Facade for Java)
- **Facade 패턴** 적용 → 다양한 로그 라이브러리(Logback, Log4j 등)를 통합
- **주요 특징**
  - 심각도 레벨: `TRACE < DEBUG < INFO < WARN < ERROR`
  - 환경별 로그 출력 조절 가능
    - 개발: TRACE ~ ERROR  
    - 운영: INFO ~ ERROR
  - 로그 레이아웃 설정 가능 → **언제, 어디서, 어떤 문제 발생** 알 수 있음
  - 다양한 **Appender 제공** → 콘솔, 파일, DB 등

---

### 2-3. SLF4J 사용 예시
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyService {
    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    public void process() {
        logger.trace("Trace 레벨 로그");
        logger.debug("Debug 레벨 로그");
        logger.info("Info 레벨 로그");
        logger.warn("Warn 레벨 로그");
        logger.error("Error 발생!", new RuntimeException("예외 발생"));
    }
}
```

---

## 3. JUnit

### 3-1. JUnit 개요
- Java 코드 단위 테스트 자동화 프레임워크
- **테스트 필요성**
  - 테스트 성공/실패 근거 확보
  - 콘솔 출력만으로 확인하는 방식은 비효율적

#### 의존성 추가 (Maven)
- `junit-jupiter-api`를 `test` scope로 추가  
  (테스트 코드 배포 시 제외 가능)

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.12.0</version>
    <!-- test/compile로 scope 설정 가능하다 -->
    <!-- 왜 나눠져 있냐? 배포할 때는 테스트 코드가 필요없기 때문-->
    <scope>test</scope>
</dependency>
```

---

### 3-2. Testing Life Cycle Annotation

| Annotation | 설명 |
|------------|------|
| `@Test` | 단위 테스트 메서드 지정 |
| `@BeforeEach` / `@AfterEach` | 각 테스트 전/후 실행, 공통 사전/사후 작업 정의 |
| `@BeforeAll` / `@AfterAll` | 모든 테스트 전/후 단 한 번 실행 (static 메서드) |
| `@DisplayName` | 테스트 설명 문자열 지정 |

---

### 3-3. Assertion (단정문)
- 메서드 결과가 예상값과 동일한지 검증
- org.junit.jupiter.api.Assertions에서 다양한 static assertion 제공
- 대부분 assertion 메서드는 **긍정/부정 쌍** 존재
  - 예: `assertEquals` / `assertNotEquals`, `assertNull` / `assertNotNull`

#### 주요 Assertion
- 값 검증: `assertEquals`, `assertNotEquals`
- 배열 내용 검증: `assertArrayEquals`
- 예외 검증: `assertThrows`
- 여러 assertions 그룹 검증: `assertAll`
  - 중간 하나 실패에도 전체 실행 가능

---

### 3-4. 테스트 작성 패턴

#### 1️⃣ Given-When-Then (BDD)
- **given**: 테스트 전 상황 준비 (객체, 데이터 등)
- **when**: 테스트 대상 메서드 실행
- **then**: 결과 검증
- 권장 패턴이지만 강제는 아님

#### 예시 흐름
- 테스트 객체/데이터 준비 → 메서드 실행 → 결과 검증

```java
public void seccessNew(){
    //given
    Calculator calc = new Calculator();
    int a = 10;
    int b = 5;
    //when
    int result = calc.divide(a,b);
    //then
    assertEquals(result, 10/5); 
}
```

---

### 3-5. JUnit 단위 테스트 F.I.R.S.T 원칙 상세

| 원칙 | 설명 |
|------|------|
| **Fast** | 테스트 빠르게, 외부 환경 의존 최소. `@SpringBootTest`는 통합 테스트 → 단위 테스트 시 슬라이스 테스트(`@DataJpaTest`) 권장 |
| **Independent** | 각 테스트 독립적, 다른 테스트 상태에 의존 X |
| **Repeatable** | 여러 번 반복해도 동일 결과, DB 테스트 시 rollback 필요 |
| **Self-Validating** | 테스트 자체만으로 결과 검증 가능 |
| **Timely** | TDD 시 테스트는 production code 작성 전 수행 |

---

#### 1️⃣ Fast (빠름)
- 단위 테스트는 **빠르게 실행**되어야 함
- 외부 환경(데이터베이스, 네트워크 등)에 의존하지 않고 **독립적으로 작성**  
- 느린 테스트는 개발 과정에서 자주 실행하기 어렵고, TDD 흐름을 방해함
- 예시:
  - `@SpringBootTest` → 통합 테스트, 느림 → 단위 테스트에서는 지양
  - `@DataJpaTest` 등 **슬라이스 테스트** 사용 권장 → 필요한 부분만 테스트


#### 2️⃣ Independent (독립적)
- **각 테스트는 다른 테스트에 의존하지 않아야 함**
- 어떤 순서로 실행되더라도 항상 동일하게 성공해야 함
- 예시:
  - 잘못된 경우: insert 테스트 → select 테스트 의존
  - 올바른 경우: setup에서 insert → select 테스트 후 delete 처리 → 다른 테스트에 영향 없음
- 목적: 테스트 간 상호 영향 최소화 → **재현 가능성 보장**


#### 3️⃣ Repeatable (반복 가능)
- **여러 번 반복해도 결과가 동일**해야 함
- 외부 상태에 따라 테스트 결과가 달라지면 안 됨
- DB 연관 테스트 시:
  - 테스트 후 상태 rollback 처리 필요
  - 테스트마다 초기 상태 보장


#### 4️⃣ Self-Validating (자기 검증 가능)
- 테스트 자체만으로 **결과 검증 가능**해야 함
- 개발자가 수동으로 확인할 필요 없어야 함
- 예시:
  - `assertEquals(expected, actual)`
  - `assertThrows(Exception.class, () -> {...})`


#### 5️⃣ Timely (적시성)
- 테스트는 **production 코드 작성 전** 진행
- TDD(테스트 주도 개발)에서는 코드 작성 전에 테스트 먼저 작성
- 목적: 테스트 작성 → 실패 확인 → 코드 작성 → 테스트 통과 → 리팩토링  
- 순서 예시:
  1. 테스트 케이스 작성 (fail 예상)
  2. 기능 코드 작성
  3. 테스트 통과 확인
  4. 리팩토링 → 테스트 통과 확인
