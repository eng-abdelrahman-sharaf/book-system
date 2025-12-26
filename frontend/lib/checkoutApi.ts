'use server';

const BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080/v1/api";
const ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2huIiwidXNlcklkIjoxMSwicm9sZSI6IkN1c3RvbWVyIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc2Njc1NzQ5NiwiZXhwIjoxNzY2NzYxMDk2fQ.s6KisSulUkBTEvvXaOO3_9SVTUULMa_un3rKrVwdI138biHpp2HjpyY_c9oXyCKMlLlB_DWtgId5ZOntWgYa5Q"; // your token

export interface CheckoutRequest {
    cardNumber?: string;
    expirationDate?: string;
    billingAddress?: string;
}

export async function submitCheckout(payload: any) {
    try {
        const res = await fetch(`${BASE_URL}/cart/checkout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": ACCESS_TOKEN
            },
            body: JSON.stringify(payload),
            mode: "cors"
        });

        const text = await res.text();

        if (!text) {
            throw new Error("Empty response from server");
        }

        const data = JSON.parse(text);

        // ✅ backend now guarantees message
        if (!data.orderId) {
            throw new Error(data.message || "Checkout failed");
        }

        return data;
    } catch (err: any) {
        console.error("Checkout error:", err);
        throw err;
    }
}

// Only returns card number as string ("" if none)
export async function getSavedCard(): Promise<string> {
    try {
        const res = await fetch(`${BASE_URL}/cart/card`, {
            headers: { "Authorization": ACCESS_TOKEN },
            method: "GET",
            mode: "cors"
        });
        const text = await res.text();
        return text || "";
    } catch (err) {
        console.error("Get saved card error:", err);
        return "";
    }
}
