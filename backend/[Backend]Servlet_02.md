# ☕ Servlet → JSP → MVC 패턴

## 🎯 학습 목표
서블릿과 JSP의 한계점을 이해하고, 이를 해결하기 위한 **MVC(Model-View-Controller)** 패턴의 필요성과 구조를 학습한다.

## 주요 키워드
> Servlet, JSP, Scope, Scriptlet, directive, 


---

   
## 🔁 Servlet → JSP → MVC

| 구분 | 설명 |
|------|------|
| **Servlet** | 자바 코드로 HTML을 생성해야 해서 UI 개발이 복잡함 😩 |
| **JSP** | HTML 내에 자바 코드를 삽입하여 UI 개발이 편리해졌지만, 비즈니스 로직과 섞여 **스파게티 코드** 발생 🍝 |
| **MVC 패턴** | **프레젠테이션(View)** 과 **비즈니스 로직(Model)** 을 분리하여 **유지보수성**과 **개발 효율성** 향상 🚀 |

---


## ⚙️ Servlet vs JSP

| 구분 | Servlet | JSP |
|------|----------|------|
| **실행 과정** | `.class` 파일을 웹 컨테이너가 직접 실행 | 최초 요청 시 Servlet 코드로 변환 → 컴파일 → 실행 (이후 요청 시 컴파일된 Servlet 재사용) |
| **개발 생산성** | 자바 코드 내 HTML 포함 → **UI 개발 복잡** | HTML 기반 + 자바 코드 삽입 → **UI 개발 용이** |



## 🧩 JSP (Java Server Pages)

> 동적인 웹 페이지를 생성하기 위한 **서버 측 스크립팅 기술**  
> HTML 태그 기반에 Java 코드를 작성하는 형태



### 🧱 JSP 구성 요소

| 구성요소 | 설명 | 예시 |
|-----------|--------|------|
| **directive** | JSP 페이지의 속성 지정 | `<%@ ... %>` |
| **scriptlet** | 자바 실행문 (local 영역, `_jspService()` 내부에 포함) | `<% ... %>` |
| **expression** | 계산식·메서드 호출 결과 출력 (`out.print()`로 변환) | `<%= ... %>` |
| **declaration** | 변수 및 메서드 선언 (class 영역에 포함) | `<%! ... %>` |
| **comment** | JSP 전용 주석 (Servlet으로 전달 X) | `<%-- ... --%>` |
| **built-in object** | JSP 내장 객체 | `request`, `session`, `response`, ... |
| **expression language (EL)** | 간결한 값 표현식 | `${expr}` |
| **JSTL** | 자주 사용하는 태그 표준화 | `<c:forEach>`, `<c:if>` 등 |



### 🧭 Directive 지시자

> JSP 실행 시 필요한 정보를 **컨테이너에 전달**하여 Servlet 생성에 활용

#### ✳️ 기본 형태
```jsp
<%@ [directive] 속성="값" 속성="값" ... %>
```



### ✳️ 지시자 종류

| 지시자 | 설명 | 사용 예 |
|--------|------|----------|
| **page** | JSP 페이지의 기본 정보 지정 (문서 타입, 출력 버퍼 크기, 에러 페이지 설정 등) | 모든 JSP에 필수 |
| **taglib** | JSP 페이지에서 사용할 태그 라이브러리 지정 | JSTL 사용 시 필요 |
| **include** | JSP 페이지의 특정 영역에 다른 문서 포함 (ex. header, footer) | 페이지 모듈화 및 재사용 |



### ⚙️ Page Directive 주요 속성

| 속성 | 기본값 | 설명 | 사용 예 |
|------|---------|------|---------|
| `pageEncoding` | UTF-8 | JSP 페이지의 문자 인코딩 설정 | `<%@ page pageEncoding="UTF-8" %>` |
| `contentType` | text/html; charset=ISO8859-1 | 페이지의 MIME 타입 설정 | `<%@ page contentType="text/html; charset=UTF-8" %>` |
| `import` | (없음) | JSP에서 사용할 클래스의 패키지 지정 | `<%@ page import="java.util.*" %>` |

---

## 💻 Scriptlet (스크립트릿)

> JSP 내에 **자바 실행문**을 작성할 때 사용되는 영역  
> `_jspService()` 메서드 내부의 **local 영역**에 포함됨  
> → JSP 내장 객체(`request`, `response`, 등)를 자유롭게 사용 가능  

📄 **형태**
```jsp
<% 자바 실행문 %>

<%@ page import="java.time.localDateTime %>
<%@ page language="java" contentType="text/html"; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCYTPE html>
<html>...</html>
```

---

### 🧠 JSP 내장 객체
> JSP 파일이 서블릿으로 변환될 때 _jspService() 내부에 자동 선언된 로컬 변수
따라서 동일한 이름으로 다시 선언 불가능 ⚠️


#### 종류
변수명 | 용도
request | 클라이언트 요청 정보
response | 서버의 응답 정보
out | 클라이언트로의 정보 출력 스트림
session | HTTP 세션에 대한 정보 
application | 웹 애플리케이션에 대한 정보
pageContent | 현재 JSP 페이지에 대한 정보 
exception | isErrorPage = true인 경우 사용되는 예외 객체
config | JSP 페이지에 대한 설정 정보 저장 


### scope 
page < request < session < application

page scope | JSP 페이지 내에서 유효한 속성 저장
request scope | 요청 처리 동안 유효; 응답이 완료되면 소멸
seesion scope | 사용자가 웹 애플리케이션에 접속한 동안 유효; 세션이 종료/만료되면 소멸
application scope | 웹 애플리케이션 전체에 걸쳐 유효; 모든 사용자의 세션에서 공유


### 🧾 웹 영역과 사용 예시
```jsp
<%-- attribute 설정 --%>
<% 
    request.setAttribute("reqAttrName", "reqAttrValue");
    session.setAttribute("sesAttrName", "sesAttrValue");
%>

<h2>attribute 확인</h2>
<ul>
    <li>request : <%=request.getAttribute()%>
    <li>session : <%=session.getAttribute()%>
</ul>
```

#### 🛠️ 주요 메서드 요약

메서드 | 설명
setAttribute(String key, Objec value) | 속성을 지정한 키와 값으로 저장(기존 속성 덮어쓰기 가능)
getAttribute(String key) | 지정한 키에 해당하는 속성의 값을 반환(값이 존재하지 않으면 null 반환)
removeAttribute(String key) | 지정한 키에 해당하는 속성 제거 
getAttributeNames() | 모든 속성의 키를 Enumeration으로 반환

