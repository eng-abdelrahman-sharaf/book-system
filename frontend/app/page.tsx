"use client";

import Link from "next/link";

export default function Home() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-amber-50 to-orange-100 flex items-center justify-center p-4">
            <div className="text-center">
                <h1 className="text-4xl font-bold text-gray-800 mb-4">
                    Welcome to Book System
                </h1>
                <p className="text-gray-600 mb-8">
                    Your personal library management system
                </p>
                <div className="flex gap-4 justify-center">
                    <Link
                        href="/login"
                        className="bg-amber-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-amber-700 transition-colors"
                    >
                        Log In
                    </Link>
                    <Link
                        href="/signup"
                        className="bg-white text-amber-600 px-6 py-3 rounded-lg font-semibold border-2 border-amber-600 hover:bg-amber-50 transition-colors"
                    >
                        Sign Up
                    </Link>
                </div>
            </div>
        </div>
    );
}
