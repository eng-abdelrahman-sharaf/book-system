'use server';
import { getAccessToken } from '@/lib/token-storage';

export interface OrderItem {
    orderId: number;
    isbn: string;
    quantity: number;
    price: number;
    title: string;
}

export interface OrderDetails {
    orderId: number;
    orderDate: string;
    totalAmount: number;
    items: OrderItem[];
}

const BASE_URL =
    process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080/v1/api";

   

export async function getPastOrders(ACCESS_TOKEN:any): Promise<OrderDetails[]> {
    try {
        const res = await fetch(`${BASE_URL}/user/orders`, {
            method: "GET",
            headers: {
                  Authorization: `Bearer ${ACCESS_TOKEN}`
            },
            mode: "cors"
        });

        console.log("getPastOrders status:", res.status);

        const text = await res.text();
        if (!text) return [];

        return JSON.parse(text);
    } catch (err) {
        console.error("Get past orders error:", err);
        return [];
    }
}
