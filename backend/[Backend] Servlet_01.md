# 🗓️ Servlet 학습 내용 정리(1)

## 🌐 웹 서버와 WAS

| 구분 | 설명 | 제공 콘텐츠 | 예시 |
|------|------|--------------|------|
| **웹 서버 (Web Server)** | 클라이언트(웹 브라우저)로부터 HTTP 요청을 받아 정적 콘텐츠(HTML, CSS, JS, 이미지 등)를 반환하는 프로그램 | 정적 콘텐츠 | Apache, Nginx |
| **WAS (Web Application Server)** | 웹 서버 + 웹 컨테이너. DB 조회나 로직 처리가 필요한 **동적 콘텐츠**를 생성하여 반환 | 동적 콘텐츠 | Tomcat, JBoss, WebLogic |

🧩 **정적 콘텐츠**: 매번 같은 결과물 제공  
⚙️ **동적 콘텐츠**: 요청마다 다른 결과물 생성

---

## ⚙️ WAS (Web Application Server)

> **웹 애플리케이션을 실행하고 관리하는 서버 환경 (미들웨어)**

### 💡 특징
- 웹 애플리케이션 동작에 필요한 실행환경 제공  
- `Web Container` 또는 `Servlet Container`라고도 함  
- JSP, Servlet 등의 실행 결과를 웹 서버로 전달  
- 대표 예시: **Tomcat**
  
---

## 🔁 클라이언트 → Web Server → WAS → DB 동작 과정

1. Web Server는 클라이언트로부터 **HTTP 요청** 수신  
2. 요청을 WAS로 전달  
3. WAS는 관련 **Servlet을 메모리에 로드**  
4. `web.xml` 참조 후 해당 Servlet에 대한 **Thread 생성 (Thread Pool 사용)**  
5. `HttpServletRequest`, `HttpServletResponse` 객체 생성 후 Servlet에 전달  
6. Servlet의 `service()` → `doGet()` / `doPost()` 실행  
7. 결과를 Response 객체에 담아 **WAS → Web Server → 클라이언트로 전송**  
8. 요청 처리 후 Thread와 Request/Response 객체 제거  

### ❓ Q&A
- **Q. WAS만 쓰면 되는가?**  
  → ❌ 정적 콘텐츠는 웹 서버가 처리해야 부하 분산 가능

- **Q. 왜 Apache Tomcat인가?**  
  → Tomcat 5.5부터 정적 콘텐츠 처리 기능이 추가되어,  
  성능 차이가 없어 **Apache Tomcat**으로 불림.

> 참고: [https://codechasseur.tistory.com/25](https://codechasseur.tistory.com/25)

---

## 🧱 Maven

> **Java 기반의 프로젝트 관리 도구**

### 주요 기능 ✨
1. 📦 **프로젝트 구조 표준화**  
2. 🔗 **의존성 관리 (pom.xml)**  
3. ⚙️ **빌드 자동화**

> 협업 시 동일한 개발 환경 유지 가능

---

## 🚀 Servlet

> WAS에서 실행되는 **Java Web Component**

### 💡 특징
- Java OOP 기반 → 유지보수성, 재사용성 우수  
- 하나의 Servlet 객체만 생성 → 멀티스레드로 요청 동시 처리  
- 필터, 리스너, 프레임워크(SPRING 등)와 통합 용이  
- Servlet Container에 의해 관리됨

### ⚠️ 단점
- 비즈니스 로직과 HTML 코드가 섞임 → MVC(Model2) 패턴으로 개선

---

### 🔗 URL Mapping

| 구분 | 설명 |
|------|------|
| **경로 지정** | `/hello`, `/user/list` 등 직접 경로 지정 |
| **확장자 매칭** | `*.do`, `*.jsp` 등 확장자로 매핑 |
| **설정 방법** | `web.xml` 또는 `@WebServlet` 애너테이션 사용 |

```java
@WebServlet(urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet { ... }
```


## 🌍 경로의 종류 (URL Path)

| 구분 | 설명 |
|------|------|
| **상대 경로** | `/`로 시작하지 않으며, **현재 파일의 위치**를 기준으로 계산되는 경로 |
| **절대 경로** | `/`로 시작하며, **서버의 루트 디렉토리(WAS 기준)** 또는 **Context Root** 기준 경로 |

### 📁 Container Root vs Context Root

| 구분 | 설명 |
|------|------|
| **Container Root** | WAS가 설치되어 실행되는 최상위 **물리적 디렉토리** (서버 관리자용) |
| **Context Root** | 특정 **웹 애플리케이션에 접근하기 위한 가상 경로** (URL의 도메인/포트 바로 뒤) |

📌 예시:
> ex. http://localhsot:8080/myapp/

---

## 🌐 Servlet 주요 개념 정리

### 🧩 Servlet 주요 API
- 모든 Servlet 클래스는 **`jakarta.servlet.http.HttpServlet`** 을 상속받음  
- HttpServlet은 요청(`HttpServletRequest`)과 응답(`HttpServletResponse`)을 처리하는 기본 구조 제공  

---

### 🔄 Servlet Life Cycle
> Servlet의 생성, 실행, 종료까지의 과정을 **WAS(Container)** 가 관리함

| 단계 | 호출 메서드 | 시점 및 역할 |
|------|--------------|--------------|
| 초기화 | `init()` | 최초 요청 시 1회만 호출. 초기 설정 및 리소스 로딩 수행 |
| 서비스 | `service()` | 요청마다 호출. 요청 방식(GET/POST)에 따라 `doGet()`, `doPost()`로 위임 |
| 소멸 | `destroy()` | WAS 종료 또는 재배포 시 1회 호출. 리소스 해제 및 정리 수행 |

> ✅ 개발자는 라이프사이클 훅 메서드 내에서 필요한 로직만 작성  
> → 객체 생성/호출은 컨테이너가 자동으로 관리  

---

### 📥 HttpServletRequest
- HTTP 요청 정보를 추상화한 객체  
- **헤더, 파라미터, 속성, 본문(body)** 등에 접근 가능  

**주요 메서드 예시**
```java
request.getParameter("name");
request.getSession();
request.getAttribute("key");
```

## 🌐 Servlet 요청 처리 과정

![Servlet Request Processing Flow](./img/backend/Servlet_request_processing_flow.png)

---

### 🔗 URL Mapping
> 클라이언트의 HTTP 요청을 특정 Servlet과 연결하는 방법

#### 🧭 URL 매핑 작성 방법
- **URL 경로 지정:** `/`로 시작하는 경로로 지정 가능  
- **확장자 매칭:** `*.확장자` 형태로 지정 (경로 지정과 함께 사용 불가)  
- **`web.xml`을 통한 설정**  
- **`@WebServlet` 애너테이션 활용**

**예시**
```java
@WebServlet(urlPatterns = {"/hello"})
public class ServletName extends HttpServlet {
    // ...
}
```
# 💡 Servlet 및 웹 애플리케이션 핵심 개념 요약

---

## 1. 🗺️ 경로 및 WAS 환경

### 경로의 종류
* **상대 경로**: /로 시작하지 않고 **현재 파일의 위치** 기반으로 계산하는 경로
* **절대 경로**: /로 시작하는 경로

### 컨테이너 Root와 컨텍스트 Root
| 개념 | 설명 | 특징 |
|:---|:---|:---|
| **컨테이너 루트** | WAS가 설치되어 실행되는 최상위 **물리적 디렉토리** | 웹 요청과 직접 관련 없고 서버 관리자가 사용 |
| **컨텍스트 루트** | 특정 **웹 애플리케이션**에 접근하기 위한 최상위 **가상 경로** | URL에서 도메인/포트 바로 다음에 옴 (ex. `http://localhsot:8080/myapp/`) |

---

## 2. 🧩 Servlet의 기본 구조 및 생명 주기

### 서블릿 주요 API
* 모든 서블릿 클래스는 **`extends jakarta.servlet.http.HttpServlet`**을 상속받습니다.

### Servelt Life Cycle (컨테이너 관리)
개발자는 Servlet 객체를 직접 만들거나 호출하지 않고, **Container**가 Life Cycle에 따라 관리합니다.

| 단계 | 호출 메소드 | 시점 및 역할 |
|:---|:---|:---|
| **초기화** | `init()` | **최초 요청 시 1회만 호출**. Servlet 객체 생성 후, 초기 설정이나 리소스 로딩 수행. |
| **서비스** | `service()` | **모든 요청마다 호출**. 클라이언트 요청 처리 및 `doGet()`, `doPost()` 등으로 위임. |
| **소멸** | `destroy()` | WAS 종료 또는 애플리케이션 재배포 시 1회 호출. 리소스 해제 및 안전한 종료. |

---

## 3. 📥 요청 (HttpServletRequest) 및 📤 응답 (HttpServletResponse)

### 📥 HttpServletRequest
HTTP 요청을 추상화한 인터페이스로 헤더, 파라미터, 속성 및 요청 본문 정보에 접근하는 메서드를 제공
* **주요 예시**: `HttpSession.getSession()`, `getAttribute()`, `getParameter("name")`

#### HTTP Method (GET/POST)
| Http 메소드 | 요청에 body가 있음 | 안전 (Safe) | **멱등 (Idempotent)** | 캐시 가능 |
|:---|:---|:---|:---|:---|
| GET | 선택 사향 | 예 | 예 | 예 |
| POST | 예 | 아니오 | 아니오 | 예 |
* **멱등성**: 특정 연산을 동일한 파라미터로 여러 번 연산하더라도 그 결과는 동일함.
* **POST가 멱등하지 않은 이유**: 주로 **서버의 상태를 변경**(데이터 생성 등)하는 작업을 수행하기 때문에 반복 시 결과가 달라질 수 있음

#### Request Parameter
* `<form>` 또는 Query String을 통해서 클라이언트가 전달한 값이며, 언제나 **문자열**
* Servlet이 처음 할 일은 파라미터를 추출하고 이를 검증하는 것

### 📤 HttpServletResponse
HTTP 응답을 추상화한 인터페이스로 헤더 조회/설정, 응답 상태, 응답 전송을 위한 작업 처리 기능을 제공
* **주요 예시**: `getWriter()`, `addCookie()`, `sendError()`, `sendRedirect()`

#### Http Status
* 웹 서버가 클라이언트의 요청을 처리한 결과를 카테고리별 숫자 코드로 반환.
* **404 (Client Error)**: 요청한 리소스를 찾을 수 없음.
* **500 (Server Error)**: 서버 내부 오류 발생 상태.

#### Content-Type과 Character Encoding
* **Content-Type**: 서버가 전송하는 데이터의 **MIME Type**으로 데이터 형식과 인코딩 방식 포함.
    * **주요 Type**: `text/plain`, `text/html` (기본 값), `application/json`, `image/jpeg`
* **Character Encoding**: 데이터를 컴퓨터가 이해하고 처리할 수 있는 형태로 변환하는 방법.
    * 응답의 기본 encoding은 **ISO-8859-1**로 한글 전송이 불가

**한글 처리를 위한 UTF-8 설정:**
```js
response.setContentType("text/html;charset=UTF-8"); // response를 통해 출력하기 전에 설정
```

