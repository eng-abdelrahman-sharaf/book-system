import { apiRequest } from "@/lib/api-client";
import { responseErrorToString } from "@/lib/error";
import { Publisher } from "@/types/publisher";

export async function getAllPublishers(): Promise<Publisher[]> {
    const res = await apiRequest("/publishers", {
        method: "GET",
    });
    if (!res.ok) {
        throw new Error(await responseErrorToString(res));
    }
    return res.json();
}

