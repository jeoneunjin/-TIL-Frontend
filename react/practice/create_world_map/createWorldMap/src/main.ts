import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config';

//-------------PrimeVue 필수 스타일 가져오기----------------------//
import 'primevue/resources/themes/aura-light-green/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css'; 

const app = createApp(App)
const pinia = createPinia(); //Pinia 인스타 생성

//--------PrimeVue 플로그인 등록 -----//
app.use(PrimeVue, {
    ripple: true
});

app.use(router)
app.use(pinia);

app.mount('#app')
