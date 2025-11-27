<script setup lang="ts">
    import { useAuthStore } from "@/stores/auth";
    import { useRouter } from "vue-router";
    import {ref} from "vue";

    const router = useRouter();
    const authStore = useAuthStore();
    const loginForm = ref({
        email: "",
        password: "",

    })

    // form submit handler
    const handleLogin = async (event:SubmitEvent) => {
        event.preventDefault()
        await authStore.login(loginForm.value)
            .then(() => {
                router.push("/main")
            })
            .catch((error) => {
                console.log(error)
        })
    }

</script>

<template>
    <div class="login-wrapper">
        <h1>로그인 페이지</h1>

        <form @submit="handleLogin">
            <!-- v-model 양방향 바인딩 -->
            <input v-model="loginForm.email"/>
            <input v-model="loginForm.password"/>
            <button>로그인</button>
        </form>
    </div>
    
</template>

<style scoped>
</style>