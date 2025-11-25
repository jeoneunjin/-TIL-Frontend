# Vue + TypeScript 프로젝트 시작

## 📑 목차
- [Vue + TypeScript 프로젝트 시작](#vue--typescript-프로젝트-시작)
  - [📑 목차](#-목차)
  - [1. 프로젝트 시작](#1-프로젝트-시작)
    - [1-1. 기본 프로젝트 템플릿 구조](#1-1-기본-프로젝트-템플릿-구조)
    - [1-2. 각 폴더의 역할](#1-2-각-폴더의-역할)
      - [📁 `src/assets`](#-srcassets)
      - [📁 `src/components`](#-srccomponents)
      - [📁 `src/views`](#-srcviews)
      - [📁 `src/router`](#-srcrouter)
      - [📁 `src/store`](#-srcstore)
      - [📁 `src/services` ⭐ (API 호출/관리 폴더)](#-srcservices--api-호출관리-폴더)
      - [도메인 단위로 나누기](#도메인-단위로-나누기)
      - [📁 `src/types`](#-srctypes)
      - [📁 `src/utils`](#-srcutils)
  - [2. 프로젝트 설정](#2-프로젝트-설정)
    - [PrimeVue 사용을 고려한 구조 확장](#primevue-사용을-고려한-구조-확장)
    - [0. 프로젝트 기본 구조 생성(Vite 사용)](#0-프로젝트-기본-구조-생성vite-사용)
      - [0-1. 프로젝트 생성](#0-1-프로젝트-생성)
      - [✅ pnpm을 더 권장하는 이유](#-pnpm을-더-권장하는-이유)
      - [📌 npm을 쓸 상황](#-npm을-쓸-상황)
      - [🏁 결론](#-결론)
      - [0-2. 설정 질문 응담(프롬프트)](#0-2-설정-질문-응담프롬프트)
    - [0-3. 프로젝트 폴더 이동 및 기본 설치](#0-3-프로젝트-폴더-이동-및-기본-설치)
    - [1. PrimeVue 설치 및 초기 설정](#1-primevue-설치-및-초기-설정)
      - [1.1. PrimeVue 및 종속성 설치](#11-primevue-및-종속성-설치)
      - [추가로, 타입 지원 희망 시](#추가로-타입-지원-희망-시)
      - [1-2. `main.ts` 파일 설정(가장 중요 💡)](#1-2-maints-파일-설정가장-중요-)
      - [2. 컴포넌트 등록 방식](#2-컴포넌트-등록-방식)
    - [3. 개발 서버 실행 및 확인](#3-개발-서버-실행-및-확인)
  - [3. 추가\_프로젝트 구조](#3-추가_프로젝트-구조)
      - [2.1. 커스텀 컴포넌트 래핑 (Base Components)](#21-커스텀-컴포넌트-래핑-base-components)
---

## 1. 프로젝트 시작

### 1-1. 기본 프로젝트 템플릿 구조

```cssharp
my-vue-app/
├─ src/
│  ├─ assets/          # 이미지, 폰트, 정적 파일
│  ├─ components/      # 재사용 가능한 컴포넌트(Button, Card, Map 등)
│  ├─ views/           # 페이지 단위 화면(HomeView.vue, AboutView.vue …)
│  ├─ router/          # 라우터 설정 (메인 메뉴, 페이지 이동)
│  ├─ store/           # Pinia 또는 Vuex (전역 상태 관리)
│  ├─ services/        # API 호출 모듈 (백엔드 연동 시 사용)
│  ├─ types/           # TypeScript 타입 정의
│  ├─ utils/           # 공통 유틸 함수
│  ├─ App.vue          # 루트 컴포넌트
│  └─ main.ts          # Vue 앱 진입 파일
├─ public/             # 정적 파일(빌드 시 그대로 제공)
├─ index.html
└─ package.json
```
---

### 1-2. 각 폴더의 역할

#### 📁 `src/assets`
> 이미지, SVG, 아이콘, 폰트 등 정적 파일 저장하는 곳

#### 📁 `src/components`
> 재사용 가능한 UI 조각들

- Button.vue
- Header.vue
- MapCard.vue
- CountryItem.vue
- Modal.vue

#### 📁 `src/views`
> 페이지 단위 컴포넌트

```text
HomeView.vue
DetailView.vue
LoginView.vue
CountryInfoView.vue
```

#### 📁 `src/router`
> Vue Router 설정

#### 📁 `src/store`
> Pinia 또는 Vuex 사용 시 전역 상태를 저장하는 곳
> 요즘은 **Pinia** 추천

- 로그인 상태
- 사용자 정보
- 전역 테마
- (ex)지도 클릭한 국가 등

--- 

#### 📁 `src/services` ⭐ (API 호출/관리 폴더)
> 백엔드와 통신하는 모듈 모아두는 곳

#### 도메인 단위로 나누기
📌 **services/apiClient.ts**

전역 axios 클라이언트 설정
(베이스 URL, 헤더 기본값 등 넣는 파일)

---

📌 **services/userService.ts**
- 회원 관련 기능 모듈
- 로그인 요청
- 회원 정보 가져오기
- 로그아웃

--- 

📌 services/countryService.ts
- 지도/국가 정보 모듈
- 국가 리스트 불러오기
- 특정 국가 정보 불러오기
- 검색

---

📌 services/authService.ts
- 토큰 저장/삭제 등 간단한 인증 처리

--- 

#### 📁 `src/types`
> TypeScript 타입을 모아두는 곳

```pgsql
types/
  User.ts
  Country.ts
  ApiResponse.ts
```

- **예시 타입 : **

```ts
export interface Country {
  code: string;
  name: string;
  riskLevel: 'High' | 'Moderate' | 'Low';
}
```

#### 📁 `src/utils`
> 전역 공용 함수들

--- 

## 2. 프로젝트 설정
> PimeVue, Vue.js + TypeScript 프로젝트 기준

### PrimeVue 사용을 고려한 구조 확장

```cssharp
my-vue-app/
├─ src/
│  ├─ assets/
│  │  └─ styles/      # PrimeVue 테마 오버라이딩 (Custom CSS/SCSS)
│  ├─ components/
│  │  ├─ base/         # 🆕 PrimeVue 래핑 컴포넌트 (BaseButton.vue, BaseInput.vue) 규모 커지면 base/(원자 컴포넌트), feature/(기능별 컴포넌트)로 분할 고려
│  │  └─ common/       # 🆕 프로젝트 고유의 복합/재사용 컴포넌트 (LoginForm.vue)
│  ├─ views/
│  │  └─ layouts/      # 🆕 AppLayout.vue, AuthLayout.vue 등 전역 레이아웃
│  ├─ router/
│  ├─ store/
│  ├─ services/
│  ├─ types/
│  ├─ utils/
│  ├─ composables/     # 🆕 Composition API 재사용 로직 (useFetch, useAuth)
│  ├─ App.vue
│  └─ main.ts
```

---

### 0. 프로젝트 기본 구조 생성(Vite 사용)

#### 0-1. 프로젝트 생성

#### ✅ pnpm을 더 권장하는 이유
- **빠름**: 패키지를 중복 다운로드하지 않아 설치 속도가 매우 빠름.
- **용량 절약**: 공통 패키지를 한 번만 저장해서 디스크 사용량이 적음.
- **의존성 충돌 적음**: npm의 hoisting 문제를 구조적으로 방지.
- **모노레포에 강함**: workspace 기능이 더 안정적이고 빠름.

#### 📌 npm을 쓸 상황
- 초보자일 때
- npm 기준으로 학습 자료를 따라갈 때
- 기존 팀이 npm을 이미 사용 중일 때

#### 🏁 결론
- 새 프로젝트라면 **pnpm 추천**, 더 빠르고 효율적임.

```bash
npm install pnpm -g

pnpm create vue@latest
```

#### 0-2. 설정 질문 응담(프롬프트)

**Select features to include in your project:**

|항목 (사진 속 내용)|선택 여부 |이유|
|----------------|-----------|---------------------------|
|[\+] TypeScript|선택 (O)|타입 검사를 통한 안정적인 개발의 핵심|
|[ ] JSX Support|선택 안 함 (X)|Vue의 표준인 .vue 파일(SFC)을 사용하므로 필요 없음|
|[\+] Router (SPA development)|선택 (O)|페이지 이동 및 URL 관리를 위해 반드시 선택해아 함|
|[\+] Pinia (state management)|선택 (O)|전역 상태 관리를 위한 공식 권장 라이브러리|
|[ ] Vitest (unit testing)|선택 안 함 (X)|나중에 추가할 수 있음 일단은 No로 진행해도 무방|
|[ ] End-to-End Testing|선택 안 함 (X)|테스트 도구이므로 일단 No로 진행|
|[\+] ESLint (error prevention)|선택 (O)|코드 품질 검사 및 오류 사전 방지를 위해 필수|
|[\+] Prettier (code formatting)|선택 (O)|일관된 코드 스타일 자동 정렬을 위해 반드시 선택하는 것을 추천| 

**Select experimental features to include in your project:**

> 둘 다 선택 X 

---

### 0-3. 프로젝트 폴더 이동 및 기본 설치
```bash
# 생성된 프로젝트로 이동
cd [project]

# 프로젝트에 필요한 종속성 설치
pnpm install
```

---

### 1. PrimeVue 설치 및 초기 설정

#### 1.1. PrimeVue 및 종속성 설치
PrimeVue와 필수적인 아이콘 라이브러리(예: PrimeIcons)를 설치

```Bash
# PrimeVue + PrimeIcons 설치 (필수)
pnpm add primevue primeicons
```
#### 추가로, 타입 지원 희망 시
```Bash
pnpm add -D @types/node
```

--- 


#### 1-2. `main.ts` 파일 설정(가장 중요 💡)
PrimeVue를 애플리케이션 전역에서 사용할 수 있도록 src/main.ts 파일에 Vue 플러그인으로 등록하고 스타일을 가져와야 함

- `src/main.ts`파일에 PrimeVue 관련 코드 추가

```ts
// src/main.ts

import { createApp } from 'vue';
import App from './App.vue';
import PrimeVue from 'primevue/config'; // 추가
// Vue Router와 Pinia는 이미 Vite가 생성했을 거임

// -------------------- PrimeVue 필수 스타일 가져오기 --------------------
// 1. 테마 파일 선택 (aura-light-green 테마 예시)
import 'primevue/resources/themes/aura-light-green/theme.css'; 
// 2. PrimeVue 기본 CSS
import 'primevue/resources/primevue.min.css';
// 3. 아이콘 세트
import 'primeicons/primeicons.css';

const app = createApp(App);
// ... Pinia, Router 설정 있음

// --- PrimeVue 플러그인 등록 ---
app.use(PrimeVue, {
  ripple: true // 물결 효과 활성화 (선택 사항)
});

// app.use(router);
// app.use(pinia);

app.mount('#app');
```
> ⚠️ 테마 선택: 위 예시에서는 aura-light-green 테마를 사용했음
> PrimeVue 공식 웹사이트에서 원하는 테마 (예: lara-light-blue, arya-green 등)로 변경하여 사용

---

#### 2. 컴포넌트 등록 방식
PrimeVue는 수백 개의 컴포넌트를 가지고 있어, 애플리케이션의 크기를 줄이고 로딩 속도를 개선하기 위해 필요한 컴포넌트만 전역 등록하거나 각 컴포넌트(.vue 파일)에서 로컬 등록하는 것을 권장

**✅ 추천: 전역 등록 (main.ts에서 자주 사용되는 컴포넌트만)**

```ts
// src/main.ts 에 추가

import Button from 'primevue/button';
import InputText from 'primevue/inputtext';

// ... (다른 설정)

app.component('Button', Button);
app.component('InputText', InputText);

// ... (app.mount('#app'))
```

**✅ 로컬 등록 (페이지/컴포넌트 내에서 등록)**
특정 컴포넌트에서만 사용되는 경우, 해당 .vue 파일 내에서 import 하여 사용

```ts
<script setup lang="ts">
import { ref } from 'vue';
import Card from 'primevue/card'; // 로컬 등록
import Button from 'primevue/button'; // 로컬 등록
// 'InputText'는 main.ts에서 전역 등록했다고 가정
</script>

<template>
  <Card>
    <template #title>로그인</template>
    <template #content>
      <InputText placeholder="아이디" />
      <Button label="로그인" />
    </template>
  </Card>
</template>
```

### 3. 개발 서버 실행 및 확인
`npm run dev`로 개발 서버 실행

---

## 3. 추가_프로젝트 구조
PrimeVue를 사용해도 기본적인 프로젝트 구조는 변경되지 않지만, 스타일 관리와 커스텀 컴포넌트에 PrimeVue를 통합하는 방식이 중요

| 디렉토리/파일 | PrimeVue를 고려한 역할
|----------------|-------------------------|
|`src/assets/styles`| PrimeVue 테마 오버라이딩 및 전역 스타일 정의 파일 (예: variables.scss, main.css)|
|`src/components`|PrimeVue 컴포넌트를 래핑(Wrapping)하여 만드는 커스텀 컴포넌트를 포함|
|`src/components/base`|(선택) PrimeVue 컴포넌트 기반으로 만든 프로젝트의 기본 UI 컴포넌트 (예: BaseButton.vue가 PrimeVue의 <Button>을 래핑함).|
|`src/App.vue`|Layout을 정의하며, <Toast>나 <Dialog> 등 전역 모달/알림 컴포넌트를 여기에 위치시킬 수 있음|

#### 2.1. 커스텀 컴포넌트 래핑 (Base Components)
> 프로젝트 전체에서 동일한 스타일과 속성(Props)을 가진 버튼이 필요할 때, 
> PrimeVue의 <Button>을 직접 사용하는 대신 커스텀 컴포넌트로 래핑하여 사용 이는 나중에 UI 라이브러리를 변경하거나 
> 디자인을 일관되게 유지하는 데 큰 도움이 됨

```ts
<script setup lang="ts">
import Button from 'primevue/button';
import type { ButtonProps } from 'primevue/button';

// PrimeVue의 ButtonProps를 상속받아 사용
interface Props extends /* @vue-ignore */ ButtonProps {
  // 프로젝트에서 사용하는 특정 스타일을 위한 Prop 추가
  styleType?: 'primary' | 'secondary' | 'danger';
}

const props = defineProps<Props>();

// styleType에 따라 class나 다른 PrimeVue prop을 계산하여 전달
const severity = computed(() => {
    if (props.styleType === 'danger') return 'danger';
    if (props.styleType === 'secondary') return 'secondary';
    return 'primary';
});
</script>

<template>
  <Button v-bind="$attrs" :severity="severity" :label="label || 'Submit'">
    <slot></slot>
  </Button>
</template>
```
