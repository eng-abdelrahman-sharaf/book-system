'use client';
import { apiRequest } from "./api-client";

/* =========================
   Types
========================= */

export interface UserUpdate {
  userId: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  phone: string;
  shippingAddress: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

/* =========================
   API Calls
========================= */

export async function getProfile(): Promise<UserUpdate> {
  const res = await apiRequest("/v1/api/user/profile");

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return res.json();
}

export async function updateProfile(user: UserUpdate): Promise<string> {
  const res = await apiRequest("/v1/api/user/profile", {
    method: "PUT",
    body: JSON.stringify(user),
  });

  const message = await res.text();

  if (!res.ok) {
    throw new Error(message);
  }

  return message || "Profile updated successfully";
}

export async function updatePassword(
  request: ChangePasswordRequest
): Promise<{ success: boolean; message: string }> {
  const res = await apiRequest("/v1/api/user/password", {
    method: "PUT",
    body: JSON.stringify(request),
  });

  const message = await res.text();

  return {
    success: res.ok,
    message: message || (res.ok ? "Password updated successfully" : "Update failed"),
  };
}
