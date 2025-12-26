import { getAccessToken, removeAccessToken } from "./token-storage";
import { responseErrorToString } from "./error";

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

export async function apiRequest(
    endpoint: string,
    options: RequestInit = {}
): Promise<Response> {
    const accessToken = getAccessToken();
    
    const headers: Record<string, string> = {
        "Content-Type": "application/json",
        ...(options.headers as Record<string, string> | undefined),
    };

    // Add Authorization header if access token exists
    if (accessToken) {
        headers["Authorization"] = `Bearer ${accessToken}`;
    }

    const url = endpoint.startsWith("http")
        ? endpoint
        : `${BACKEND_URL}${endpoint}`;

    const response = await fetch(url, {
        ...options,
        headers,
        credentials: "include", // Include cookies (for refresh token)
    });

    // Handle 401 Unauthorized - token might be expired
    if (response.status === 401) {
        // Try to refresh the token
        const refreshed = await refreshAccessToken();
        if (refreshed) {
            // Retry the original request with new token
            const newAccessToken = getAccessToken();
            if (newAccessToken) {
                const retryHeaders: Record<string, string> = {
                    "Content-Type": "application/json",
                    ...(options.headers as Record<string, string> | undefined),
                    "Authorization": `Bearer ${newAccessToken}`,
                };
                return fetch(url, {
                    ...options,
                    headers: retryHeaders,
                    credentials: "include",
                });
            }
        }
        // If refresh failed, clear tokens
        removeAccessToken();
    }

    return response;
}

async function refreshAccessToken(): Promise<boolean> {
    try {
        // Try to get refresh token from localStorage as fallback
        const refreshToken = localStorage.getItem("refreshToken");
        
        const response = await fetch(`${BACKEND_URL}/v1/api/auth/refresh`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include", // Include refresh token cookie
            body: JSON.stringify(
                refreshToken ? { refreshToken } : {}
            ),
        });

        if (response.ok) {
            const data = await response.json();
            if (data.accessToken) {
                const { setAccessToken } = await import("./token-storage");
                setAccessToken(data.accessToken);
                
                // Also store refresh token in localStorage if provided (for display purposes)
                if (data.refreshToken) {
                    localStorage.setItem("refreshToken", data.refreshToken);
                }
                return true;
            }
        }
        return false;
    } catch (error) {
        return false;
    }
}

export async function unauthenticatedRequest(
    endpoint: string,
    options: RequestInit = {}
): Promise<Response> {
    const url = endpoint.startsWith("http")
        ? endpoint
        : `${BACKEND_URL}${endpoint}`;

    return fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers,
        },
        credentials: "include", // Include cookies (for refresh token)
    });
}





