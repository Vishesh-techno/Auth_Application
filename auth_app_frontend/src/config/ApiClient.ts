import useAuth from "@/auth/Store";
import axios from "axios";

const ACCESS_TOKEN_KEY = "authToken";
const REFRESH_TOKEN_KEY = "refreshToken";

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
    headers: {
        "Content-Type": "application/json"
    },
    withCredentials: true,
    timeout: 30000
});

const storedAccessToken = localStorage.getItem(ACCESS_TOKEN_KEY);
if (storedAccessToken) {
    apiClient.defaults.headers.common["Authorization"] = `Bearer ${storedAccessToken}`;
}

let isRefreshing = false;
const refreshSubscribers: Array<(token: string | null, error?: unknown) => void> = [];

const onRefreshed = (token: string | null, error?: unknown) => {
    refreshSubscribers.forEach((callback) => callback(token, error));
    refreshSubscribers.length = 0;
};

const subscribeRefresh = (callback: (token: string | null, error?: unknown) => void) => {
    refreshSubscribers.push(callback);
};

const getAccessToken = () => useAuth.getState().accessToken ?? localStorage.getItem(ACCESS_TOKEN_KEY);
const getRefreshToken = () => useAuth.getState().refreshToken ?? localStorage.getItem(REFRESH_TOKEN_KEY);

const setAuthSession = (accessToken: string, refreshToken?: string) => {
    useAuth.setState({
        accessToken,
        refreshToken: refreshToken ?? useAuth.getState().refreshToken,
        authStatus: true
    });
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    apiClient.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;

    if (refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
};

const clearAuth = () => {
    useAuth.getState().logout(true);
    delete apiClient.defaults.headers.common["Authorization"];
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
};

const tryRefreshToken = async () => {
    const refreshToken = getRefreshToken();
    const response = await apiClient.post("/auth/refresh", refreshToken ? { refreshToken } : undefined);
    const token = response.data?.accessToken;
    const newRefreshToken = response.data?.refreshToken ?? refreshToken;

    if (!token) {
        throw new Error("Unable to refresh access token");
    }

    setAuthSession(token, newRefreshToken ?? undefined);
    return token;
};

apiClient.interceptors.request.use((config) => {
    const accessToken = getAccessToken();
    if (accessToken) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error?.config;
        const status = error?.response?.status;

        if (status !== 401 || !originalRequest) {
            return Promise.reject(error);
        }

        const failedRequest = originalRequest as any;
        if (failedRequest._retry) {
            return Promise.reject(error);
        }

        if (failedRequest.url?.includes("/auth/refresh") || failedRequest.url?.includes("/auth/login") || failedRequest.url?.includes("/auth/logout")) {
            clearAuth();
            return Promise.reject(error);
        }

        if (isRefreshing) {
            return new Promise((resolve, reject) => {
                subscribeRefresh((token, refreshError) => {
                    if (refreshError || !token) {
                        reject(refreshError || error);
                        return;
                    }
                    failedRequest.headers = failedRequest.headers ?? {};
                    failedRequest.headers.Authorization = `Bearer ${token}`;
                    resolve(apiClient(failedRequest));
                });
            });
        }

        failedRequest._retry = true;
        isRefreshing = true;

        try {
            const token = await tryRefreshToken();
            onRefreshed(token);
            originalRequest.headers = originalRequest.headers ?? {};
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
        } catch (refreshError) {
            onRefreshed(null, refreshError);
            clearAuth();
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

export default apiClient;