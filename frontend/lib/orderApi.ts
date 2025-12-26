'use server';

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

const ACCESS_TOKEN =
    "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2huIiwidXNlcklkIjoxMSwicm9sZSI6IkN1c3RvbWVyIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc2Njc2NDM3NywiZXhwIjoxNzY2NzY3OTc3fQ.BX5_HXqn1IBjv6iAsnvBJ_iGFjiviriLHG0GOfGfWQewaYxLKD7YMmvRbKSK1LG1snu2SjS9u3gfTyTrDI6hcA";

export async function getPastOrders(): Promise<OrderDetails[]> {
    try {
        const res = await fetch(`${BASE_URL}/user/orders`, {
            method: "GET",
            headers: {
                "Authorization": ACCESS_TOKEN
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
