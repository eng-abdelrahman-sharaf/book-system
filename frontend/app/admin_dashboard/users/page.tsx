"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAllUsers, makeUserAdmin, dismissAdmin } from "@/api/reports/reports";
import { getAccessToken } from "@/lib/token-storage";
import { Button } from "@/components/ui/button";
import { Users, ArrowLeft } from "lucide-react";
import { redirect } from "next/navigation";

const UsersPage: React.FC = () => {
    const router = useRouter();
    const [users, setUsers] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>("");
    const token = getAccessToken() || "";

    useEffect(() => {
        const loadUsers = async () => {
            try {
                setLoading(true);
                const data = await getAllUsers(token);
                setUsers(Array.isArray(data) ? data : []);
            } catch (err: any) {
                if (err.message === "FORBIDDEN") {
                    setError("You do not have permission to view users.");
                } else if (err.message === "UNAUTHORIZED") {
                    redirect("/login");
                } else {
                    setError("Failed to load users.");
                }
            } finally {
                setLoading(false);
            }
        };

        loadUsers();
    }, [token]);

    const handleMakeAdmin = async (userId: number) => {
        try {
            console.log("check",userId)
            await makeUserAdmin(userId, token);
     const data = await getAllUsers(token);
            setUsers(Array.isArray(data) ? data : []);
        } catch (err: any) {
            if (err.message === "FORBIDDEN") {
                setError("You do not have permission to modify user roles.");
            } else if (err.message === "UNAUTHORIZED") {
                redirect("/login");
            } else {
                setError("Failed to update user role.");
            }
        }
    };

    const handleDismissAdmin = async (userId: number) => {
        try {
            await dismissAdmin(userId, token);
            // Refresh the users list
            const data = await getAllUsers(token);
            setUsers(Array.isArray(data) ? data : []);
        } catch (err: any) {
            if (err.message === "FORBIDDEN") {
                setError("You do not have permission to modify user roles.");
            } else if (err.message === "UNAUTHORIZED") {
                redirect("/login");
            } else {
                setError("Failed to update user role.");
            }
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 p-6">
                <div className="max-w-4xl mx-auto">
                    <p className="text-center p-4">Loading users...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-4xl mx-auto">
                <div className="flex justify-between items-center mb-6">
                    <div className="flex items-center gap-3">
                        <Users className="h-6 w-6" />
                        <h1 className="text-3xl font-bold">Users Management</h1>
                    </div>
                    <Button
                        variant="outline"
                        onClick={() => router.push("/admin_dashboard")}
                        className="flex items-center gap-2"
                    >
                        <ArrowLeft className="h-4 w-4" />
                        Back to Dashboard
                    </Button>
                </div>

                {error && (
                    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                        {error}
                    </div>
                )}

                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    <div className="px-6 py-4 border-b border-gray-200">
                        <h2 className="text-xl font-semibold text-gray-800">All Users</h2>
                        <p className="text-sm text-gray-600">Total users: {users.length}</p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="min-w-full">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Name
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Role
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Actions
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {users.length > 0 ? (
                                    users.map((user: any, index: number) => (
                                        <tr key={user.id || index} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                {user.firstName && user.lastName
                                                    ? `${user.firstName} ${user.lastName}`
                                                    : user.username || user.email || 'N/A'}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                                                    user.role === 'Admin'
                                                        ? 'bg-red-100 text-red-800'
                                                        : user.role === 'Manager'
                                                        ? 'bg-blue-100 text-blue-800'
                                                        : 'bg-green-100 text-green-800'
                                                }`}>
                                                    {user.role || 'Customer'}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                                {user.role === 'Admin' ? (
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => handleDismissAdmin(user.userId)}
                                                        className="text-red-600 hover:text-red-800 hover:bg-red-50"
                                                    >
                                                        Dismiss Admin
                                                    </Button>
                                                ) : (
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => handleMakeAdmin(user.userId)}
                                                        className="text-blue-600 hover:text-blue-800 hover:bg-blue-50"
                                                    >
                                                        Make Admin
                                                    </Button>
                                                )}
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={3} className="px-6 py-4 text-center text-gray-500">
                                            No users found
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UsersPage;