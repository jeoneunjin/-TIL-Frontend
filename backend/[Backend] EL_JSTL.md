# 🗓️ EL & JSTL 학습 내용 정리

## 🎯 학습 목표


## 💡 주요 키워드
-

---

## EL(Expression Language) & JSTL(JavaServer Pages Standard Tag Library)
> ***EL***: ${}로 JSP에서 데이터 표현 간편화
> ***JSTL***: 반복·조건·포맷 등 공통 기능을 태그로 제공하는 표준 라이브러리
> JSP에서 최대한 프로그래밍 요소를 제거(태그 중심으로 변경)


### EL
    - 기본 표현 법 : [${attribute_name}]
    - JSP의 expression(<%=...%>) 대체
    - 단순한 출력은 물론 웹 스코프에 저장된 attribute를 사용하는데 매우 편리
    > 작은 범위에서 큰 범위로 확장하며 attribute 검색하여 처음 발견된 값을 활용
    (page -> request -> session -> application)
    > 동일한 이름의 attribute가 있는데 큰 범위의 attribute를 사용하고 싶다면, 명시적으로 scope를 지정해야 함 [${sessionScope.sum}]
    - 값이 없을 경우 null이 아닌 공백으로 표시 

#### 주요 EL 내장 객체
이름 | 객체 타입 | 표현 데이터 | 사용 예
pageContext | PageContext | 현재 페이지의 컨텍스트 | ${pageContext}
requestScope | Map | 현재 요청 스코프의 속성 | ${requestScope.attributeName}
sessionScope | Map | 현재 세션 스코프의 속성 | ${sessionScope.attributeName}
param | Map | 요청 파라미터의 단일 값 | ${param.parameterName}
cookie | Map | 현재 요청의 쿠키 정보 | ${cookie.cookieName}

#### param 예시

* 요청 URL
```bash
GET /test.jsp?hobby=reading&name=eunjin
```

* EL에서 접근 방법:
```jsp
${param.hobby} → "reading"
${param.name}  → "eunjin"
```


* 요청 URL
```bash
GET /test.jsp?hobby=reading&hobby=swimming
```

* EL에서 접근 방법:
```jsp
${param.hobby}       → "reading"   (첫 번째 값만 가져옴)
${paramValues.hobby} → ["reading","swimming"]  (모든 값 배열)
```

#### 객체 접근 법
- JavaBeans의 property에 사용할 때 set/get을 제외하고 첫 글자를 소문자로 접근
    > property에 직접 접근하는 것이 아님; setter/getter가 동작
- Record는 그냥 property 이름 사용
- Map 계열은 Key로 접근

표기법 | 설명 | 활용 예
. : [${obj.property}] | 객체지향적이며 간단. java-naming룰에 어긋나면 사용 불가 | [${paramValues.names[0]}] (O), [${header.User-Agent}] (X) -> header.User에 Agent를 빼는 연산을 수행하려고 함(원래 의도와 달라짐)
[ ] : [${obj[property]}] | property의 형태에 상관 없이 사용 가능 | [${paramValue["names"][0]}] (O), [${header["User-Agent"]}] 

- 객체의 property뿐만 아니라 일반 메서드도 사용 가능
```jsp
    List<String> friends=List.of("eun", "jin");
    request.setAttribute("users", friends);

    <li>friends 수: ${friends.size()}, eun 포함 여부? ${friends.contains(eunjin)}</li>
```

---

#### EL 연산자
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

### JSTL
    - 자주 사용되는 기능에 대해 정형화된 태그 제공
    - 별도의 라이브러리 추가 설치 필요

### 사용 예시
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
// 뒤에 <% }%>로 안 닫아주면 컴파일 에러가 발생한다.(500 error)
```


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


