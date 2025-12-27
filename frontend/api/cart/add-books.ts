import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";

export interface AddBookItem {
    isbn: string;
    quantity: number;
}

export async function addBooks(items: AddBookItem[]): Promise<void> {
    const res = await apiRequest("/cart/add-books", {
        method: "POST",
        body: JSON.stringify({ items }),
    });

    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}
