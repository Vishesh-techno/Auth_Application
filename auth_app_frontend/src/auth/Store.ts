import type LoginData from '@/models/LoginData';
import type LoginResponseData from '@/models/LoginResponseData';
import type User from '@/models/User';
import { loginUser, logoutUser } from '@/services/AuthService';
import { create } from 'zustand';
import { persist } from 'zustand/middleware'

const LOCAL_KEY = "app_state"
const ACCESS_TOKEN_KEY = "authToken"
const REFRESH_TOKEN_KEY = "refreshToken"

type AuthState = {
    accessToken: string | null;
    refreshToken: string | null;
    user: User | null;
    authStatus: boolean;
    authLoading: boolean;
    login: (loginData: LoginData) => Promise<LoginResponseData | null>;
    loginWithOtp: (response: LoginResponseData) => void;
    setAuthSession: (response: Partial<LoginResponseData>) => void;
    logout: (silent?: boolean) => void;
    checkLogin: () => boolean;
};

const useAuth = create<AuthState>()(
    persist((set, get) => ({
        accessToken: null,
        refreshToken: null,
        user: null,
        authStatus: false,
        authLoading: false,
        login: async (loginData) => {
            set({ authLoading: true });
            try {
                const response = await loginUser(loginData);
                if (response) {
                    get().setAuthSession(response);
                }
                return response;
            } finally {
                set({ authLoading: false });
            }
        },
        loginWithOtp: (response) => {
            get().setAuthSession(response);
        },
        setAuthSession: (response) => {
            set((state) => {
                const accessToken = response.accessToken ?? state.accessToken;
                const refreshToken = response.refreshToken ?? state.refreshToken;
                const user = response.user ?? state.user;
                const authStatus = Boolean(accessToken);

                if (accessToken) {
                    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
                }
                if (response.refreshToken) {
                    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
                }

                return {
                    accessToken,
                    refreshToken,
                    user,
                    authStatus,
                };
            });
        },
        logout: async (silent = false) => {
            if (!silent) {
                try {
                    await logoutUser();
                } catch (error) {
                    console.warn(error);
                }
            }

            localStorage.removeItem(ACCESS_TOKEN_KEY);
            localStorage.removeItem(REFRESH_TOKEN_KEY);

            set({
                accessToken: null,
                refreshToken: null,
                user: null,
                authLoading: false,
                authStatus: false
            });
        },
        checkLogin: () => {
            return Boolean(get().accessToken && get().authStatus);
        }
    }), {
        name: LOCAL_KEY
    })
);

export default useAuth;