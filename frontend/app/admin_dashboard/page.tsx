"use client";

import AdminDashboard from "@/components/admindashboard";
import LogoutButton from "@/components/LogoutButton";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import { Home } from "lucide-react";

export default function Dashboard()
{
    return (
        <div className="min-h-screen bg-gray-50">
            <div className="flex justify-between items-center p-4">
                <Button asChild variant="outline">
                    <Link href="/home" className="flex items-center gap-2">
                        <Home className="h-4 w-4" />
                        Back to Home
                    </Link>
                </Button>
                <LogoutButton />
            </div>
            <AdminDashboard/>
        </div>
    );
}