# 🗓️ Session & Cookie 학습 내용 정리

## 🎯 학습 목표

---

## 💡 주요 키워드

---

## Session, Cookie의 필요성

### HTTP 특징
> Stateless; 상태를 기억하지 않음

### Stateless한 이유?
- 단순성 : 각 요청이 독립적으로 처리 되기 때문에 이전 요청과 연결할 필요가 없고, 서버의 구성이 단순해지며 속도가 빠름
- 확장성 : 여러 서버 간 공유해야 할 상태가 없기 때문에 여러 서버에 요청을 분산시킬 수 있어 부하 분산 및 시스템 확장이 편리
- 신뢰성 : 독립적이기 떄문에 한 요청의 실패가 다른 요청에 영향을 미치지 않으므로 신뢰도 향상
- 지원절약 : 서버에서 상태를 저장하지 않기 때무에 그만큼 서버의 메모리, 저장 공간 절약

> Cookie, Session 등으로 stateless 특성 보안

## Cookie
> 웹 서버에서 정보를 생성해서 클라이언트(웹 브라우저)에 보관하는 데이터

### 과정
1. 브라우저 - 최초 요청 -> 서버
2. (서버) 쿠키 생성
3. 서버 - response시 쿠키 전송 -> 브라우저
4. (브라우저) 쿠키 저장
5. 브라우저 - request시 cookie 전송 -> 서버

### Cookie의 주요 property
- name을 제외한 property는 getter/setter로 접근 가능

1. name : 쿠키를 만들 때 절달하는 쿠키의 이름(쿠키를 구별하는 유일한 값)
> 동일한 이름의 쿠키는 기존 쿠키를 덮어씀
> 알파벳, 숫자, 하이픈(-), 언더스코어(_), 틸트(~), 점으로 구성되며 공백 등 나머지 URLEncoder를 통해 인코딩 필요

2. value : 쿠키의 값으로 쿠키 생성시 전달하거나 setValue() 메서드를 통해 설정 가능
> 작성 규칙은 name과 동일, 한글 사용은 가능
```js
    Cookie cookie = new Cookie("username", "eunjin");
    Cookie cookie = new Cookie("status-path", URLEncoder.encode("status 경로에 저장된 쿠키", "UTF-8"));
```

3. path : 쿠키가 유효한 경로
> path가 설정된 하위 경로에서만 쿠키 전송

### 언제 쿠키가 전송되지 않는가?
#### 📌 상황 설정

서버에서 쿠키 발급
```http
 Set-Cookie: sessionId=abc123; Path=/app
```
> 의미 : 이 쿠키는 /app 경로와 그 하위 경로에서만 브라우저가 서버로 전송함

#### 🚫 쿠키가 전송되지 않는 경우 예시
요청 URL | 쿠키 전송 여부 
/app/home | ✅ 전송됨
/app/user/profile | ✅ 전송됨
/login | ❌ 전송되지 않음
/about | ❌ 전송되지 않음
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
   |   쿠키를 브라우저가 저장       |
   |                                |
   | ---- GET /check-cookie.jsp --->|
   |   Cookie: user=eunjin          |
   |                                |
   | <------------------------------|
   |   JSP에서 쿠키 확인 가능       |
```
#### 🔑 핵심 포인트
1. 쿠키 발급 시점: `response.addCookie()` → 브라우저에게만 전달
2. 같은 요청 내 확인 불가: 현재 request에는 포함되지 않음
3. 다음 요청에서 확인 가능: 브라우저가 쿠키를 서버로 전송할 때
> 단, path를 지정했으면 해당 path 또는 path 하위 경로의 요청에서만 확인 가능함. (미지정 시에는 모든 page에서 확인 가능)