import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";

export async function deleteAuthor(authorId: number): Promise<void> {
    const res = await apiRequest(`/authors/${authorId}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}

