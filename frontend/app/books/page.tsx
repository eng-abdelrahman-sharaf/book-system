"use client";

import React, { useEffect, useState } from "react";
import { getAllBooks } from "@/lib/bookApi";
import { Book } from "@/types/book";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { createBook, CreateBookPayload } from "@/api/books/create";
import { updateBook } from "@/api/books/update";
import { toast } from "react-hot-toast";
import { XIcon } from "lucide-react";
import { useRouter } from "next/navigation";
import { ArrowLeft } from "lucide-react";

const categories = [
    "Science",
    "Art",
    "Religion",
    "History",
    "Geography",
] as const;

export default function ManageBooksPage() {
    const router = useRouter();
    const [books, setBooks] = useState<Book[]>([]);
    const [filteredBooks, setFilteredBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [editing, setEditing] = useState<Book | null>(null);
    const [creating, setCreating] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const modalOpen = editing !== null || creating;

    const [form, setForm] = useState<CreateBookPayload>({
        isbn: "",
        title: "",
        publisherId: 1,
        publicationYear: undefined,
        sellingPrice: 0,
        category: "Science",
        numberOfBooks: undefined,
        threshold: undefined,
        authorName: null,
    });

    const load = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllBooks();
            setBooks(data);
            setFilteredBooks(data);
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Failed to load books"
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    useEffect(() => {
        if (searchTerm.trim() === "") {
            setFilteredBooks(books);
        } else {
            const filtered = books.filter(book =>
                book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                book.isbn.toLowerCase().includes(searchTerm.toLowerCase()) ||
                (book.authorName && book.authorName.toLowerCase().includes(searchTerm.toLowerCase())) ||
                book.category.toLowerCase().includes(searchTerm.toLowerCase())
            );
            setFilteredBooks(filtered);
        }
    }, [searchTerm, books]);

    const startCreate = () => {
        setCreating(true);
        setEditing(null);
        setForm({
            isbn: "",
            title: "",
            publisherId: 1,
            publicationYear: undefined,
            sellingPrice: 0,
            category: "Science",
            numberOfBooks: undefined,
            threshold: undefined,
            authorName: null,
        });
    };

    const startEdit = (book: Book) => {
        setEditing(book);
        setCreating(false);
        setForm({
            isbn: book.isbn,
            title: book.title,
            publisherId: book.publisherId,
            publicationYear: book.publicationYear ?? undefined,
            sellingPrice: book.sellingPrice,
            category: book.category as CreateBookPayload["category"],
            numberOfBooks: book.numberOfBooks ?? undefined,
            threshold: book.threshold ?? undefined,
            authorName: book.authorName,
        });
    };

    const closeForm = () => {
        setEditing(null);
        setCreating(false);
    };

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (editing) {
                await updateBook(editing.isbn, form);
                toast.success("Book updated");
            } else {
                await createBook(form);
                toast.success("Book created");
            }
            await load();
            setEditing(null);
            setCreating(false);
        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Failed to save book"
            );
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-7xl mx-auto">
                <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center gap-4">
                        <Button
                            variant="outline"
                            onClick={() => router.push("/admin_dashboard")}
                            className="flex items-center gap-2"
                        >
                            <ArrowLeft className="h-4 w-4" />
                            Back to Dashboard
                        </Button>
                        <h1 className="text-2xl font-bold">Manage Books</h1>
                    </div>
                    <Button onClick={startCreate}>Add New Book</Button>
                </div>

                <div className="mb-4">
                    <Input
                        type="text"
                        placeholder="Search books by title, ISBN, author, or category..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="max-w-md"
                    />
                </div>

                {error && (
                    <div className="p-3 bg-red-100 text-red-700 border border-red-300 rounded">
                        {error}
                    </div>
                )}

                <div className="overflow-hidden rounded-md border bg-white">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>ISBN</TableHead>
                                <TableHead>Title</TableHead>
                                <TableHead>Publisher</TableHead>
                                <TableHead>Year</TableHead>
                                <TableHead>Price</TableHead>
                                <TableHead>Category</TableHead>
                                <TableHead>Stock</TableHead>
                                <TableHead>Actions</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={8}>
                                        Loading...
                                    </TableCell>
                                </TableRow>
                            ) : books.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={8}>
                                        No books found
                                    </TableCell>
                                </TableRow>
                            ) : filteredBooks.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={8}>
                                        No books match your search
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredBooks.map((b) => (
                                    <TableRow key={b.isbn}>
                                        <TableCell>{b.isbn}</TableCell>
                                        <TableCell>{b.title}</TableCell>
                                        <TableCell>{b.publisherName}</TableCell>
                                        <TableCell>
                                            {b.publicationYear ?? "-"}
                                        </TableCell>
                                        <TableCell>${b.sellingPrice}</TableCell>
                                        <TableCell>{b.category}</TableCell>
                                        <TableCell>{b.numberOfBooks}</TableCell>
                                        <TableCell>
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() => startEdit(b)}>
                                                Edit
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </div>

                {modalOpen && (
                    <div
                        className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4"
                        onClick={closeForm}>
                        <div
                            className="relative w-full max-w-3xl rounded-lg bg-white shadow-2xl"
                            onClick={(e) => e.stopPropagation()}>
                            <button
                                type="button"
                                aria-label="Close"
                                className="absolute right-3 top-3 rounded-full p-1 text-gray-500 transition hover:bg-gray-100 hover:text-gray-700"
                                onClick={closeForm}>
                                <XIcon />
                            </button>
                            <div className="border-b px-6 py-4">
                                <h2 className="text-lg font-semibold">
                                    {editing ? "Edit Book" : "Add Book"}
                                </h2>
                            </div>
                            <div className="px-6 py-4">
                                <form
                                    onSubmit={submit}
                                    className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm mb-1">
                                            ISBN
                                        </label>
                                        <Input
                                            value={form.isbn}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    isbn: e.target.value,
                                                })
                                            }
                                            disabled={!!editing}
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Title
                                        </label>
                                        <Input
                                            value={form.title}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    title: e.target.value,
                                                })
                                            }
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Author
                                        </label>
                                        <Input
                                            value={form.authorName ?? ""}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    authorName:
                                                        e.target.value === ""
                                                            ? null
                                                            : e.target.value,
                                                })
                                            }
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Publisher ID
                                        </label>
                                        <Input
                                            type="number"
                                            value={form.publisherId}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    publisherId: Number(
                                                        e.target.value || 0
                                                    ),
                                                })
                                            }
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Publication Date
                                        </label>
                                        <Input
                                            type="date"
                                            value={
                                                form.publicationYear
                                                    ? `${form.publicationYear}-01-01`
                                                    : ""
                                            }
                                            onChange={(e) => {
                                                const val = e.target.value;
                                                setForm({
                                                    ...form,
                                                    publicationYear:
                                                        val === ""
                                                            ? undefined
                                                            : new Date(
                                                                  val
                                                              ).getFullYear(),
                                                });
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Selling Price
                                        </label>
                                        <Input
                                            type="number"
                                            step="0.01"
                                            value={form.sellingPrice}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    sellingPrice: Number(
                                                        e.target.value
                                                    ),
                                                })
                                            }
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Category
                                        </label>
                                        <select
                                            className="w-full border rounded px-3 py-2"
                                            value={form.category}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    category: e.target
                                                        .value as CreateBookPayload["category"],
                                                })
                                            }>
                                            {categories.map((c) => (
                                                <option key={c} value={c}>
                                                    {c}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Number of Books
                                        </label>
                                        <Input
                                            type="number"
                                            value={form.numberOfBooks ?? ""}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    numberOfBooks:
                                                        e.target.value === ""
                                                            ? undefined
                                                            : Number(
                                                                  e.target.value
                                                              ),
                                                })
                                            }
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Threshold
                                        </label>
                                        <Input
                                            type="number"
                                            value={form.threshold ?? ""}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    threshold:
                                                        e.target.value === ""
                                                            ? undefined
                                                            : Number(
                                                                  e.target.value
                                                              ),
                                                })
                                            }
                                        />
                                    </div>
                                    <div className="md:col-span-2 flex gap-2 justify-end mt-2">
                                        <Button type="submit">
                                            {editing
                                                ? "Save Changes"
                                                : "Create Book"}
                                        </Button>
                                        <Button
                                            type="button"
                                            variant="outline"
                                            onClick={closeForm}>
                                            Cancel
                                        </Button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
