<!-- 
# 참고 사이트 : 
    https://ko.vuejs.org/guide/essentials/lifecycle.html 

# Lifecycle Hooks :
    Vue 인스턴스의 생애주기 동안 특정 시점에 실행되는 함수
    개발자가 특정 단계에서 의도하는 로직이 실행되도록 함
## 특징 : 
    - Vue는 LifeCycle Hooks에 등록된 콜백 함수들을 인스턴수와 자동 연결
        -> hooks 함수들은 반드시 동기적으로 작성되어야 함
    - 인스턴스 생애 주기의 여러 단계에서 호출되는 여러 hooks들이 있음
        -> onMounted, onUpdated, onUnmouned 등이 있음
-->

<script setup>
    import {ref, onMounted, onUpdated, useTemplateRef} from 'vue'

    const ImageUrl = ref('')
    const img = useTemplateRef("img")

    const getImage = async () => {
        const response = await fetch("url 주소")
        const json = await response.json()
        ImageUrl.value = json[0].url 
    }

    getImage()
    
    console.log('setup 영역', img, img.value)

    onMounted(()=>{
        //구성 요소에 대해서 무언가를 하고 싶으면 최소한 마운트 된 후의 hook을 사용해야 함
        console.log('onMounted 영역', img, img.value)
    })

    onUpdated(()=>{
        console.log('onUpdated 영역', img, img.value)
    })
</script>

<template>
    <img :src="ImageUrl"></img>
</template>

<style>
</style>