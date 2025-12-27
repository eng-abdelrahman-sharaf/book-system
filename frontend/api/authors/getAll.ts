import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { Author } from "@/types/author";

export async function getAllAuthors(): Promise<Author[]> {
    const res = await apiRequest("/authors", {
        method: "GET",
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
    return res.json();
}

