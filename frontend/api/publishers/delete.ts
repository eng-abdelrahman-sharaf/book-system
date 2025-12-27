import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";

export async function deletePublisher(publisherId: number): Promise<void> {
    const res = await apiRequest(`/publishers/${publisherId}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}

