"use server";
import { responseErrorToString } from "@/lib/error";
import { SignUpFormData } from "@/types/authentication/signup";

const BACKEND_URL = process.env.BACKEND_URL || process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

export const signUp = async (data: SignUpFormData) => {
    const response = await fetch(`http://localhost:8080/v1/api/auth/signup`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username: data.username,
            password: data.password,
            firstName: data.firstName,
            lastName: data.lastName,
            email: data.email,
            phone: data.phone,
            shippingAddress: data.address,
        }),
    });

    if (!response.ok) {
        throw new Error(
            "Failed to sign up: " + (await responseErrorToString(response))
        );
    }
};
