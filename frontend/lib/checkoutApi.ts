'use client';

import { apiRequest } from "./api-client";

/* =========================
   Types
========================= */

export interface CheckoutRequest {
    cardNumber?: string;
    expirationDate?: string;
    billingAddress?: string;
}

export interface CheckoutResponse {
    orderId: number;
    message: string;
}

/* =========================
   API Calls
========================= */

export async function submitCheckout(
    payload: CheckoutRequest
): Promise<CheckoutResponse> {

    const res = await apiRequest("/cart/checkout", {
        method: "POST",
        body: JSON.stringify(payload),
    });

    const text = await res.text();

    if (!text) {
        throw new Error("Empty response from server");
    }

    let data: any;
    try {
        data = JSON.parse(text);
    } catch {
        throw new Error(text);
    }

    if (!res.ok) {
        throw new Error(data.message || "Checkout failed");
    }

    if (!data.orderId) {
        throw new Error(data.message || "Order was not created");
    }

    return data;
}

/**
 * Returns saved card number or empty string
 */
export async function getSavedCard(): Promise<string> {
    const res = await apiRequest("/cart/card", {
        method: "GET",
    });

    const text = await res.text();
    return text || "";
}
