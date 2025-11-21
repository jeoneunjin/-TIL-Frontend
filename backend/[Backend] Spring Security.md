# 🌱 Spring Security 학습 정리

## 📚 목차
1. [Authentication(인증) vs Authorization(인가)](#1-authentication인증-vs-authorization인가)
2. [인증과 인가 절차](#2-인증과-인가-절차)
3. [AuthenticationManager](#3-authenticationmanager)
4. [CSRF와 CSRF Token](#4-csrf와-csrf-token)
5. [Session vs JWT 기반 인증](#5-session-vs-jwt-기반-인증)
6. [Spring Security Filter 흐름](#6-spring-security-filter-흐름)

---

## 1. Authentication(인증) vs Authorization(인가)

### 🔐 Authentication (인증)
**"너 누구야?"**  
사용자의 *신원을 확인*하는 절차.

#### ✔ 특징
- 아이디/비밀번호, OTP, 지문/얼굴 인식 등으로 본인 여부 확인
- 결과: “이 사용자는 A이다”

#### ✔ 목적
- 사용자가 진짜 본인인지 확인

---

### 🔑 Authorization (인가)
**"너 이 행동 해도 돼?"**  
인증된 사용자에게 *권한을 부여하거나 제한*하는 절차.

#### ✔ 특징
- 관리자만 가능 / 특정 API 접근 가능 등
- 인증이 먼저 → 인가가 그다음

#### ✔ 목적
- 사용자가 *무엇을 할 수 있는지* 결정

---

## 2. 인증과 인가 절차

### 2-1. 🔐 인증(Authentication) 절차
1) 사용자가 로그인 정보 입력  
2) 서버가 정보 검증 (DB 또는 OAuth Provider)  
3) 인증 성공 시 세션/JWT 발급  
4) 이후 요청마다 세션 또는 JWT로 신원 인증

---

### 2-2. 🔑 인가(Authorization) 절차
1) 서버가 사용자 정보 확인 (세션 or JWT)  
2) 권한(Role) 조회 (USER, ADMIN 등)  
3) 요청 리소스와 권한 비교  
4) 접근 허용 또는 403 Forbidden 반환

---

### 2-3. 🔥 전체 흐름 요약
사용자 → **인증(너 누구?)** → 신원 확인 → **인가(해도 돼?)** → 권한 확인 → 접근 허용

---

## 3. AuthenticationManager

Spring Security에서 **인증을 총괄하는 핵심 객체**.

### ✔ ProviderManager (구현체)
- 여러 인증 방식을 제공하는 `AuthenticationProvider`들을 관리

### ✔ AuthenticationProvider
- 실제 인증 방식을 구현하는 객체
- Dao, JWT, OAuth 등 다양한 방식 존재

### ✔ DaoAuthenticationProvider
- `UserDetailsService` + `PasswordEncoder` 기반 인증
- 개발자가 직접 **CustomUserDetails** 로 확장 가능

---

## 4. CSRF와 CSRF Token

### 4-1. ⚠ CSRF 공격이란?
Cross-Site Request Forgery  
**사용자가 원하지 않은 요청을 강제로 보내는 공격**

예:
- 로그인된 브라우저에서 공격자가 만든 URL 클릭  
- 서버는 사용자 요청으로 착각 → 위험한 요청 수행

---

### 4-2. 🔒 CSRF Token의 역할
서버가 사용자를 구분하기 위해 발급하는 **1회성 토큰**으로,  
요청마다 함께 제출해야 서버가 “정상 요청”임을 판단.

### ✔ 동작 방식
1) 서버가 CSRF Token 발급  
2) 클라이언트는 요청 시 해당 토큰을 헤더 혹은 hidden input으로 전달  
3) 서버는 토큰 비교 후 일치하면 정상 요청으로 인정

---

### 4-3. 스프링에서 CSRF를 비활성화하는 이유
👉 **JWT 기반 API 서버에서는 대부분 disable 한다.**

이유:
- CSRF는 “쿠키 기반 인증”에서 발생  
- JWT는 쿠키 대신 Authorization 헤더 사용 → CSRF 거의 무의미
- 그래서 보통  
  ```java
  http.csrf().disable();
  ```

---