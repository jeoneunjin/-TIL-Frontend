import {useAuthStore} from "@/stores/auth.js";
import {API_ENDPOINTS} from "@/api/endpoints.js";
import {authApi, resourceApi} from "@/api/index.js";
import router from "@/routers/index.js";

let isRefreshing = false;
let failedQueue = [];

export const setupInterceptor = () => {
    setupRequestInterceptor(authApi);
    setupResponseInterceptor(authApi);

    setupRequestInterceptor(resourceApi);
    setupResponseInterceptor(resourceApi);
}

const setupRequestInterceptor = (api) => {
    api.interceptors.request.use(config => {
        const authStore = useAuthStore();

        if (authStore.existsAccessToken()) {
            config.headers["Authorization"] = authStore.getAccessToken();
        }

        return config;
    }, (error)  => {
        return Promise.reject(error);
    })
}

const setupResponseInterceptor = (api) => {
    api.interceptors.response.use((config) => {
        return config;
    }, async (error) => {

        const originalRequest = error.config;

        if(error.response.status === 401 && !originalRequest._retry ) { // 401이 아니고, 재요청이 아닌 경우
            return handleUnauthorizedError();
        }
    })
}

const handleLogout = async () => {
    const authStore = useAuthStore();
    await authStore.logout();
    await router.push("/login");
}

const addQueue = (originalRequest, api) => {
    return new Promise((resolve, reject) => {
        failedQueue.push({resolve, reject});
    }).then((token) => {
        if (originalRequest.headers) {
            originalRequest.headers.Authorization = token;
        }

        return api(originalRequest); // 새로 받은 토큰으로 재요청
    }).catch((err) => {Promise.reject()})
}

const handleUnauthorizedError = async (originalRequest, api) => {
    // refresh-token 요청했는데, 401 인 경우,
    // refresh token도 만료된 상태 -> 로그아웃
    if (originalRequest.url.includes(API_ENDPOINTS.AUTH.REFRESH)) {
        await handleLogout();
        return Promise.resolve();
    }

    if (isRefreshing) {
        await addQueue(originalRequest, api);
    }

    return tokenRefreshing(originalRequest, api);
}

const tokenRefreshing = async (originalRequest, api) => {
    originalRequest._retry = true;
    isRefreshing = true;

    try {
        const refreshInstance = axios.create({
            baseURL: authApi.defaults.baseURL,
            withCredentials: true, // refresh token 사용
        })

        const refreshResponse = await refreshInstance.get(API_ENDPOINTS.AUTH.REFRESH);
        const newAccessToken = refreshResponse.headers.authorization;

        if (newAccessToken) {
            const authStore = useAuthStore();
            authStore.setAccessToken(newAccessToken); // 변수에 access token 저장
            originalRequest.headers.Authorization = newAccessToken;
        }

        await setNewToken(null, newAccessToken);
        return api(originalRequest); // 원래 요청 다시 날리기


    } catch (error) {
        await handleLogout();
        await setNewToken(error, null);
        return Promise.reject();
    } finally {
        isRefreshing = false;
    }
}

const setNewToken = async (error, token) => {
    failedQueue.forEach(({resolve, reject}) => {
        if (error) {
            reject(error);
        } else {
            resolve(token);
        }
    });

    failedQueue = [];
}