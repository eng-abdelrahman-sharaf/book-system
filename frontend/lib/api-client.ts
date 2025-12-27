import { getAccessToken, removeAccessToken, getRefreshToken, removeRefreshToken } from "./token-storage";
import { responseErrorToString } from "./error";

const BACKEND_URL =
    process.env.BACKEND_URL ||
    process.env.NEXT_PUBLIC_BACKEND_URL ||
    "http://localhost:8080/v1/api";

// Shared refresh promise to prevent concurrent refresh attempts
let refreshPromise: Promise<boolean> | null = null;

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
                const retryResponse = await fetch(url, {
                    ...options,
                    headers: retryHeaders,
                    credentials: "include",
                });
                if (retryResponse.ok || retryResponse.status !== 401) {
                    return retryResponse;
                }
            }
        }
        removeAccessToken();
        removeRefreshToken();
    }

    return response;
}

async function refreshAccessToken(): Promise<boolean> {
    // If a refresh is already in progress, wait for it instead of starting a new one
    if (refreshPromise) {
        return refreshPromise;
    }

    // Start a new refresh attempt
    refreshPromise = (async (): Promise<boolean> => {
        try {
            // Get refresh token using helper function
            const refreshToken = getRefreshToken();
            
            if (!refreshToken) {
                console.log("No refresh token found");
                return false;
            }
            
            const response = await fetch(`${BACKEND_URL}/auth/refresh`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include", // Include refresh token cookie
                body: JSON.stringify({ refreshToken }),
            });

            if (response.ok) {
                const data = await response.json();
                if (data.accessToken) {
                    const { setAccessToken, setRefreshToken } = await import("./token-storage");
                    setAccessToken(data.accessToken);
                    
                    if (data.refreshToken) {
                        setRefreshToken(data.refreshToken);
                    }
                    console.log("Token refreshed successfully");
                    return true;
                } else {
                    console.log("Refresh response missing accessToken");
                }
            } else {
                const errorText = await response.text();
                console.log("Refresh failed:", response.status, errorText);
            }
            return false;
        } catch (error) {
            console.error("Error refreshing token:", error);
            return false;
        } finally {
            // Clear the promise so future requests can attempt refresh again
            refreshPromise = null;
        }
    })();

    return refreshPromise;
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





