'use client';
import React, { useState } from "react";
import { useRouter } from "next/navigation";
import toast, { Toaster } from 'react-hot-toast';
import { updatePassword, ChangePasswordRequest } from "../../../lib/updateProfileApi";

const PasswordPage: React.FC = () => {
    const router = useRouter();
    const [userId, setUserId] = useState<number>(13); // should come from token/session

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!currentPassword || !newPassword || !confirmPassword) {
            toast.error("Please fill all fields");
            return;
        }

        if (newPassword !== confirmPassword) {
            toast.error("New password and confirm password do not match");
            return;
        }

        const payload: ChangePasswordRequest = {currentPassword, newPassword };

        try {
            const result = await updatePassword(payload);
            if (result.success) {
                toast.success(result.message);
                router.push("/cart"); // temporary redirect
            } else {
                toast.error(result.message);
            }
        } catch (err: any) {
            toast.error(err.message || "Password update failed");
        }
    };

    return (
        <div className="max-w-xl mx-auto p-4">
            <Toaster position="top-right" />
            <h1 className="text-2xl font-bold mb-4">Change Password</h1>

            <form onSubmit={handleSubmit}>
                <div className="mb-2">
                    <label className="block font-semibold mb-1">Current Password</label>
                    <input
                        type="password"
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        className="w-full border p-2 rounded"
                        required
                    />
                </div>

                <div className="mb-2">
                    <label className="block font-semibold mb-1">New Password</label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        className="w-full border p-2 rounded"
                        required
                    />
                </div>

                <div className="mb-2">
                    <label className="block font-semibold mb-1">Confirm New Password</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        className="w-full border p-2 rounded"
                        required
                    />
                </div>

                <div className="flex gap-2 mt-4">
                    <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
                        Update Password
                    </button>
                    <button type="button" onClick={() => router.push("/cart")} className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600">
                        Return to Cart
                    </button>
                </div>
            </form>
        </div>
    );
};

export default PasswordPage;
