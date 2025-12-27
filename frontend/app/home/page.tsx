"use client";

import React, { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "next/navigation";
import Link from "next/link";
import SearchForm from "@/components/search/search";
import BookComponent from "@/components/books/bookt";
import { Book, BookSearchParams } from "@/types/book";
import { searchBooksServer } from "@/lib/bookApi";
import LogoutButton from "@/components/LogoutButton";
import { Button } from "@/components/ui/button";

export default function Home() {
    const searchParams = useSearchParams();
    const [books, setBooks] = useState<Book[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    // Memoize params from URL to avoid unnecessary fetches
    const params = useMemo<BookSearchParams>(() => {
        return {
            title: searchParams.get("title") || undefined,
            author: searchParams.get("author") || undefined,
            isbn: searchParams.get("isbn") || undefined,
            category: searchParams.get("category") || undefined,
            publisher: searchParams.get("publisher") || undefined,
        };
    }, [searchParams]);

    useEffect(() => {
        let isCancelled = false;
        const fetchBooks = async () => {
            setLoading(true);
            setError(null);
            try {
                const results = await searchBooksServer(params);
                if (!isCancelled) {
                    setBooks(results);
                }
            } catch (err) {
                if (!isCancelled) {
                    setError(
                        err instanceof Error
                            ? err.message
                            : "Failed to search books"
                    );
                    setBooks([]);
                }
            } finally {
                if (!isCancelled) {
                    setLoading(false);
                }
            }
        };

        fetchBooks();

        return () => {
            isCancelled = true;
        };
    }, [params]);

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="flex justify-end p-4 gap-4">
                <Button asChild>
                    <Link href="/profile">Profile</Link>
                </Button>
                <Button asChild>
                    <Link href="/admin_dashboard">Admin Dashboard</Link>
                </Button>
                <Button asChild>
                    <Link href="/cart">Cart</Link>
                </Button>
                <Button asChild>
                    <Link href="/order">Past Orders</Link>
                </Button>
                <LogoutButton />
            </div>
            <div className="max-w-7xl mx-auto">
                <SearchForm />

                {error && (
                    <div className="mt-8 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                        <p className="font-semibold">Error:</p>
                        <p>{error}</p>
                    </div>
                )}

                <div className="mt-8">
                    <div className="mb-4 text-gray-700">
                        <p className="text-lg font-semibold">
                            {loading
                                ? "Loading..."
                                : books.length === 0
                                ? "No books found"
                                : `Found ${books.length} book${
                                      books.length === 1 ? "" : "s"
                                  }`}
                        </p>
                    </div>

                    {loading && (
                        <div className="flex justify-center items-center gap-3 text-gray-600">
                            <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
                            <span>Searching...</span>
                        </div>
                    )}

                    {!loading && books.length > 0 && (
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {books.map((book) => (
                                <BookComponent key={book.isbn} book={book} />
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
