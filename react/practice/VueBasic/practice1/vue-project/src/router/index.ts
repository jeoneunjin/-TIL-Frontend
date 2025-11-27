import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/pages/LoginPage.vue') // lazy loading
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: routes
})

export default router
