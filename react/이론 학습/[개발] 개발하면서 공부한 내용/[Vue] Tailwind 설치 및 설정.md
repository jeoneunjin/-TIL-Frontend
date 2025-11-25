# Tailwind 설치 및 설정 과정

## 📑 목차
- [1. Tailwind 설치](#1-설치)
- [2. 설정 파일 작성](#2-설정-파일-작성)
  
---

## 1. 설치
```bash
#----v3버전용
#npm
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p

#pnpm 
pnpm add -D tailwindcss postcss autoprefixer
pnpx tailwindscss init -p


#----v4버전용(내가 설치한 것)
#pnpm
pnpm add -D tailwindcss postcss autoprefixer
pnpm add -D @tailwindcss/cli postcss autoprefixer
pnpx @tailwindcss/cli init -p
```

## 2. 설정 파일 작성
```text
my-project/
├─ tailwind.config.js (수동 생성)
├─ src/
│  ├─ assets/
│  │  └─ styles/
│  │      └─ tailwind.css  ← Tailwind 엔트리
│  ├─ main.ts  ← 여기에서 import
│  ├─ App.vue
│  └─ ...
```
### 2-1. tailwind.config.js

- Tailwind가 어떤 파일을 스캔할지(HTML/TS/JS/Vue)
- 테마 확장(색상, 폰트 등)
- 플러그인 추가
- Tailwind 커스텀의 핵심 파일

```js
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts}"
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### 2-2. tailwind.css (엔트리 CSS)

- `@tailwind base;`, `@tailwind components;`, `@tailwind utilities;`
- Tailwind의 모든 스타일이 실제로 빌드되는 파일

```css
@import "tailwindcss";

/* PrimeVue 기본 스타일 */
@import "primevue/resources/themes/lara-light-blue/theme.css";
@import "primeicons/primeicons.css";

html, body {
    @apply bg-gray-50 text-gray-900;
}
```
