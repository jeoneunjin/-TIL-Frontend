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
      - [2. 컴포넌트 등록 방식 예시 코드(App.vue)](#2-컴포넌트-등록-방식-예시-코드appvue)
    - [3. 개발 서버 실행 및 확인](#3-개발-서버-실행-및-확인)
  - [3. 추가\_프로젝트 구조](#3-추가_프로젝트-구조)
      - [2.1. 커스텀 컴포넌트 래핑 (Base Components)](#21-커스텀-컴포넌트-래핑-base-components)
  - [추가 설치](#추가-설치)
      - [(추가) Tailwind vs PrimeVue 역할 차이](#추가-tailwind-vs-primevue-역할-차이)
      - [🔹 핵심 차이](#-핵심-차이)
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

> https://primevue.org/vite

#### 1.1. PrimeVue 및 종속성 설치
PrimeVue와 필수적인 아이콘 라이브러리(예: PrimeIcons)를 설치

```Bash
# PrimeVue + PrimeIcons 설치 (필수)
pnpm add primevue primeicons

# PrimeVue 스타일 디자인 정의한 라이블러리 설치
pnpm add primevue @primeuix/themes
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
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

//----- Primevue
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';

//----- Tailwind    
//import './assets/styles/tailwind.css';

const app = createApp(App)

app.use(createPinia())
app.use(router)

//----- PrimeVue 플로그인 등록
app.use(PrimeVue, {
    preset: Aura //(선택 사항)
})

app.mount('#app')

```
---

#### 2. 컴포넌트 등록 방식 예시 코드(App.vue)
> TailWind 또한 같이 확인하는 코드이므로, tailwind 설정 끝난 후 확인 또는 tailwind 부분만 주석 처리할 것
```vue
<script setup lang="ts">
  import { ref } from 'vue';
  import Button from 'primevue/button';
  import InputText from 'primevue/inputtext';

  // PrimeVue InputText에 바인딩할 데이터
  const welcomeName = ref('사용자');
</script>

<template>
  <!-- 
    Tailwind CSS v4 테스트 영역:
    - bg-teal-500: 배경색
    - text-white: 텍스트 색상
    - p-6: 큰 패딩
    - rounded-xl: 둥근 모서리
  -->
  <div class="min-h-screen bg-gray-100 flex flex-col items-center justify-center p-4 sm:p-8">
    
    <div class="bg-teal-500 text-white p-6 rounded-xl shadow-2xl max-w-lg w-full mb-8 transition duration-300 hover:shadow-3xl">
      <h1 class="text-3xl font-bold mb-2">Tailwind CSS Test (v4)</h1>
      <p class="text-sm opacity-90">이 블록이 짙은 청록색이면 Tailwind가 잘 적용된 것입니다.</p>
    </div>

    <!-- PrimeVue 컴포넌트 및 상호작용 테스트 영역 -->
    <div class="bg-white p-8 rounded-xl shadow-xl max-w-lg w-full space-y-6">
      <h2 class="text-2xl font-semibold text-gray-800 border-b pb-3 mb-4">PrimeVue & Tailwind Demo</h2>

      <!-- InputText 컴포넌트 테스트 -->
      <div class="flex flex-col space-y-2">
        <label for="username" class="text-gray-600 font-medium">이름 입력:</label>
        <InputText id="username" v-model="welcomeName" class="w-full" type="text" />
      </div>

      <!-- PrimeVue Button 컴포넌트 테스트 -->
      <div class="flex flex-col sm:flex-row gap-4">
        <Button label="기본 버튼 (Aura 테마)"></Button>
        
        <!-- Tailwind 유틸리티 클래스로 PrimeVue 버튼 스타일링 -->
        <Button 
          :label="`${welcomeName}님 환영합니다!`" 
          severity="success" 
          class="shadow-lg hover:shadow-xl transition duration-300"
        ></Button>
      </div>

      <p class="mt-6 text-sm text-gray-500">
        👆 위의 버튼과 입력창에 PrimeVue의 'Aura' 테마 스타일이 적용되었는지 확인하세요.
      </p>
    </div>
  </div>
</template>

<style scoped></style>
```

### 3. 개발 서버 실행 및 확인
- `npm run dev`로 개발 서버 실행
- `http://localhost:5173/`으로 화면 확인

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

---

## 추가 설치
- axios 설치

> https://axios-http.com/kr/docs/intro

```bash
#npm
$ npm install axios

#pnpm 
$ pnpm add axios
```

- vue-router 설치(설정에서 이미 설치함)

> https://router.vuejs.org/installation.html

```bash
#npm
npm install vue-router@4

#pnpm
pnpm add vue-router@4
```

- pinia 설치(설정에서 이미 설치함)

- Tailwind 설치

#### (추가) Tailwind vs PrimeVue 역할 차이

| 라이브러리            | 역할          | 특징                                                           |
| ---------------- | ----------- | ------------------------------------------------------------ |
| **Tailwind CSS** | **스타일링**    | HTML 요소에 유틸리티 클래스 붙여서 디자인 적용. 버튼, 텍스트, 레이아웃 등 모든 스타일을 직접 조합. |
| **PrimeVue**     | **UI 컴포넌트** | 버튼, 테이블, 달력, 모달 등 미리 만들어진 Vue 컴포넌트 제공. 내부 로직 포함.             |


#### 🔹 핵심 차이
1. Tailwind(**스타일 중심**)
  - “스타일을 어떻게 입힐지” 개발자가 직접 결정
  - 자유도가 높고, 커스터마이징 쉽고, 반응형/상태 스타일 쉽게 가능
  - HTML에 클래스 붙이는 방식 → UI 로직은 없음
2. PrimeVue(**기능 중심**)
  - “어떤 기능을 가진 UI를 바로 쓰고 싶을 때” 사용
  - 내부 동작(클릭 이벤트, 폼 validation, 테이블 정렬 등)이 이미 구현됨
  - 스타일은 어느 정도 기본 제공 → Tailwind로 세부 디자인 커스터마이징 가능