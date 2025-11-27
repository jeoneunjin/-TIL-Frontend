import { resourceApi } from "@/api";
import { API_ENDPOINTS } from "@/api/endpoints";
import { defineStore } from "pinia";
import { ref } from 'vue';

export const useUserStore=defineStore("user", () => {
    const profile = ref(null);
    const fetchProfile = async() => {
        await resourceApi.get(API_ENDPOINTS.USER.PROFILE)
        .then((response) =>{
            profile.data = response.data;
        })
    }

    const getProfile = () => {
        return profile.data;
    }

    return {
        fetchProfile,
        getProfile,
    }
})