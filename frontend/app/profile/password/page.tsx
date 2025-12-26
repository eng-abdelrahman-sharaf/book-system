'use client';
import React, { useState } from "react";
import { changePassword } from "../../../lib/updateProfileApi";

const ChangePasswordPage: React.FC = () => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");

        try {
            const msg = await changePassword({ oldPassword, newPassword });
            setMessage(msg);
            setOldPassword("");
            setNewPassword("");
        } catch (err: any) {
            setMessage(err?.message || "Password update failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-md mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Change Password</h1>

            <form onSubmit={handleSubmit} className="space-y-3">
                <div>
                    <label className="block font-semibold mb-1">
                        Current Password
                    </label>
                    <input
                        type="password"
                        value={oldPassword}
                        onChange={e => setOldPassword(e.target.value)}
                        className="w-full border p-2 rounded"
                        required
                    />
                </div>

                <div>
                    <label className="block font-semibold mb-1">
                        New Password
                    </label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={e => setNewPassword(e.target.value)}
                        className="w-full border p-2 rounded"
                        required
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className="bg-red-600 text-white px-4 py-2 rounded"
                >
                    {loading ? "Updating..." : "Change Password"}
                </button>

                {message && (
                    <p className="mt-2 text-sm text-center">{message}</p>
                )}
            </form>
        </div>
    );
};

export default ChangePasswordPage;
