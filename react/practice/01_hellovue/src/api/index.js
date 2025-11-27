import axios from "axios";
import { setupInterceptor } from "./interceptor";

const { VITE_AUTHENTICATION_SERVER_URL, VITE_RESOURCE_SERVER_URL } = import.meta.env;

export const authApi = axios.create({
  baseURL: VITE_AUTHENTICATION_SERVER_URL,
  headers: {
    "Content-Type": "application/json", //데이터를 JSON으로 주고받음
  },
  withCredentials: true//이 옵션이 없으면 토큰을 못 불러옴
})

export const resourceApi = axios.create({
  baseURL: VITE_RESOURCE_SERVER_URL,
  headers: {
    "Content-Type": "application/json", //데이터를 JSON으로 주고받음
  },
  withCredentials: true//이 옵션이 없으면 토큰을 못 불러옴
})

setupInterceptor();