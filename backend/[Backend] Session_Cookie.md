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

```js
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

## Session