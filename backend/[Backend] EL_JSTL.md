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


### 📚 JSTL
    - 자주 사용되는 기능에 대해 정형화된 태그 제공
    - 별도의 라이브러리 추가 설치 필요

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


