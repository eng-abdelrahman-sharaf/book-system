const ACCESS_TOKEN_KEY = "accessToken";

export function setAccessToken(token: string): void {
    if (typeof window !== "undefined") {
        localStorage.setItem(ACCESS_TOKEN_KEY, token);
    }
}

export function getAccessToken(): string | null {
    if (typeof window !== "undefined") {
        return localStorage.getItem(ACCESS_TOKEN_KEY);
    }
    return null;
}

export function removeAccessToken(): void {
    if (typeof window !== "undefined") {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
    }
}

export function hasAccessToken(): boolean {
    return getAccessToken() !== null;
}

export function getRefreshToken(): string | null {
    if (typeof window !== "undefined") {
        return localStorage.getItem("refreshToken");
    }
    return null;
}

export function setRefreshToken(token: string): void {
    if (typeof window !== "undefined") {
        localStorage.setItem("refreshToken", token);
    }
}

export function removeRefreshToken(): void {
    if (typeof window !== "undefined") {
        localStorage.removeItem("refreshToken");
    }
}

export function hasRefreshToken(): boolean {
    if (typeof window !== "undefined") {
        // Only check localStorage - HttpOnly cookies cannot be checked via JavaScript
        return localStorage.getItem("refreshToken") !== null;
    }
    return false;
}

export function clearAllTokens(): void {
    removeAccessToken();
    removeRefreshToken();
    // HttpOnly cookies are cleared by backend on logout
}





