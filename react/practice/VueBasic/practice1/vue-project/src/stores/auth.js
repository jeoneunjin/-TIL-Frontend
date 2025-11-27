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
    };

    return {
        login,
    };
});
