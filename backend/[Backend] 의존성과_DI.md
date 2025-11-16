# 🌱 스프링 DI(Dependency Injection) 학습 요약

## 📚 목차
1. DI 기본 개념  
2. 명시적 DI  
3. 묵시적 DI  
4. DI 방식 비교  

---

## 1. 의존적 DI (Dependency Injection)

### 1-1. 핵심 개념 요약

| 개념 | 설명 | 핵심 키워드 | 비유 (생활/Spring) |
|:---|:---|:---|:---|
| **DI** (의존성 주입) | 객체의 **의존성(멤버 변수)**을 외부에서 **주입**하는 것. | 외부 주입, 멤버 변수 설정 | 세탁기 **랜탈** |
| **빈 (Bean)** | Spring Framework에 의해 **생성되고 관리되는** 자바 객체. | 스프링 관리 객체 | 세탁기 |
| **의존 관계** | 어떤 객체가 비즈니스 로직 처리를 위해 다른 객체에 의존하는 관계 (**has-a** 관계). | has-a 관계, 로직 처리 | 빨래하는 사람 → 세탁기 |
| **IoC** (제어의 역전) | 객체의 생성, 의존 관계 설정, 생명주기 관리 등 **객체 제어 권한**이 개발자 → **Spring 컨테이너**로 넘어가는 것. | 제어권 전환, Spring 처리 | 렌탈회사 ≈ IoC 컨테이너 |

#### 🏡 DI (의존성 주입)의 생활 속 비유

| Spring 개념 | 생활 속 행위 | 설명 | 핵심 키워드 |
|:---|:---|:---|:---|
| **의존성** | 세탁기 (빨래라는 비즈니스 로직에 필요) | 사용하려는 대상 객체 | Has-a 관계 |
| **생성** (개발자 직접 관리) | **세탁기 구매** | 사용자가 직접 객체를 만들고(new) 관리하는 것. (DI 미적용) | 직접 생성/관리 |
| **주입** (DI) | **세탁기 랜탈** (또는 서비스 이용) | 사용자가 직접 만들지 않고, 외부(렌탈 회사/컨테이너)에서 객체를 **제공받아** 사용하는 것. | 외부 주입/제공 |

---

### 1-2. 의존 관계와 DI

#### 스프링의 빈(Bean)이란? 
- 스프링 프레임워크에 의해 **생성되고 관리되는** 자바 객체
- 스프링은 빈의 생성, 의존 관계 설정, 객체 관리 등 빈의 라이프 사이클을 관리
    - 스프링을 빈의 컨테이너라고 함

#### 의존 관계란?
- 어떤 객체(빈)가 비지니스로직 처리를 위해 다른 객체(빈)에 의존하는 관계
- 객체 각 has-a 관계

- 의존 관계 관리를 위해 DI 개념을 사용하며 이 과정에서 **제어의 역전**이 발생
    - 스프링을 IoC(제어의 역전; Inversion Of Control) 컨테이너라고 불림
    - 렌탈회사 = IoC 컨테이너

#### 🛠️ DI의 필요성 및 Spring 등장

| 단계 | 문제점 | 해결 방안 | 결과 |
|:---|:---|:---|:---|
| 1. 직접 생성/사용 | 객체 수정 필요 시, **사용자 객체 코드를 직접 수정**해야 함. | 인터페이스 도입 | 여전히 사용자 객체가 의존성을 `new`로 직접 생성. |
| 2. 인터페이스 사용 | 의존성을 직접 생성하여 사용하므로, **변경 시 수정 필요**. | **Factory Pattern** 적용 (외부 주입) | 의존성을 직접 생성하지 않고 **외부에서 주입**받음. 유연성 확보. |
| 3. Factory 복잡성 | Factory를 구현하고 관리하는 것이 **복잡하고 귀찮음**. | **Spring Framework** 도입 | **POJO** 작성하면 Spring이 설정에 따라 **자동으로 빈을 생성 및 주입** (**DI**). 제어의 역전 (IoC) 발생. |


#### ⚙️ Spring의 빈 관리 과정

1.  **개발자:** POJO로 빈 작성.
2.  **전달:** 빈의 생성/관계 설정 메타 정보(≈ 설정 파일)를 **Spring 컨테이너**에 전달.
3.  **런타임:**
    * Spring은 메타 정보로 **빈 객체 생성** (주로 **싱글턴** 형태).
    * 빈 관계 정보에 따라 **빈 주입 처리** (DI).

---

## 2. 명시적 DI

### 2-1. 명시적 DI
- **개발자가 직접 빈 생성과 주입 과정을 명시적으로 설정 파일에 작성**하는 방식  

#### 🔖 주요 어노테이션 정리

| 어노테이션 | 설명 | 적용 위치 |
|:-------------|:------|:-----------|
| `@Configuration` | 이 클래스가 **Spring 설정 파일**임을 명시. | 클래스 |
| `@Bean` | 이 메서드가 반환하는 객체를 **Spring 빈으로 등록**. | 메서드 |

#### 빈 설정 예시 코드
```java
@Configuration // 여기는 설정 정보가 담겨있다
public class WasherConfig {
    @Bean // 메서드를 통해서 빈 생성
    public SWasher sWasher() { // SWasher 타입의 빈 생성, 빈 이름은 sWasher
        return new SWasher();  // 실제로 사용될 빈 객체 반환
    }

    @Bean
    public LWasher lWasher() {
        return new LWasher();
    }

    @Bean(name = "myWasherUser") // 빈 이름을 myWasherUser로 설정
    public WasherUser washerUser() {
        // return new WasherUser(sWasher());       // 생성자를 통한 빈 주입
        WasherUser washerUser = new WasherUser();
        washerUser.setWasher(sWasher());           // WasherUser의 setter를 이용한 주입
        return washerUser;
    }
}
```
---

#### 빈 사용 예시 코드

```java
public class WasherUserTest {
    public static void main(String[] args){
        ApplicationContext ctx = new AnnotationConfigApplicationContext(WasherConfig.class); //설정 정보로 컨테이너 생성

        Washer sWasher = ctx.getBean(SWasher.class);    // SWasher 타입의 빈을 가져옴

        WasherUser user = ctx.getBean(WasherUser.class); // WahserUser 타입의 빈을 가져옴
        user.useWahser("옷");

        System.out.println(sWahser==user.getWasher);     // 같은 객체인지 확인
    }
}

```
> 만약  
> `Washer sWasher = ctx.getBean(Washer.class);`  
> 로 **Washer 타입의 빈을 가져오려면 어떻게 될까?**  
>
> 👉 **에러 발생!**  
> 어떤 빈을 줘야 하는지(여기에서는 `SWasher`와 `LWasher`) 알 수 없기 때문에 에러가 발생함.  
>
> 따라서 **동일 타입이 여럿일 경우**,  
> 타입 + 이름 기반으로 빈 조회가 가능함 👇  
>
> `Washer myWasher = ctx.getBean("sWasher", Washer.class);`
---

## 3. 묵시적 DI

### 3-1. 묵시적 DI 
- 명시적 Di 처럼 `@Configuration`에서 `@Bean` 을 사용하지 않는 형태

#### `@Component`
- 빈으로 사용할 각각의 클래스들에 `@Component` 표시

``` java
@Target(ElementType.TYPE)
public @interface Component {
    String value() default ""; // 생성되는 빈의 이름을 재정의 하려는 경우 사용
}
```

#### 기본 빈의 이름

- 클래스 이름이 Pascal case인 경우는 첫 글자를 소문자로 한 camel case
- 그렇지 않은 경우는 클래스 이름 그대로 사용
- @Component value 속성으로 재정의 가능

---

### 3-2. 묵시적 DI Annotation

#### 1. 의존성 자동 주입 - `@Autowired`
- **빈을 주입**하기 위해 사용되는 annotation

```java
public @interface Autowired {
    boolean required() default true;
}
```

- 타입 기반으로 빈 자동 주입
    > 단 해당 빈은 반드시 **하나만 존재** 해야 함
    >
    > **타입 충돌**이 발생할 경우 **이름 기반의 조건을 추가**하기 위해 `@Qualifier` 활용

- 생성자와 메서드에서 사용 시 대상 파라미터는 모두 **Spring Bean** 또는 `@Value`에 의한 **scalar 값**
- 한 클래스에 `@Autowired`에 적용된 **생성자**는 **최대 하나**만 가능하며 **메서드는 여러 번** 사용 가능

#### 2. `@ComponentScan`
- `@Component`는 단지 "빈이 될 수 있다"는 의도로 **실제 빈을 만들지는 않음**
- 빈을 scan해야 빈으로 등록됨
- **backPackages**로 scan  대상 패키지 등록
    - 생략 시 현재 애너테이션이 사용된 클래스의 하위 패키지 scan
- `@Configuration`에 선언된 클래스에 사용

```java
@Target(ElementTye TYPE)
public @interface ComponentScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {}; // Component를 찾아볼 package 등록 -> 하위 패키지 모두 scan

}
```

#### 3. `@Qualifier`
- 이름 기반으로 주입될 빈을 한정 짓기 위해서 사용하는 annotation(`@Autowired`와 함께 사용)

#### 4. `@Component`
- 특별한 의미를 가지지 않은 단순히 "빈의 대상"임을 나타내는 애너테이션
- 스테레오타입
    - 용도에 따라 미리 여러 형태로 정형화 해놓은 타입
    - 내부적으로 `@Component` 포함, 빈으로 관리됨
- 스테레오타입 애너테이션들
- 스프링 계층(역할)을 명학히 인식하도록 하기 위해 사용된다. 
    > 그냥 @Component만 있다면 서비스인지 레포지토리인지 일반 빈인지 알 수 없음. 
    >
    > 따라서 역할을 명시함으로써 어떤 빈인지를 나타내는 역할을 함

| annotation | 설명 | 
|-------------|-----------------------------------------------------|
|`@Repository`| MVC에서 Model의 Repository(DAO) 계열 빈에 사용 |
|`@Service`| MVC에서 Model의 Service 계열의 빈에 사용 |
|`@Controller`| MVC에서 controller로 사용되는 빈에 사용 | 
|`@Configuration`|java 기반의 메타정보 클래스에 사용|
|`@Component`| 다른 스테레오 타입 애너테이션에 해당되지 않을 경우 사용| 

---

### 3-3. DI 방법의 비교

#### 주입 방식에 따른 DI 방법의 비교
#### ✔ 생성자 주입 (권장)
- 의존성이 **필수**임을 명확히 표현  
- 필드를 `final`로 두어 **불변성 확보**  
- 순환 의존성 문제를 **컴파일 시점에 빠르게 발견**  
- 테스트 용이

#### ✔ Setter 주입 (옵션성 의존성)
- 선택적 의존성에 적합  
- 런타임에 수정 가능

#### ✔ 필드 주입 (비권장)
- 코드 간결하지만  
- 테스트가 어려움(Mock 주입 불가)  
- 불변성 보장 불가


#### 설정 방식에 따른 DI 방법의 비교

| 항목 | 명시적 DI | 묵시적 DI |
|------|-----------|-----------|
| 빈 설정 방식 | @Bean으로 직접 정의 | @ComponentScan + @Component |
| **외부 라이브러리 빈 등록(중요)** | 가능 | 불가 |
| 주된 용도 | 설정 제어가 필요할 때 | 대부분의 서비스/레포지토리 |
| 권장 패턴 | 묵시적 DI 중심 + 명시적 DI 보조 |
