'use client';

import { CartBookPrice } from "./mockCartData";
import { apiRequest } from "./api-client";

const BASE_PATH = "/cart";

/* =========================
   API Calls
========================= */

export async function getCartItems(): Promise<CartBookPrice[]> {
    try {
        const res = await apiRequest(`${BASE_PATH}/items`, {
            method: "GET",
        });

        if (!res.ok) {
            console.error("getCartItems failed:", res.status);
            return [];
        }

        const text = await res.text();
        return text ? JSON.parse(text) : [];
    } catch (err) {
        console.error("Cart fetch error:", err);
        return [];
    }
}

export async function getCartTotal(): Promise<number> {
    try {
        const res = await apiRequest(`${BASE_PATH}/total`, {
            method: "GET",
        });

        if (!res.ok) {
            console.error("getCartTotal failed:", res.status);
            return 0;
        }

        const text = await res.text();
        return text ? JSON.parse(text) : 0;
    } catch (err) {
        console.error("Cart total fetch error:", err);
        return 0;
    }
}

export async function removeBook(isbn: string): Promise<boolean> {
    try {
        const res = await apiRequest(`${BASE_PATH}/remove`, {
            method: "DELETE",
            body: JSON.stringify({ isbn }),
        });

        return res.ok;
    } catch (err) {
        console.error("Remove book error:", err);
        return false;
    }
}

export async function clearCart(): Promise<boolean> {
    try {
        const res = await apiRequest(`${BASE_PATH}/clear`, {
            method: "DELETE",
        });

        return res.ok;
    } catch (err) {
        console.error("Clear cart error:", err);
        return false;
    }
}
