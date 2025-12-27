import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { CreateBookPayload } from "./create";

export async function updateBook(
    isbn: string,
    payload: CreateBookPayload
): Promise<void> {
    const res = await apiRequest(`/books/${encodeURIComponent(isbn)}`, {
        method: "PUT",
        body: JSON.stringify(payload),
    });
    console.log(res)
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}
