import { useAuthStore } from '@/stores/auth';
import {createRouter, createWebHistory} from 'vue-router'; 

const routes = [
    {
        path: '/', 
        redirect: 'login',
    },
    {
        path: '/login',
        name: 'login',
        component: () => import("@/pages/LoginPage.vue")
    },
    {
        path: '/main',
        name: 'main',
        meta:{
            requiresAuth: true,
        },
        component: () => import("@/pages/MainPage.vue")
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to,from,next) => {

    const authStore = useAuthStore();
    const requiresAuth =to.matched.some(rt =>rt.meta.requiresAuth);

    if(requiresAuth){
        // authStore의 accesstoken이 있는지 확인
        if(authStore.existsAccessToken()){
            next();
            return;
        }

        await authStore.refresh()
        if(authStore.existsAccessToken()){
            next();
            return;
        }

        next({
            path: '/login',
        });

        return;

    }

    next();
})
export default router;