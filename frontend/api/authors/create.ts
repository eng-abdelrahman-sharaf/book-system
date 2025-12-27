import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { CreateAuthorPayload } from "@/types/author";

export async function createAuthor(payload: CreateAuthorPayload): Promise<void> {
    const res = await apiRequest("/authors", {
        method: "POST",
        body: JSON.stringify(payload),
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}

