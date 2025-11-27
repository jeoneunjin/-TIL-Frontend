<!-- 
# Diretive : 
    "v-" 접두사가 있는 특수 속성
전체구문) 
    Name:Argument.Modifiers="value"
예시)
    v-on:submit.prevent="onSubmit"
## Built-in Directives
    v-text, v-show, v-if, v-for

## v-if vs v-show
    - v-if 초기 조건이 false인 경우 아무 작업도 수행하지 않음; 토글 비용이 높음
    - v-show 초기 조건에 관계 없이 항상 렌더링; 초기 렌더링 비용이 더 높음
    => 콘텐츠 매우 자주 전환하는 경우 v-show, 
    => 실행 중 조건이 변경되지 않는 경우는 v-if 권장

## v-for with v-if
    - 동일한 요소에서 v-if가 v-for보다 우선순위가 더 높음 
    - v-if 조건은 v-for 범위의 변수에 접근할 수 없음
    * 그럼 어떻게 해결?
    1. computed를 활용해 필더링 된 목록을 반환하여 반복하도록 설정
    2. template에서 반복 돌리고 v-if 위치 이동
    -> 1번 권장

## v-for
    - 순서가 항상 같지 않다(순서에 의존하면 안 된다)
    - key(unique한 값)를 꼭 함께 사용; 데이터의 예측 가능한 행동을 유지하기 위해 같이 사용
    - 주의! index를 key로 사용하지마라; 중간에 있는 데이터 하나라도 삭제하면 모든 index가 업데이트 됨
        -> 변경으로 인지하여 다시 랜더링(불필요한 랜더링을 하게 됨)

# Template Syntax : 
    Dom에 Vue 객체의 데이터를 선언적으로 바인딩할 수 있는 
    HTML 기반 템플릿 구문을 사용

# Dynamically data binding
## v-bind
    하나 이상의 속성 또는 컴포넌트 데이터를 표현식에 동적으로 바인딩
    - Attribute Bindings
## Class and Style Bindings

# Computed :
    계산된 속성을 정의하는 함수; 미리 계산된 속성을 사용하여 
    템플릿에서 표현식을 단순하게 하고 불필요한 반복 연산을 줄임
## method과 computed의 차이? 
    computed 속성은 의존된 반응형 데이터를 기반으로 캐시(cached)된다
    의존하는 데이터가 변경된 경우에만 재평가하고 변경되지 않았다면 이전에 계산된 결과 즉시 반환
    = 의존된 데이터가 변경되면 자동으로 업데이트
    - method는 호출할때마다 항상 함수를 실행(=호출할 때만 실행) 

# watch :
    반응형 데이터를 감시하고, 감시하는 데이터가 변경되면 콜백 함수 호출
    보통 비동기적 감시할 때 사용
## computed과의 차이? 
    computed는 템플릿 내에서 사용되는 데이터 연산용; 계산된 값을 반환
    watch는 데이터 변경에 따른 특정 작업 처리용; 계산된 값을 반환X 특정 작업 수행

    -->

<template>
    <!-- directive example-->
    <a v-bind:href="myUrl">Link</a>
    <button v-on:clock="doSomething">Button</button>
    <form v-on:submit.prevent="onSubmit">...</form>
    <p v-if="seen">Hi There</p>

    <!-- v-if vs v-show -->
    <!-- <img :src="errimg.img1" v-if="age < 0"></img>
    <img :src="errimg.img2" v-show="age < 0"></img> 
    <p>Message: {{ msg }}</p>-->

    <!-- watch -->
    <p> {{ count }} </p>
    <!-- computed -->
    <ui>
        <li> name: {{ user.name }}</li>
        <li> age : {{ user.age }}</li>

        <p>Can Vote? {{ canVote }}</p>
    </ui>

</template>

<script setup>

//----- computed
import {computed, ref,  watch} from 'vue';
const user = ref({
    name: 'Eunjin',
    age : 25,
    salary: 100000000,
});
const canVote = computed(()=>{
    return user.value.age >= 18? 'Yes' : 'No'
});

//----- watch
const count = ref(0)
// count 변수 값 감시
watch(count, (newValue, oldValue)=>{
    console.log(`newValue: ${newValue}, oldValue: ${oldValue}`)
})

</script>