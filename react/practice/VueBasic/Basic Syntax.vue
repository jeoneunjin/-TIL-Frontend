<template>

    <a v-bind:href="myUrl">Link</a>
    <button v-on:clock="doSomething">Button</button>
    <form v-on:submit.prevent="onSubmit">...</form>
    <p v-if="seen">Hi There</p>

    <p>Message: {{ msg }}</p>

    <!-- v-bind -->
    

</template>

<script setup>
/*
# Diretive : 
    "v-" 접두사가 있는 특수 속성
전체구문) 
    Name:Argument.Modifiers="value"
예시)
    v-on:submit.prevent="onSubmit"
## Built-in Directives
    v-text, v-show, v-if, v-for

# Template Syntax : 
    Dom에 Vue 객체의 데이터를 선언적으로 바인딩할 수 있는 
    HTML 기반 템플릿 구문을 사용

# Dynamically data binding
## v-bind
    하나 이상의 속성 또는 컴포넌트 데이터를 표현식에 동적으로 바인딩
    - Attribute Bindings
## Class and Style Bindings

*/




</script>