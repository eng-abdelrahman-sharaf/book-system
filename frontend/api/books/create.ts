import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { Book } from "@/types/book";

export type CreateBookPayload = {
    isbn: string;
    title: string;
    publisherId: number;
    publicationYear?: number;
    sellingPrice: number;
    category: Book["category"];
    numberOfBooks?: number;
    threshold?: number;
    authorName?: string | null;
};

export async function createBook(payload: CreateBookPayload): Promise<void> {
    const res = await apiRequest("/books", {
        method: "POST",
        body: JSON.stringify(payload),
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}
