"use client";

import { BookOpen, Lock, User, Eye, EyeOff, LogOut } from "lucide-react";
import { Input } from "../ui/input";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import React, { useState, useEffect } from "react";
import { LoginFormData, loginSchema } from "@/types/authentication/login";
import { login } from "@/api/authentication/login";
import { logout as logoutApi } from "@/api/authentication/logout";
import toast from "react-hot-toast";
import { setAccessToken, setRefreshToken, hasAccessToken, hasRefreshToken, clearAllTokens, getRefreshToken } from "@/lib/token-storage";

export default function LoginForm() {
    const [showPassword, setShowPassword] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
    });

    // Check authentication status
    useEffect(() => {
        setIsAuthenticated(hasAccessToken() || hasRefreshToken());
    }, []);

    const onSubmit = async (data: LoginFormData) => {
        try {
            const response = await login(data);
            
            // Store access token in localStorage
            setAccessToken(response.accessToken);
            
            // Store refresh token in localStorage (for API client use)
            if (response.refreshToken) {
                setRefreshToken(response.refreshToken);
            }
            
            setIsAuthenticated(true);
            toast.success("Logged in successfully!");
            
            // Redirect to home after successful login
            window.location.href = "/home";
        } catch (error) {
            toast.error(error instanceof Error ? error.message : "Failed to log in");
        }
    };

    const handleLogout = async () => {
        try {
            // Get refresh token before clearing
            const refreshToken = getRefreshToken();
            
            // Clear tokens from storage first
            clearAllTokens();
            setIsAuthenticated(false);
            
            // Call backend to invalidate refresh token
            await logoutApi(refreshToken);
            
            toast.success("Logged out successfully!");
            
            // Redirect to home page
            window.location.href = "/";
        } catch (error) {
            // Even if backend call fails, clear local tokens
            clearAllTokens();
            setIsAuthenticated(false);
            toast.success("Logged out successfully!");
            window.location.href = "/";
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-amber-50 to-orange-100 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">
                {/* Header */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-amber-600 rounded-full mb-4">
                        <BookOpen className="w-8 h-8 text-white" />
                    </div>
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">
                        Welcome Back
                    </h1>
                    <p className="text-gray-600">
                        Sign in to your account to continue
                    </p>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
                    <div>
                        <Input
                            label="Username"
                            icon={User}
                            type="text"
                            placeholder="Enter your username"
                            {...register("username")}
                        />
                        {errors.username && (
                            <p className="text-red-500 text-sm mt-1">
                                {errors.username.message}
                            </p>
                        )}
                    </div>

                    <div>
                        <div className="relative">
                            <Input
                                label="Password"
                                icon={Lock}
                                type={showPassword ? "text" : "password"}
                                placeholder="Enter your password"
                                {...register("password")}
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                            >
                                {showPassword ? (
                                    <EyeOff className="w-5 h-5" />
                                ) : (
                                    <Eye className="w-5 h-5" />
                                )}
                            </button>
                        </div>
                        {errors.password && (
                            <p className="text-red-500 text-sm mt-1">
                                {errors.password.message}
                            </p>
                        )}
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className="w-full bg-amber-600 text-white font-semibold py-3 rounded-lg shadow-lg transition-all duration-200 hover:bg-amber-700 hover:shadow-xl active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isSubmitting ? "Logging in..." : "Log In"}
                    </button>
                </form>

                {/* Logout Button (shown when logged in) */}
                {isAuthenticated && (
                    <div className="mt-4">
                        <button
                            type="button"
                            onClick={handleLogout}
                            className="w-full bg-red-600 text-white font-semibold py-3 rounded-lg shadow-lg transition-all duration-200 hover:bg-red-700 hover:shadow-xl active:scale-95 flex items-center justify-center gap-2"
                        >
                            <LogOut className="w-4 h-4" />
                            Log Out
                        </button>
                    </div>
                )}

                {/* Sign Up Link */}
                <div className="mt-6 text-center">
                    <p className="text-gray-600">
                        Don't have an account?{" "}
                        <Link
                            href="/signup"
                            className="text-amber-600 hover:text-amber-700 font-semibold hover:underline"
                        >
                            Sign Up
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}





