"use server";

import { responseErrorToString } from "@/lib/error";

const BACKEND_URL = process.env.BACKEND_URL || process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

export const fetchTopCustomers = async (
    token: string
) => {
    
    const response = await fetch(
        `${BACKEND_URL}/v1/api/user/reports/TopBookCustomers`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response.status);

    return response.json();
};

export const fetchPreviousMonthSales = async (
    token: string
) => {
    
    const response = await fetch(
        `${BACKEND_URL}/v1/api/user/reports/totalsales`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response.status);

    return response.json();
};

export const fetchDailySales = async (
    date: string,
    token: string
) => {
    
    const response = await fetch(
        `${BACKEND_URL}/v1/api/user/reports/t?date=${date}`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response.status);

    return response.json();
};
export const getAllbooks = async (
    token: string
) => {
    
    const response = await fetch(
        `${BACKEND_URL}/v1/api/books`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response);

    return response.json();
};

export const fetchBookReport = async (
    isbn: string,
    token: string
) => {
    console.log(isbn)
    
    const response = await fetch(
        `${BACKEND_URL}/v1/api/user/reports/orderd/${isbn?isbn:-1}`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response);

    return response.json();
};

export const fetchTopBooks = async (
    token: string
) => {
    
    const response = await fetch(
        `${BACKEND_URL}/v1/api/user/reports/TopBooksales`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response);

    return response.json();
};

export const getAllUsers = async (
    token: string
) => {
    
    const response = await fetch(
        `http://localhost:8080/v1/api/user/all`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response);

    return response.json();
};

export const makeUserAdmin = async (
    userId: number,
    token: string
) => {
    
    const response = await fetch(
        `http://localhost:8080/v1/api/user/make-admin/${userId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP statussss:", response);

    return response.text();
};

export const dismissAdmin = async (
    userId: number,
    token: string
) => {
    
    const response = await fetch(
        `http://localhost:8080/v1/api/user/dismiss-admin/${userId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (response.status === 401) {
        throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "SERVER_ERROR");
    }

    console.log("HTTP status:", response);

    return response.text();
};