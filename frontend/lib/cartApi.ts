'use server';
import { CartBookPrice } from "./mockCartData";

const BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080/v1/api";
const ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2huIiwidXNlcklkIjoxMSwicm9sZSI6IkN1c3RvbWVyIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc2Njc1NzQ5NiwiZXhwIjoxNzY2NzYxMDk2fQ.s6KisSulUkBTEvvXaOO3_9SVTUULMa_un3rKrVwdI138biHpp2HjpyY_c9oXyCKMlLlB_DWtgId5ZOntWgYa5Q";

export async function getCartItems(): Promise<CartBookPrice[]> {
    try {
        const res = await fetch(`${BASE_URL}/cart/items`, {
            headers: { "Authorization": ACCESS_TOKEN },
            method: "GET",
            mode: "cors"
        });

        console.log("getCartItems response status:", res.status);

        const text = await res.text();
        if (!text) return []; // empty cart
        return JSON.parse(text);
    } catch (err) {
        console.error("Cart fetch error:", err);
        return []; // fallback empty array
    }
}

export async function getCartTotal(): Promise<number> {
    try {
        const res = await fetch(`${BASE_URL}/cart/total`, {
            headers: { "Authorization": ACCESS_TOKEN },
            method: "GET",
            mode: "cors"
        });

        console.log("getCartTotal response status:", res.status);

        const text = await res.text();
        if (!text) return 0; // empty total
        return JSON.parse(text);
    } catch (err) {
        console.error("Cart total fetch error:", err);
        return 0; // fallback zero
    }
}

export async function removeBook(isbn: string): Promise<void> {
    try {
        const res = await fetch(`${BASE_URL}/cart/remove`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": ACCESS_TOKEN
            },
            body: JSON.stringify({ isbn }),
            mode: "cors"
        });

        console.log("removeBook response status:", res.status);
        // backend may not return res.ok = false, so no error thrown
    } catch (err) {
        console.error("Remove book error:", err);
        throw err;
    }
}

export async function clearCart(): Promise<void> {
    try {
        const res = await fetch(`${BASE_URL}/cart/clear`, {
            method: "DELETE",
            headers: { "Authorization": ACCESS_TOKEN },
            mode: "cors"
        });

        console.log("clearCart response status:", res.status);
        // backend may not return res.ok = false
    } catch (err) {
        console.error("Clear cart error:", err);
        throw err;
    }
}
