import { defineStore } from 'pinia';
import { ref } from 'vue';
import { authApi, resourceApi } from "@/api";
import { API_ENDPOINTS } from "@/api/endpoints.js";

export const useAuthStore = defineStore('auth', () => {

    const accessToken = ref(null);

    const login = async (loginRequest) => {
        await authApi.post(API_ENDPOINTS.AUTH.LOGIN, loginRequest)
            .then((response) => {
                let token = response.headers.authorization;

                accessToken.value = token;
                authApi.defaults.headers.common["Authorization"] = token;
                resourceApi.defaults.headers.common["Authorization"] = token;
            })
            .catch((error) => {
                throw error;
            });
    }

    const logout = async () => {
        await authApi.post(API_ENDPOINTS.AUTH.LOGOUT)
            .then((response) => {})
            .catch((error)=> {})
    }

    const existsAccessToken = () => {
        return accessToken.value;
    }

    const getAccessToken = () => {
        return accessToken.value;
    }

    const setAccessToken = (newToken) => {
    accessToken.value = newToken;
    };
    
    const refresh = async() => {
        await authApi.get(API_ENDPOINTS.AUTH.REFRESH_TOKEN)
            .then((response) => {
                    let token = response.headers.authorization;
                    accessToken.value = token;
                    authApi.defaults.headers.common["Authorization"] = token;
                    resourceApi.defaults.headers.common["Authorization"] = token;
                })
    }

    return {
        login,
        refresh,
        logout,
        existsAccessToken,
        getAccessToken,
        setAccessToken,
    };
});