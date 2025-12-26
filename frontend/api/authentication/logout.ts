"use server";

const BACKEND_URL = process.env.BACKEND_URL || process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

export const logout = async (refreshToken: string | null): Promise<void> => {
    if (!refreshToken) {
        return;
    }

    try {
        await fetch(`${BACKEND_URL}/v1/api/auth/logout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify({ refreshToken }),
        });
    } catch (error) {
        // Silently fail - logout should work even if backend call fails
    }
};
