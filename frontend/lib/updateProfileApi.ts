'use server';

const BASE_URL =
    process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080/v1/api";

const ACCESS_TOKEN =
    "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2huIiwidXNlcklkIjoxMSwicm9sZSI6IkN1c3RvbWVyIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTc2Njc2NDM3NywiZXhwIjoxNzY2NzY3OTc3fQ.BX5_HXqn1IBjv6iAsnvBJ_iGFjiviriLHG0GOfGfWQewaYxLKD7YMmvRbKSK1LG1snu2SjS9u3gfTyTrDI6hcA";

/* =========================
   Types
========================= */

export interface UserUpdate {
    userId: number;
    firstName: string;
    username: string;
    lastName: string;
    email: string;
    phone: string;
    shippingAddress: string;
}

export interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
}

/* =========================
   API Calls
========================= */
export async function getProfile(): Promise<UserUpdate> {
  const res = await fetch(`${BASE_URL}/user/profile`, {
    method: "GET",
    headers: { "Authorization": ACCESS_TOKEN },
    mode: "cors",
  });
  const text = await res.text();
  return text ? JSON.parse(text) : {} as UserUpdate;
}
export async function updateProfile(user: UserUpdate): Promise<string> {
    try {
        const res = await fetch(`${BASE_URL}/user/profile`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": ACCESS_TOKEN
            },
            body: JSON.stringify(user),
            mode: "cors"
        });

        const text = await res.text();
        // return backend message even if status is 400/403/500
        return text || "No response from server";

    } catch (err) {
        console.error("Update profile error:", err);
        return "Network error or backend unreachable";
    }
}

export async function changePassword(
    data: ChangePasswordRequest
): Promise<string> {
    try {
        const res = await fetch(`${BASE_URL}/user/password`, {
            method: "PUT",
            headers: {
                "Authorization": ACCESS_TOKEN,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data),
            mode: "cors"
        });

        const text = await res.text();
        return text || "Password updated";
    } catch (err) {
        console.error("Change password error:", err);
        throw err;
    }
}
