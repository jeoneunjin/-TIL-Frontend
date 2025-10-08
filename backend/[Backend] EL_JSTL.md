# 🗓️ EL & JSTL 학습 내용 정리

## 🎯 학습 목표
- JSP에서 **EL(Expression Language)**과 **JSTL(JavaServer Pages Standard Tag Library)**를 활용하여 코드를 간결하고 직관적으로 작성  
- HTML과 JSP의 로직 분리, 프로그래밍 요소를 최소화  

---

## 💡 주요 키워드
- EL, JSTL, param, requestScope, sessionScope, pageContext, cookie  
- 반복, 조건, 포맷, null 처리, 자동 캐스팅  

---

## 🔹 EL(Expression Language)
> JSP에서 `${}`로 데이터를 간편하게 표현  
> 복잡한 `<%= ... %>` 대신 간단한 출력, 스코프 탐색 가능  

### ✅ 특징
- 범위: **page → request → session → application**
- 동일 이름의 attribute가 여러 스코프에 존재하면 **작은 범위 우선**
- 값이 없으면 **null 대신 공백 출력**

### 🧩 주요 EL 내장 객체

| 이름 | 객체 타입 | 설명 | EL 사용 예 |
|------|-----------|------|------------|
| pageContext | PageContext | 현재 페이지 컨텍스트 | `${pageContext}` |
| requestScope | Map | 요청 스코프 속성 | `${requestScope.attr}` |
| sessionScope | Map | 세션 스코프 속성 | `${sessionScope.user}` |
| param | Map | 요청 파라미터 단일 값 | `${param.name}` |
| paramValues | Map | 요청 파라미터 배열 값 | `${paramValues.hobby}` |
| cookie | Map | 요청 쿠키 정보 | `${cookie.sessionId.value}` |

---

### 📝 param 예시
**URL 요청(단일 파라미터)**
```bash 
GET /test.jsp?hobby=reading&name=eunjin
```

**EL에서 접근 방법:**
```jsp
${param.hobby} → "reading"
${param.name}  → "eunjin"
```


**요청 URL(다중 값 파라미터)**
```bash
GET /test.jsp?hobby=reading&hobby=swimming
```

**EL에서 접근 방법:**
```jsp
${param.hobby}       → "reading"   (첫 번째 값만 가져옴)
${paramValues.hobby} → ["reading","swimming"]  (모든 값 배열)
```

#### 🧩 객체 접근 법
- **JavaBeans**: property 접근 시 set/get 제외하고 첫 글자를 소문자로 접근  
  > setter/getter가 동작, 직접 property 접근 아님
- **Record**: property 이름 그대로 사용
- **Map 계열**: Key로 접근

| 표기법 | 설명 | 활용 예 |
|--------|------|----------|
| `.` | 객체지향적, 간단. java-naming 룰 위반 시 사용 불가 | `${paramValues.names[0]}` ✅, `${header.User-Agent}` ❌ |
| `[]` | property 형태 제한 없음, 특수문자 포함 Key 가능 | `${paramValues["names"][0]}`, `${header["User-Agent"]}` ✅ |

- 객체 property뿐 아니라 일반 메서드도 사용 가능
```jsp
List<String> friends = List.of("eun", "jin");
request.setAttribute("users", friends);

<li>friends 수: ${friends.size()}, eun 포함 여부? ${friends.contains('eun')}</li>

```

---


####  ⚡ EL 연산자
> 산술연산, 비교 연산, 논리 연산, 상향 연산(? :), ***empty 연산***(데이터의 존재 여부를 나타내는 단항 연산자)

- 자바와 다른 점
    - 문자열에 대한 +(결합)연산은 지원하지 않고, 자동 캐스팅 오류(ex. ${"1"+"2"}:3, ${"1"+"b"} : "b"는 숫자로 변환이 불가하기 때문에 오류)
    - 나누기(/, div) : 정수 간의 연산에도 소수점의 결과를 리턴
    - 비교 연산자 : 수치 비교 뿐 아니라 문자 데이터에 대한 사전식 비교 처리
- ***empty 값이 true로 평가받는 경우***
    - null
    - 빈 문자열
    - 길이가 0인 배열
    - 빈 Collection 객체

---

## 🌟 JSTL (Jakarta Standard Tag Library)

> 💡 JSP에서 자바 코드(`scriptlet`) 없이도 **제어문, 반복문, 변수 처리 등을 HTML 태그처럼** 작성할 수 있게 해주는 표준 태그 라이브러리.

### 🧩 JSTL 사용 선언

> 📘 `taglib` directive를 활용해 JSTL을 JSP 페이지에 등록해야 함.

```jsp
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
```

- **prefix** : 페이지 내에서 태그를 사용할 때 붙이는 접두사  
- **uri** : 태그 라이브러리를 식별하는 고유 문자열로 라이브러리 로드에 사용  

| 🧱 라이브러리 | ⚙️ 주요 기능 | 🏷️ 일반적인 접두어 | 🔗 URI |
|:-------------:|:-------------:|:----------------:|:-------------------------:|
| core | 변수 지원, 제어문, URL 처리 | `c` | `jakarta.tags.core` |


### 💡 **Core 태그 정리**

#### 🧩 변수 지원 (`<c:set>`, `<c:remove>`)

```jsp
    //<c:set var="name" value="value" scope="scope"/>
    <c:set var="name" value="eunjin" scope="request"/>

    //<c:remove var="name" scope="scope"/>
    <c:remove var="name" scope="request"/>
```

| 속성 | 설명 |
|:----:|:----|
| `var` | 값을 저장할 EL 변수 이름 (String) |
| `value` | 변수의 값 자체 |
| `scope` | 변수를 저장할 영역 <br> → `page`(기본값), `request`, `session`, `application` |


#### 🔍 조건문(`<c:if>`)
```jsp
    <c:if test="조건" [var="test_result"]>
        ...
    </c:if>
```

- `test` 속성에는 **true/false 값**이나 이를 리턴하는 **EL 표현식** 사용 가능  
> 그 외의 값이 오면 **false**로 인식
- `var="test_result"`의 경우 test의 결과값을 test_result 이름으로 EL 변수에 저장해서 재사용 가능


#### 조건문(`c:choose>`, `<c:when>`, `<c:otherwise>`)
```jsp
    <c:choose>
        <c:when test="...">...</c:when>
        <c:when test="...">...</c:when>
        <c:otherwise>...</c.otherwise>
    </c:choose>
```
- `if-else` 구조와 유사한 **조건 분기 처리용 블록**  
- 여러 조건을 한 번에 처리할 때 사용  


#### 반복문(<c:forEach>)
```jsp
    <c:forEach var="name" items="collection" varStatus="status">...</c:forEach>
    
    <%
        Map(String, String)map = Map.of("name", "eunjin", "age", "25");
        request.setAttribute("person", map);
    
    %>

    <c:forEach var="data" items="${person}" varStatus="status">
        ${status.index}번째 요소 : ${data.key}, ${data.value}<br>
    </c:forEach>
```

---


### 📝 사용 예시
#### **Before(EL&JSTL 사용 전)**
```jsp
<%-- EL&JSTL 사용 전 --%>
<% 
    request.setAttribute("attrName", Integer.parseInt(request.getParameter("value")));
%>

<%
    Object Obj = request.getAttribute("attrName");
    if(obj!=null){
        int val = (Integer)obj;
    
%>
<div>
    <% 
        out.append("""
            <p>
                저장된 값 : %d
            </p>""".formatted(val));
    %>
</div>
<%
} //if
%>
```

💡 주의: <% } %> 생략 시 컴파일 에러(500) 발생


#### **After(EL&JSTL 사용 후)**
```jsp
<%-- 사용 후--%>
<c: set var="val" value="${param.value}/>
<c:if test="${!empth val}>
    <div>
        <p>저장된 값 : ${val}</p>
    </div>
<c:if>

// param.value 은 **HTTP 요청 파라미터 value**를 가져오는 표현
// param; 내장 객체로 Map 형태
```

---

## 🌐 페이지 모듈화 (Page Modularization)

> 💡 대부분의 웹사이트는 `header`, `footer`, `sidebar` 등 **반복적인 구조**를 가지고 있음  
> → 동일한 내용을 여러 페이지에서 사용하기 때문에 **페이지 모듈화**가 필요함

---

### 🧩 include directive (`<%@ include ... %>`)

> ✨ 현재 페이지에 다른 JSP 파일의 내용을 **컴파일 전에 삽입**하여 재활용하는 방식  
> 즉, **정적 포함(Static Include)** 이라고 함.

#### ✅ 특징

| 구분 | 설명 |
|------|------|
| ⏰ 포함 시점 | JSP가 **서블릿(Java 코드)** 으로 변환되기 **전에** 포함됨 |
| 🧱 결과 | 하나의 큰 JSP 파일로 합쳐져 **하나의 Servlet**으로 컴파일됨 |
| 🤝 변수 공유 | include된 파일은 같은 서블릿 내부에 존재하므로, **변수 및 선언부 공유 가능** |
| ⚠️ 주의점 | “같은 서블릿 내부”에서만 공유 가능 (다른 JSP에서는 각각 독립적으로 컴파일됨) |

---

#### 💻 예시 코드

```jsp
<%@ include file="/header.jsp" %>
```
>JSP가 서블릿(Java 코드)으로 변환될 때,
[header.jsp]의 내용이 그대로 현재 JSP 소스 안에 복사되어 붙여넣기 됨.
결과적으로 별도의 서블릿이 아닌 하나의 서블릿 클래스로 컴파일됨.

#### 🧠 코드 예시
```jsp
    <!-- main.jsp -->
    <%@ include file="header.jsp" %>
    <%! int count = 0; %>
    <%
        count++;
        out.println("main.jsp count: " + count);
    %>
```
```jsp
    <!-- header.jsp -->
    <%
        out.println("header.jsp에서 count 접근: " + count);
    %>
```
✅ 위 예시는 정상적으로 동작,
> 이유: include directive가 두 파일을 하나의 JSP 소스로 합쳐서 컴파일하기 때문
> 따라서 count 변수는 같은 서블릿 내부의 멤버 변수로 공유됨