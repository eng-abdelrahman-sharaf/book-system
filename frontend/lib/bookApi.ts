import { Book, BookSearchParams } from "../types/book";
import { apiRequest } from "./api-client";
import { responseErrorToString } from "./error";
import { getAccessToken } from "./token-storage";

const BACKEND_URL = "http://localhost:8080/api/v1";

export async function searchBooksServer(
    params: BookSearchParams
): Promise<Book[]> {
    // Build query string from params
    const queryParams = new URLSearchParams();

    if (params.title) {
        queryParams.append("title", params.title);
    }
    if (params.author) {
        queryParams.append("author", params.author);
    }
    if (params.isbn) {
        queryParams.append("isbn", params.isbn);
    }
    if (params.category) {
        queryParams.append("category", params.category);
    }
    if (params.publisher) {
        queryParams.append("publisher", params.publisher);
    }

    const queryString = queryParams.toString();
    const url = `/books${queryString ? `?${queryString}` : ""}`;
    
const response = await apiRequest(url, {
        method: "GET",
    });

    if (!response.ok) {
        throw new Error(await responseErrorToString(response));
    }

    return response.json();
}
