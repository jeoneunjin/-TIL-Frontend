<!-- script, template, style로 구성된 걸 볼 수 있다 -->

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 실제 DOM을 직접 접근하는 대신 ref를 통해 간접 관리
const titleRef = ref<HTMLElement | null>(null)

// 컴포넌트가 실제 DOM에 마운트된 후 접근 가능
onMounted(() => {
  console.log("Mounted! titleRef 값:", titleRef.value)
  // 가상 DOM이 실제 DOM과 동기화된 이후에야 DOM 조작 가능

  titleRef.value!.style.fontSize = "28px"
  
})

// 버튼 클릭 시 DOM 조작 (간접 접근)
const changeColor = () => {
  titleRef.value!.style.color = "tomato"
}

</script>

<template>
  <h1>You did it!</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the
    documentation
  </p>

  <div>
    <h1 ref="titleRef">Hello Vue!</h1>

    <button @click="changeColor">
      Change Title Color
    </button>
  </div>
</template>

<style scoped>
div {
  padding: 20px;
}
button {
  margin-top: 10px;
}
</style>
