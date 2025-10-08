# 🗓️ Session & Cookie 정리

---

## 🎯 학습 목표
- HTTP의 Stateless 특성과 상태 관리 필요성 이해  
- Cookie의 생성, 전송, 확인 과정 이해  
- Path, MaxAge 등 쿠키 속성 이해 및 사용 방법 숙지  

---

## 💡 주요 키워드
- HTTP: Stateless, Request/Response, Header  
- Cookie: name, value, path, maxAge, secure, httpOnly  
- Session: 서버 측 상태 관리  

---

## 🌐 HTTP 특징
- **Stateless**: 서버가 클라이언트 상태를 기억하지 않음

### Stateless한 이유
| 이유 | 설명 |
|------|-----|
| 단순성 | 각 요청이 독립적 처리 → 서버 구성 단순, 속도 빠름 |
| 확장성 | 서버 간 상태 공유 불필요 → 부하 분산 용이 |
| 신뢰성 | 한 요청 실패가 다른 요청 영향 없음 → 신뢰도 ↑ |
| 자원 절약 | 서버 메모리/저장 공간 절약 |

> Stateless를 보완하기 위해 **Cookie, Session** 사용  

---

## 🍪 Cookie

### Cookie 정의
- 서버에서 생성 → 클라이언트(브라우저)에 저장 → 요청 시 서버 전송  

### 쿠키 동작 과정
1. 브라우저 → 서버: 최초 요청  
2. 서버: 쿠키 생성  
3. 서버 → 브라우저: `Set-Cookie` 헤더 전송  
4. 브라우저: 쿠키 저장  
5. 브라우저 → 서버: 요청 시 `Cookie` 헤더 전송  

---

### 쿠키 주요 속성

| 속성 | 설명 | 특징 |
|------|------|------|
| name | 쿠키 이름 (유일) | 알파벳, 숫자, `하이픈(-)`, `언더바(_)`, `틸트(~)`, `.` 가능; 공백/한글 등은 URLEncoder 필요 |
| value | 쿠키 값 | name과 동일 규칙, 한글 가능 |
| path | 유효 경로 | 설정된 하위 경로 요청에서만 전송, 미지정 시 발급 URL 디렉토리 / `/` 지정 시 모든 페이지 전송 가능 |
| maxAge | 유효 기간 | 양수: 초 단위 존재, 0: 즉시 삭제, 음수/미지정: 세션 쿠키(브라우저 종료 시 삭제) |
| secure | HTTPS 전용 | true: HTTPS에서만 전송 |
| httpOnly | JS 접근 제한 | true: JavaScript에서 접근 불가 |

---

### 쿠키 Path 예시
```http
Set-Cookie: sessionId=abc123; Path=/app
```
> 의미 : 이 쿠키는 /app 경로와 그 하위 경로에서만 브라우저가 서버로 전송함

#### 🚫 쿠키가 전송되지 않는 경우 예시
| 요청 URL            | 쿠키 전송 여부 |
| ----------------- | -------- |
| /app/home         | ✅ 전송     |
| /app/user/profile | ✅ 전송     |
| /login            | ❌ 미전송    |
| /about            | ❌ 미전송    |

> 쿠키가 유효한 경로(`/app`)에 포함되지 않은 요청에서는 쿠키가 아예 전송되지 않음

- 경로 미 설정 시에는 context root 설정
- context root로 지정하면 동일 도메인의 다른 애플리케이션까지 접근 가능
- 단, 보안 이유가 발생할 수 있음

```http
    http://localhost:8080
    //localhost 부분이 domain이다. 
```

4. maxAge : 쿠키의 유효 기간
- 양수 : **초 단위로 해당 시간**까지 쿠키 존재, 시간이 지나면 자동 폐기
- 음수 또는 미 지정 : 세션 쿠키로 **브라우저 종료** 등으로 세션 종료 시 폐기
- 0 : **브라우저에 도착**하는(응답) 즉시 폐기(`쿠키 삭제`는 따로 없고 유효기간 **0**으로 덮어쓴다.)

+ secure: HTTPS에서만 전송 허용, httpOnly: JavaScript에서 접근 불가 설정


---

### Cookie의 생성과 사용

```java
    //make-cookie
    Cookie cookie = new Cookie("user", "eunjin");
    cookie.setMaxAge(60*1);
    response.addCookie(cookie);

    //check-cookie-jsp
    Cookie[] cookies = request.getCookies();
    if(cookies != null) {
        for(Cookie cookie: cookies){
            out.println("쿠키 명 : "+ cookie.geName() + "<br>");
            out.println("값 : " + URLDecoder.decode(cookie.getValue(), "utf-8")+"<br>");
        }
    }
```
| 상황                     | 확인 가능 여부                       |
| ---------------------- | ------------------------------ |
| 기존 요청에 포함된 쿠키          | ✅ 확인 가능                        |
| 방금 생성한 쿠키(`addCookie`) | ❌ 확인 불가 (forward된 페이지에서도 마찬가지) |

- 핵심: `addCookie()`는 브라우저로 전송만 하고, 같은 요청(request)에서는 존재하지 않음
- 다음 요청에서 브라우저가 쿠키를 서버로 보내야 확인 가능


```sql
브라우저                          서버
   |                                |
   | ---- GET /make-cookie -------->|
   |                                |
   |   Set-Cookie: user=eunjin      |
   | <------------------------------|
   |                                |
   |   쿠키를 브라우저가 저장         |
   |                                |
   | ---- GET /check-cookie.jsp --->|
   |   Cookie: user=eunjin          |
   |                                |
   | <------------------------------|
   |   JSP에서 쿠키 확인 가능        |
```
#### 🔑 핵심 포인트
1. 쿠키 발급 시점: `response.addCookie()` → 브라우저에게만 전달
2. 같은 요청 내 확인 불가: 현재 request에는 포함되지 않음
3. 다음 요청에서 확인 가능: 브라우저가 쿠키를 서버로 전송할 때
> 단, path를 지정했으면 **해당 path 또는 path 하위 경로**의 요청에서만 확인 가능함. (미지정 시에는 모든 page에서 확인 가능)


---

## 🌐 Session & Exception Handling 요약

---

## 🟢 Session

- 서버에 클라이언트 상태 저장
- 하나의 브라우저 당 하나의 세션 생성
- 쿠키 vs 세션
  | 구분 | 쿠키 | 세션 |
  |------|------|------|
  | 브라우저 종료 시 | 유지 가능 | 종료 |
  | 서버 저장 위치 | 클라이언트 | 서버 |
  | 키 | 없음 | JSESSIONID 쿠키 필요 |

### 🔄 Session 동작 과정
1. 브라우저 → 최초 요청 → 서버
2. 서버: 세션 생성 및 저장 (key: JSESSIONID)
3. 서버 → 브라우저: JSESSIONID 쿠키 전달
4. 브라우저 → 재요청: JSESSIONID 전달
5. 서버: 세션 확인 후 활용

### ⚙️ HttpSession 주요 메서드
| 메서드 | 설명 |
|--------|------|
| `getAttribute(String name)` | 세션 속성 가져오기 |
| `setAttribute(String name, Object value)` | 세션 속성 설정 |
| `invalidate()` | 세션 무효화 (logout 처리) |
| `getId()` | 세션 ID 확인 |
| `getCreationTime()` | 세션 생성 시간 |
| `getLastAccessedTime()` | 마지막 접근 시간 |
| `getMaxInactiveInterval()` | 최대 유효기간(초) |
| `setMaxInactiveInterval(int sec)` | 최대 유효기간 설정(초) |

> 세션은 `getLastAccessedTime()` 기준으로 최대 유효기간(`getMaxInactiveInterval()`)까지 유지됨

### ⏱️ HttpSession 유효기간 설정
- web.xml (분 단위)
```xml
<session-config>
    <session-timeout>30</session-timeout>
</session-config>

```
> 설정 시간은 **분 단위**

- 애플리케이션 별 web.xml에서 context 단위 재정의 가능
- HttpSession#setMaxInactiveInterval(int sec)을 통해 프로그래밍적으로 재정의 가능(**초 단위** 설정)

### login & logout 실습
> pratice에 `logout_logoin_crud_practice` 프로젝트 참고

---

## 🛡️ SessionFilter 예시
- 세션이 있는 상황에서만 접근해야 하는 페이지
- /${root}/auth 하위 경로

```java
@WebFilter({"/auth"})
public class SessionFilter extends HttpFilter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpSerlvetRespoonse) response;

        HttpSession session = req.getSession();
        if(session.getAttribute("loginUser")!=null){
            chain.doFilter(request, response);
        } else {
            session.setAttribute("alertMsg", "로그인 후 사용하세요.");
            res.sendRedirect(req.getContextPath() +"/member?action=login-form");
        }
    }
}
```

---

## ⚠️ Exception 처리

### 기본 Exception 처리 정책
- 어떠한 경우도 예외에 대한 정보가 클라이언트에게 직접 전달되지 않도록 처리 필요
- 사용자에게 보여지는 페이지와 문제 해결을 위한 로깅이 병행 되어야 함

### 404 오류 처리 과정
| 경우                                      | 처리                                       |
| --------------------------------------- | ---------------------------------------- |
| Front Controller 진입 후 sub controller 없음 | response.sendError(404) → WAS 기본 404 페이지 |
| Front Controller 진입 못함                  | WAS 기본 404 페이지 처리                        |

```java
    //case 1
    String action = preProcessing(request, response);
    swtich(action){
        case "index" -> redirect(request, resposne, "/");
        // default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); //WAS 404 전달
        default -> forward(request, response, "/error/404.jsp"); 
    }

```

### 500 오류 처리 과정

| 경우                                 | 처리                                                                                                 |
| ---------------------------------- | -------------------------------------------------------------------------------------------------- |
| Case 1: Front Controller try~catch | Checked Exception 처리 후 **직접 페이지 전달** (Fallback 필요)                                                     |
| Case 2: WAS로 예외 전파                 | Unchecked Exception → 항상 전달<br>Checked Exception → ServletException wrapping 후 전달 → WAS 기본 500 페이지 |

```java
    //case 2
    try{
        ...
    } catch(Exception e){
        throw new SerlvetException(e);
    }
```

### WAS의 예외 처리 활용
- web.xml에 WAS가 받은 오류를 처리할 에러 페이지 설정
```xml
<error-page>
    <error-code>404</error-code>
    <location>/error/404.jsp</location>
<error-page>
```

### 🔗 Servlet Filter와 Error 처리
> error page 호출 전 dispathcerTypes = {DsipatcherType.ERROR}가 설정된 Filter 호출
#### 1️⃣ 기본 개념: Filter + dispatcherTypes
- **Filter**: Servlet 요청/응답을 가로채서 처리할 수 있는 컴포넌트
> 예: 인증, 로깅, 인코딩 처리 등
- **dispatcherTypes**: Filter가 **언제 호출될지** 지정
    - 기본: REQUEST (일반 HTTP 요청일 때)
    - 기타: ERROR, FORWARD, INCLUDE, ASYNC
즉, Filter는 단순히 모든 요청에서 동작할 수도 있고, 특정 상황(예: 에러 발생 시)에만 동작하도록 설정할 수 있음

#### 2️⃣ dispatcherTypes = {DispatcherType.ERROR} 의미
- 요청 처리 중 예외 발생 → error page 호출 과정에서 호출되는 필터를 의미
- 순서:
1. Servlet/Controller에서 예외 발생
2. WAS가 <error-page> 매핑된 JSP/Servlet으로 forward 시도
3. 이때, Filter 중 dispatcherTypes에 ERROR가 포함된 필터가 호출됨
즉, 에러 페이지 전용 필터를 정의할 수 있음
> ⚠️ 주의: 일반 요청(REQUEST)과 에러 처리(ERROR)는 서로 다른 Filter 체인을 타게 됨

#### 3️⃣ Filter 안에 들어가는 내용
에러 전용 Filter에서는 보통 다음과 같은 작업을 함
```java
@WebFilter(
    urlPatterns = "/*",
    dispatcherTypes = {DispatcherType.ERROR}
)
public class ErrorLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // 1. 에러 정보 가져오기
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        // 2. 로깅
        System.out.println("Error occurred in servlet: " + servletName);
        System.out.println("Request URI: " + requestUri);
        System.out.println("Status Code: " + statusCode);
        if (throwable != null) {
            throwable.printStackTrace();
        }

        // 3. 필요하면 추가 처리 (ex: 알림, 에러 통계)
        // ...

        // 4. 다음 Filter/Servlet 호출
        chain.doFilter(request, response);
    }
}

```
