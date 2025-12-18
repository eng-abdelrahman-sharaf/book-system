"use client";
import { BookOpen, Mail, Phone, User } from "lucide-react";
import { Input } from "../ui/input";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import Link from "next/link";

const signupSchema = z.object({
    firstName: z
        .string()
        .min(1, "First name is required")
        .min(2, "First name must be at least 2 characters"),
    lastName: z
        .string()
        .min(1, "Last name is required")
        .min(2, "Last name must be at least 2 characters"),
    email: z.email("Invalid email address"),
    phone: z
        .string()
        .min(1, "Phone number is required")
        .regex(
            /^[+]?[(]?[0-9]{1,4}[)]?[-\s.]?[(]?[0-9]{1,4}[)]?[-\s.]?[0-9]{1,9}$/,
            "Invalid phone number format"
        ),
});

type FormData = z.infer<typeof signupSchema>;

export default function SignupForm() {
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<FormData>({
        resolver: zodResolver(signupSchema),
    });

    const onSubmit = (data: FormData) => {
        console.log("Form submitted:", data);
        alert("Account created successfully!");
    };

    return (
        <div className="min-h-screen bg-linear-to-br from-amber-50 to-orange-100 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">
                {/* Header */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-amber-600 rounded-full mb-4">
                        <BookOpen className="w-8 h-8 text-white" />
                    </div>
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">
                        Join Our Library
                    </h1>
                    <p className="text-gray-600">
                        Create your account to start exploring
                    </p>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
                    <div>
                        <Input
                            label="First Name"
                            icon={User}
                            type="text"
                            id="firstName"
                            placeholder="John"
                            {...register("firstName")}
                        />
                        {errors.firstName && (
                            <p className="text-red-500 text-sm mt-1">
                                {errors.firstName.message}
                            </p>
                        )}
                    </div>

                    <div>
                        <Input
                            label="Last Name"
                            icon={User}
                            type="text"
                            id="lastName"
                            placeholder="Doe"
                            {...register("lastName")}
                        />
                        {errors.lastName && (
                            <p className="text-red-500 text-sm mt-1">
                                {errors.lastName.message}
                            </p>
                        )}
                    </div>

                    <div>
                        <Input
                            label="Email Address"
                            icon={Mail}
                            type="text"
                            id="email"
                            placeholder="john.doe@example.com"
                            {...register("email")}
                        />
                        {errors.email && (
                            <p className="text-red-500 text-sm mt-1">
                                {errors.email.message}
                            </p>
                        )}
                    </div>

                    <div>
                        <Input
                            label="Phone Number"
                            icon={Phone}
                            type="tel"
                            id="phone"
                            placeholder="+1 (555) 000-0000"
                            {...register("phone")}
                        />
                        {errors.phone && (
                            <p className="text-red-500 text-sm mt-1">
                                {errors.phone.message}
                            </p>
                        )}
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        className="w-full bg-amber-600 text-white font-semibold py-3 rounded-lg shadow-lg transition-all duration-200 hover:bg-amber-700 hover:shadow-xl active:scale-95">
                        Sign Up
                    </button>
                </form>

                {/* Login Link */}
                <div className="mt-6 text-center">
                    <p className="text-gray-600">
                        Already have an account?{" "}
                        <Link
                            href="#"
                            className="text-amber-600 hover:text-amber-700 font-semibold hover:underline"
                            onClick={(e) => {
                                e.preventDefault();
                                alert("Redirecting to login page...");
                            }}>
                            Log In
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}
