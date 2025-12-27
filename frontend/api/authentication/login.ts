"use server";

import { responseErrorToString } from "@/lib/error";
import { LoginFormData, LoginResponse } from "@/types/authentication/login";

const BACKEND_URL = process.env.BACKEND_URL || process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";


export const login = async (data: LoginFormData): Promise<LoginResponse> => {
    const response = await fetch(`http://localhost:8080/v1/api/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include", // Include cookies for refresh token
        body: JSON.stringify({
            username: data.username,
            password: data.password,
        }),
    });

    if (!response.ok) {
        const errorMessage = await responseErrorToString(response);
        throw new Error(`Failed to log in: ${errorMessage}`);
    }

    const loginResponse: LoginResponse = await response.json();
    return loginResponse;
};





