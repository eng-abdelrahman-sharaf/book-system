import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { UpdateAuthorPayload } from "@/types/author";

export async function updateAuthor(
    authorId: number,
    payload: UpdateAuthorPayload
): Promise<void> {
    const res = await apiRequest(`/authors/${authorId}`, {
        method: "PUT",
        body: JSON.stringify(payload),
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}

