"use client";

import { apiRequest } from "./api-client";
import { responseErrorToString } from "./error";
import { PublisherOrder, PublisherOrderStatus } from "@/types/publisher-orders";

const BASE_URL = "/publisher-orders";

export async function getAllPublisherOrders(status?: PublisherOrderStatus): Promise<PublisherOrder[]> {
    try {
        const endpoint = status 
            ? `${BASE_URL}?status=${status}`
            : BASE_URL;
        
        const response = await apiRequest(endpoint, {
            method: "GET",
        });

        if (!response.ok) {
            const errorMessage = await responseErrorToString(response);
            throw new Error(`Failed to fetch publisher orders: ${errorMessage}`);
        }

        return await response.json();
    } catch (error) {
        console.error("Error fetching publisher orders:", error);
        throw error;
    }
}

export async function getPublisherOrderById(orderId: number): Promise<PublisherOrder> {
    try {
        const response = await apiRequest(`${BASE_URL}/${orderId}`, {
            method: "GET",
        });

        if (!response.ok) {
            const errorMessage = await responseErrorToString(response);
            throw new Error(`Failed to fetch publisher order: ${errorMessage}`);
        }

        return await response.json();
    } catch (error) {
        console.error("Error fetching publisher order:", error);
        throw error;
    }
}

export async function confirmPublisherOrder(orderId: number): Promise<PublisherOrder> {
    try {
        const response = await apiRequest(`${BASE_URL}/${orderId}/confirm`, {
            method: "PUT",
        });

        if (!response.ok) {
            const errorMessage = await responseErrorToString(response);
            throw new Error(`Failed to confirm publisher order: ${errorMessage}`);
        }

        return await response.json();
    } catch (error) {
        console.error("Error confirming publisher order:", error);
        throw error;
    }
}

