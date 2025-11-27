<script setup>
import { useUserStore  } from '@/stores/user';
import { onBeforeMount } from 'vue';
import { ref } from 'vue';

const userStore = useUserStore();
const profile = ref(null);

const loadProfile = async () => {
    await userStore.fetchProfile()
    .then(()=>{
        profile.value = userStore.getProfile();
        console.log(profile.value);
    })
    .catch((error) => {
        console.log(error);
    })
}

onBeforeMount(async() => {
    await loadProfile();
})

</script>

<template>
    <h1>메인페이지임</h1>

    <h2>프로필 정보</h2>

    <div v-if="profile">
        <div>이메일: {{ profile.email }}</div>
        <div>이름: {{ profile.name }}</div>
        <div>권한: {{ profile.role }}</div>
    </div>
    <div v-else>
        <div>프로필 로딩중 ...</div>
    </div>
    
</template>

<style scoped>
</style>