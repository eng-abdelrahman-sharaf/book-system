import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { CreatePublisherPayload } from "@/types/publisher";

export async function createPublisher(payload: CreatePublisherPayload): Promise<void> {
    const res = await apiRequest("/publishers", {
        method: "POST",
        body: JSON.stringify(payload),
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
}

