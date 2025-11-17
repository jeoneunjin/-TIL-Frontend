# Vue + TypeScript 프로젝트 시작

## 목차
- [Vue + TypeScript 프로젝트 시작](#vue--typescript-프로젝트-시작)
  - [목차](#목차)
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
    - [1. PrimeVue 설치 및 초기 설정](#1-primevue-설치-및-초기-설정)
      - [0. PrimeVue 사용을 고려한 구조 확장](#0-primevue-사용을-고려한-구조-확장)
      - [1.1. PrimeVue 및 종속성 설치](#11-primevue-및-종속성-설치)
      - [1-2. `main.ts` 파일 설정](#1-2-maints-파일-설정)
      - [1-3. 컴포넌트 등록 방식](#1-3-컴포넌트-등록-방식)
    - [2. 프로젝트 구조](#2-프로젝트-구조)
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

### 1. PrimeVue 설치 및 초기 설정

#### 0. PrimeVue 사용을 고려한 구조 확장

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

#### 1.1. PrimeVue 및 종속성 설치
Vite로 프로젝트를 생성한 후, PrimeVue와 필수적인 아이콘 라이브러리(예: PrimeIcons)를 설치

```Bash
# PrimeVue 설치
npm install primevue@^3.52.0

# PrimeIcons 설치 (필수)
npm install primeicons
```
--- 

#### 1-2. `main.ts` 파일 설정
PrimeVue를 애플리케이션 전역에서 사용할 수 있도록 src/main.ts 파일에 Vue 플러그인으로 등록하고 스타일을 가져와야 함

```ts
// src/main.ts

import { createApp } from 'vue';
import App from './App.vue';
import PrimeVue from 'primevue/config';

// 1. 테마 파일 (필수)
import 'primevue/resources/themes/aura-light-green/theme.css';
// 2. 기본 CSS (필수)
import 'primevue/resources/primevue.min.css';
// 3. 아이콘 (필수)
import 'primeicons/primeicons.css';

const app = createApp(App);

app.use(PrimeVue, {
  // PrimeVue 전역 설정 옵션 (예: Ripple 효과 활성화)
  ripple: true
});

// Vue Router, Pinia 등 다른 플러그인 등록
// app.use(router);
// app.use(pinia);

app.mount('#app');
```
> ⚠️ 테마 선택: 위 예시에서는 aura-light-green 테마를 사용했음
> PrimeVue 공식 웹사이트에서 원하는 테마 (예: lara-light-blue, arya-green 등)로 변경하여 사용

---

#### 1-3. 컴포넌트 등록 방식
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

---

### 2. 프로젝트 구조
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
> 디자인을 일관되게 유지하는 데 큰 도움이 됩니다.

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
