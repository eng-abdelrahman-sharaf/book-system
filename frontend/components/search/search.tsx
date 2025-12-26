"use client";

import React, { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { BookSearchParams } from "../../types/book";
import { Sliders } from "lucide-react";

const categories = [
    "Science",
    "Art",
    "Religion",
    "History",
    "Geography",
] as const;

export default function SearchForm() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const [showAdvanced, setShowAdvanced] = useState(false);

    const [formValues, setFormValues] = useState<BookSearchParams>({
        title: "",
        author: "",
        isbn: "",
        category: "",
        publisher: "",
    });

    // Initialize form values from URL params
    useEffect(() => {
        setFormValues({
            title: searchParams.get("title") || "",
            author: searchParams.get("author") || "",
            isbn: searchParams.get("isbn") || "",
            category: searchParams.get("category") || "",
            publisher: searchParams.get("publisher") || "",
        });
    }, [searchParams]);

    const handleInputChange = (
        field: keyof BookSearchParams,
        value: string
    ) => {
        setFormValues((prev) => ({
            ...prev,
            [field]: value,
        }));
    };

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();

        // Build URL query params
        const params = new URLSearchParams();

        if (formValues.title?.trim()) {
            params.set("title", formValues.title.trim());
        }
        if (formValues.author?.trim()) {
            params.set("author", formValues.author.trim());
        }
        if (formValues.isbn?.trim()) {
            params.set("isbn", formValues.isbn.trim());
        }
        if (formValues.category?.trim()) {
            params.set("category", formValues.category.trim());
        }
        if (formValues.publisher?.trim()) {
            params.set("publisher", formValues.publisher.trim());
        }

        // Update URL with search params
        router.push(`/search?${params.toString()}`);
    };

    const handleClear = () => {
        setFormValues({
            title: "",
            author: "",
            isbn: "",
            category: "",
            publisher: "",
        });
        setShowAdvanced(false);
        router.push("/search");
    };

    return (
        <form
            onSubmit={handleSearch}
            className="w-full max-w-4xl mx-auto p-6">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">
                Search Books
            </h2>

            {/* Simple Search Bar */}
            <div className="flex gap-2 mb-6 items-center">
                <Input
                    type="text"
                    value={formValues.title}
                    onChange={(e) => handleInputChange("title", e.target.value)}
                    placeholder="Search by book title..."
                    containerClassName="grow"
                />
                <button
                    type="button"
                    onClick={() => setShowAdvanced(!showAdvanced)}
                    className="p-2 border border-gray-300 rounded-md hover:bg-gray-100 transition-colors"
                    title="Show advanced filters">
                    <Sliders className="text-gray-600" />
                </button>
                <Button type="submit">Search</Button>
            </div>

            {/* Advanced Filters */}
            {showAdvanced && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6 pb-6 border-t pt-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Author
                        </label>
                        <Input
                            type="text"
                            value={formValues.author}
                            onChange={(e) =>
                                handleInputChange("author", e.target.value)
                            }
                            placeholder="Search by author name..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            ISBN
                        </label>
                        <Input
                            type="text"
                            value={formValues.isbn}
                            onChange={(e) =>
                                handleInputChange("isbn", e.target.value)
                            }
                            placeholder="Search by ISBN..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Publisher
                        </label>
                        <Input
                            type="text"
                            value={formValues.publisher}
                            onChange={(e) =>
                                handleInputChange("publisher", e.target.value)
                            }
                            placeholder="Search by publisher name..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Category
                        </label>
                        <select
                            value={formValues.category}
                            onChange={(e) =>
                                handleInputChange("category", e.target.value)
                            }
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                            <option value="">All Categories</option>
                            {categories.map((category) => (
                                <option key={category} value={category}>
                                    {category}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="md:col-span-2 flex gap-2">
                        <Button
                            type="button"
                            onClick={handleClear}
                            variant="outline">
                            Clear All
                        </Button>
                    </div>
                </div>
            )}
        </form>
    );
}
