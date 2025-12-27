import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { UpdatePublisherPayload } from "@/types/publisher";

export async function updatePublisher(
    publisherId: number,
    payload: UpdatePublisherPayload
): Promise<void> {
    const res = await apiRequest(`/publishers/${publisherId}`, {
        method: "PUT",
        body: JSON.stringify(payload),
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}

