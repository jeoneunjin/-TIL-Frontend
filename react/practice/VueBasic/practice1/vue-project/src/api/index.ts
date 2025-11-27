import axios from "axios";

const { VITE_AUTHENTICATION_SERVER_URL, VITE_RESOURCE_SERVER_URL } = import.meta.env;

export const authApi = axios.create({
    baseURL: VITE_AUTHENTICATION_SERVER_URL,
    headers: {
        "Content-Type": "application/json", //JSON 형태로 데이터를 주고 받을거임
    },
    withCredentials: true // 이 옵션이 없으면 토큰을 못 불러옴
})

export const resourceApi = axios.create({
    baseURL: VITE_RESOURCE_SERVER_URL,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true // 이 옵션이 없으면 토큰을 못 불러옴
})