# 🗓️ MVC 디자인 패턴 학습 내용 정리

## 🎯 학습 목표
서블릿과 JSP의 한계점을 이해하고, 이를 해결하기 위한 **MVC(Model-View-Controller)** 패턴의 필요성과 구조를 학습한다.

---

## 💡 주요 키워드
- Model  
- View  
- Controller  
- Forward  
- Redirect  
- PRG (Post-Redirect-Get)  

---

## 🧩 MVC 디자인 패턴
> 애플리케이션을 역할에 따라 **Model**, **View**, **Controller**로 모듈화하는 패턴

| 모듈 | 설명 | 주요 업무 |
|------|------|-----------|
| 🧭 **Controller** | 사용자의 요청을 받고 Model과 View 간의 상호 작용을 조정하는 모듈 | HttpServletRequest 분석, 파라미터를 이용해 적절한 Controller 호출, Model의 반환 값 확인, 결과를 Attribute로 저장, 적절한 View 호출 |
| ⚙️ **Model** | 비즈니스 로직(Service) 담당 + 필요 시 DB 연동(DAO) | 비즈니스 로직 수행 및 데이터 처리 |
| 🪟 **View** | 사용자와의 상호작용(UI) 담당 | 웹 스코프에 저장된 Attribute 활용, HTML 형식으로 응답 생성 |

---

### 🧱 Service & DAO
| 이름 | 주요 업무 |
|------|------------|
| **Service** | 비즈니스 로직 처리, 필요 시 DAO 호출로 DB 연동 |
| **DAO** | JDBC를 이용해 DB 연동 |

---

## 🔄 MVC 요청 → 응답 흐름

```text
Request (URL 기반 요청)
   ↓
Controller
   ↓ (요청 전달)
Model ⇄ DB
   ↓ (결과 전달)
Controller
   ↓ (뷰 선택)
View
   ↓
Response (HTML 기반 응답)
```

### 🌟 MVC 장점
1. 분리된 관심사로 독립적 개발 및 유지보수가 용이
2. 코드의 가독성, 재사용성 향상

---

### 모델 비교
| Model                                                | 장점                                      | 단점                                  |
| ---------------------------------------------------- | --------------------------------------- | ----------------------------------- |
| Model1 (JSP(controller+view) + model(DB))            | ✅ 구조 간단, 소규모 앱에 적합<br>✅ 직관적, JSP 하나로 구성 | ❌ 유지보수 어려움<br>❌ 확장성 제한              |
| Model2 (JSP(view) + Servlet(controller) + model(DB)) | ✅ 명확한 구조, 역할 분리<br>✅ 유지보수 용이, 확장성 높음    | ❌ 초기 설정 복잡, 개발 속도 느림<br>❌ MVC 이해 필요 |

> ***실무에서는 Model2 선택***

---


## 🔁 웹 컴포넌트 호출 & PRG 패턴 정리


### 🧭 웹 컴포넌트 호출(forward)
> 서블릿/JSP는 메서드 호출처럼 직접 호출하지 않고 **컨테이너(Container)**가 호출  
> 내부 페이지로 이동할 때 사용 (`RequestDispatcher#forward`)

### 특징
1. RequestDispatcher#forward
> controller가 받았던 request resposne를 JSP에 전달(controller가 새로운 내부 타겟 결정하여 결과를 전달), request에 저장한 정보는 JSP에서 확인 가능

### 클라이언트 요청 & 서버 처리 컴포넌트

**❓ 클라이언트 요청 횟수:**  
➡️ **1회**  

**❓ 서버에서 동작한 웹 컴포넌트:**  
➡️ **2개** (mainController, result.jsp)

#### 📊 전체 흐름 요약
```scss
[Request 1]
↓
Web Browser → Controller → (determine internal target)
↓
Controller → forward (internal resource, same request)
↓
View (JSP) renders response
↓
Response (HTML) → Client
```


```jsp
RequestDispatcher dispatcher = request.getRequestDispatcher("/reulst.jsp");
dispatcher.forward(request, response);
```


### 웹 컴포넌트 호출(redirect)
> 현재 실행중인 페이지의 실행을 중단하고 다른 웹 자원이 대신 호출되도록 함
> 서버 내부 리소스 뿐만 아니라 외부의 리소스까지 사용 가능

### 클라이언트 요청 & 서버 응답 (Redirect)

**❓ 클라이언트 요청 횟수:**  
➡️ **2회**  

**❓ 서버 응답 횟수:**  
➡️ **2회**


#### 📊 전체 흐름 요약
```scss
[Request 1]
↓
Web Browser → Controller → (determine redirect target)
↓
Controller → Response (Redirect, 302 + Location)
↓
Web Browser → [Request 2 to Location URL]
↓
Controller → View → Response (HTML)
↓
Client (Final page shown)
```
### 🔹 첫 번째 요청과 응답

> Redirect 시 주의 사항:  
> 첫 번째 요청에 대한 응답은 클라이언트가 최종적으로 받은 페이지에서 확인할 수 없습니다.  
> 즉, 요청 1에서 받은 정보는 요청 2로 전달되지 않으며, 최종 응답(요청 2)은 요청 1과 무관합니다.


---


### 🔁 Forward vs Redirect 비교

| 구분 | Forward | Redirect |
|------|---------|---------|
| 요청 횟수 | 1회 | 2회 (요청 1 + 요청 2) |
| 처리 위치 | 서버 내부 이동 | 클라이언트가 새로운 요청 |
| URL 변화 | 변하지 않음 | 변함 |
| request 데이터 | 유지됨 | 소멸됨 |
| 속도 | 빠름 (한 번만 요청) | 느림 (두 번 요청) |
| 사용 목적 | JSP 결과 전달, 내부 페이지 이동 | 외부 페이지나 새 URL 이동 |

> 🔹 Forward: 서버에서 직접 호출  
> 🔹 Redirect: 브라우저를 통한 간접 호출

---

### 🧠 PRG(Post-Redirect-Get) 패턴

- POST 요청 처리 후 **Redirect** → GET 요청으로 전환  
- 목적: 새로고침 시 **중복 POST 제출 방지**
- [새로 고침] 행위는 이전에 했던 요청을 다시 진행 
> 멱등한 GET, PUT, DELETE는 문제 없지만 Post 요청은 멱등하지 않으므로, 어려 버 수행 되면 중복 제출 문제가 발생함 

#### 흐름
```text
POST → (서버 처리)
     → Redirect (302)
     → GET (새 URL)
```

---


### 🌐 301 vs 302 리디렉션

> 기본(default) 리디렉션 코드는 **302(Found)**

| 상태 코드 | 의미 | 특징 | 사용 예시 |
|-----------|------|------|-----------|
| **301** | 영구 이동 | 브라우저/검색엔진 URL 캐시, PageRank 새 URL로 이전 | 도메인 변경, URL 구조 개편 |
| **302** | 임시 이동 | 기존 URL 유지, 클라이언트 이후 요청 시 원래 URL 사용 가능 | 로그인 후 이동, 임시 공지, A/B 테스트 |

**설명**
- 301: 요청 URL이 영구적으로 새로운 URL로 변경 → 검색엔진이 새 URL을 대표 페이지로 인식, PageRank 이전  
- 302: 요청 URL은 여전히 대표 페이지 → 임시 이동, 기존 PageRank 유지

> 🔑 결론:  
> 요청 1이 발생한 URL이 대표 페이지로 유지되려면 **302**, 새 URL로 대표 페이지를 변경하려면 **301** 사용

