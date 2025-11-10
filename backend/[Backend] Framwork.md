# 🤖  Framwork 학습 요약

## 📚 목차

- ***

---

## 1. Framwork

### 1-1. Framwork란?

- 비지니스 로직이 빠진 뼈대만 갖춰진 반 제품 형태의 애플리케이션

#### Spring Framework
- 자바 애플리케이션 개발을 위한 경량 framework

- 프레임워크는 비지니스 로직과 무관한 귀찮고 어려우며 모듈화 할 수 있는 일들 담당
- 개발자는 프레임워크에 적합한 비지니스 로직을 공급하는데 집중

#### 주요 특징
>  POJO 기반의 DI, AOP, PSA

1. **DI**(Dependency Injection: 의존성 주입)
> 의존성을 없앨 수 있을까? -> 의존성은 없앨 수 없다. 
> 그럼 DI는 뭘 하는건가? -> 의존성을 관리하는 것
- 의존성을 주입 받아 사용하므로 의존성이 변경되더라도 의존하는 객체는 변경될 필요가 없음
- **코드의 유지보수성 향상**


2. **AOP**(Aspect Oriented Programming: 관점 지향 프로그래밍)
- 비즈니스 로직에서 부가적인 횡단 관심사 코드를 분리/모듈화하고 필요한 곳에 적용
- **핵심 관심사에 집중**


3. **PSA**(Portable Service Abstraction: 서비스를 추상화 해서 자유롭게 바꿔 낄 수 있게 함)
> **추상화란?** 복잡한 구현을 숨기고 단순히 접근하게 함
- 어렵고 복잡한 개념을 특정 환경에 종속되지 않고 쉽게 사용할 수 있게 추상화된 레이어 제공
    - interface 및 구현체
- JPA를 쓰건 MyBatis를 쓰건 스프링에서 T.X 처리하는 방법은 동일

4. **POJO**(**Plain Old Java Object)
- 비지니스 로직 구현을 위해서 별다른 방법 없이 평범한 객체를 만들면 됨


---

## 2. SLF4J와 logging

### 2-1. logging

#### logging의 필요성
- 디버깅
- 장기간 동작하는 시스템 상태에 대한 기록

#### Slf4j(Simple Logging Facade for Java)
- java에서 logging을 위한 facade
    - facade 패턴은 라이브러리의 복잡한 구조를 단순화 시켜 인터페이스로 제공하는 패턴(ex. JDBC)
- Slf4j의 구현체는 log4j,logback 등

---

### 2-2. Slf4j

#### 특징
- 심각도에 따라 trace < debug < info < warn < error 다섯 단계로 로그 구분
- 사용자 설정에 따라서 확인할 로그 레벨 결정
    - 개발 과정 : trace~error log 볼 수 있음
    - 운영 과정 : info~error log 볼 수 있음
- 로그 레이아웃 설정을 통해 다양한 정보 제공
    - 언제 어디서 발생한 에러인지 알 수 있음
- 다양한 Appender 제공

---

## 3. JUnit

### 3-1.JUnit
- Java 코드의 단위 테스트 자동화를 위함 프레임워크
- 테스트와 자동화의 필요성
    > 테스트 성공/실패를 판단하는 근거는? 
    > 콘솔에 출력해서 확인하는 방식은 효율적이지 못함
- 의존성 추가 

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

- 테스팅 life cycle 관련 기본 annotation
| annotation| 설명 |
|------------------|----------------------------------------|
|`@Test`| 대상 메서드가 단위 테스트를 수행하는 테스트 메서드임을 나타냄|
|`@BeforeEach` `@AfterEach`|**각각의** @Test 전/후에 공통적으로 처리할 사전/사후 작업 메서드 정의|
|`@BeforeAll`, `@AfterAll` | **모든** @Test에 거쳐 한번 만 필요한 사전/사후 작업 메서드 정의 **static 메서드**에 작성| 
|`@DisplayName`|테스트의 내용을 나타내는 문자열| 

---

### 3-2. assertion
- 단정문 : 메서드 호출 결과가 예상한 값과 동일한지 판단하는 문장
- org.junit.jupiter.api.Assertions에 다양한 assertion 메서드들이 static하게 제공됨
- standard assertion
    - 대부분 assertion 메서드들은 긍정과 부정이 쌍으로 존재
    - [assertEquals : assertNot Equals], [assertNull : assertNotNull], ...

#### 기본적인 단정문들

- equals vs same : equals 또는 ==에 대한 검증
- 배열의 내용 검증 : 배열의 경우 equals가 재정의 되지 않기 때문에 별도의 내용 비교 메서드 제공
    - assertArrayEquals
- 예외에 대한 검증 : 특정 상황에서 예상하는 예외가 잘 발생하는지에 대한 검증
    - assertThrows
- 여러 assertions들을 함께 그룹핑해서 검증
    - grouping 하지 않으면 중간에 하나만 실패하면 전체가 바로 종료
    - assertAll
