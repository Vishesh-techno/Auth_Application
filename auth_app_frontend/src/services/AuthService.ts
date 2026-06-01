import apiClient from "@/config/ApiClient"
import type LoginData from "@/models/LoginData";
import type LoginResponseData from "@/models/LoginResponseData";
import type RegisterData from "@/models/RegisterData"

export const registerUser = async (signupData: RegisterData) => {
    const response = await apiClient.post<LoginResponseData>('/auth/register', signupData)
    return response.data;
}

export const loginUser = async (loginData: LoginData) => {
    const response = await apiClient.post<LoginResponseData>('/auth/login', loginData)
    return response.data;
}

export const sendOtp = async (payload: { email: string }) => {
    const response = await apiClient.post('/otp/send', payload)
    return response.data;
}

export const verifyOtp = async (payload: { email: string; otp: string }) => {
    const response = await apiClient.post<LoginResponseData>('/otp/verify', payload)
    return response.data;
}

export const logoutUser = async () => {
    const response = await apiClient.post('/auth/logout');
    return response.data;
}

