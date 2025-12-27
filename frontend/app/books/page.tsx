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
import { getAllAuthors } from "@/api/authors/getAll";
import { Author } from "@/types/author";

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

    const [authors, setAuthors] = useState<Author[]>([]);
    const [form, setForm] = useState<CreateBookPayload>({
        isbn: "",
        title: "",
        publisherId: 1,
        publicationYear: undefined,
        sellingPrice: 0,
        category: "Science",
        numberOfBooks: undefined,
        threshold: undefined,
        authorIds: [],
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
        const loadAuthors = async () => {
            try {
                const data = await getAllAuthors();
                setAuthors(data);
            } catch (err) {
                console.error("Failed to load authors:", err);
            }
        };
        loadAuthors();
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
            authorIds: [],
        });
    };

    const startEdit = async (book: Book) => {
        setEditing(book);
        setCreating(false);
        
        // Ensure authors are loaded
        if (authors.length === 0) {
            try {
                const loadedAuthors = await getAllAuthors();
                setAuthors(loadedAuthors);
                // Parse authorName to get author IDs (if authorName exists, try to match with authors)
                let authorIds: number[] = [];
                if (book.authorName) {
                    const authorNames = book.authorName.split(",").map(name => name.trim());
                    authorIds = loadedAuthors
                        .filter(author => authorNames.includes(author.name))
                        .map(author => author.authorId);
                }
                setForm({
                    isbn: book.isbn,
                    title: book.title,
                    publisherId: book.publisherId,
                    publicationYear: book.publicationYear ?? undefined,
                    sellingPrice: book.sellingPrice,
                    category: book.category as CreateBookPayload["category"],
                    numberOfBooks: book.numberOfBooks ?? undefined,
                    threshold: book.threshold ?? undefined,
                    authorIds: authorIds,
                });
            } catch (err) {
                console.error("Failed to load authors:", err);
                // Set form without author IDs if loading fails
                setForm({
                    isbn: book.isbn,
                    title: book.title,
                    publisherId: book.publisherId,
                    publicationYear: book.publicationYear ?? undefined,
                    sellingPrice: book.sellingPrice,
                    category: book.category as CreateBookPayload["category"],
                    numberOfBooks: book.numberOfBooks ?? undefined,
                    threshold: book.threshold ?? undefined,
                    authorIds: [],
                });
            }
        } else {
            // Parse authorName to get author IDs (if authorName exists, try to match with authors)
            let authorIds: number[] = [];
            if (book.authorName) {
                const authorNames = book.authorName.split(",").map(name => name.trim());
                authorIds = authors
                    .filter(author => authorNames.includes(author.name))
                    .map(author => author.authorId);
            }
            setForm({
                isbn: book.isbn,
                title: book.title,
                publisherId: book.publisherId,
                publicationYear: book.publicationYear ?? undefined,
                sellingPrice: book.sellingPrice,
                category: book.category as CreateBookPayload["category"],
                numberOfBooks: book.numberOfBooks ?? undefined,
                threshold: book.threshold ?? undefined,
                authorIds: authorIds,
            });
        }
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
                                            Authors
                                        </label>
                                        <div className="border rounded-md p-2 max-h-48 overflow-y-auto">
                                            {authors.length === 0 ? (
                                                <p className="text-sm text-gray-500">Loading authors...</p>
                                            ) : (
                                                authors.map((author) => (
                                                    <label
                                                        key={author.authorId}
                                                        className="flex items-center space-x-2 py-1 cursor-pointer hover:bg-gray-50 rounded px-2"
                                                    >
                                                        <input
                                                            type="checkbox"
                                                            checked={form.authorIds?.includes(author.authorId) ?? false}
                                                            onChange={(e) => {
                                                                const currentIds = form.authorIds ?? [];
                                                                if (e.target.checked) {
                                                                    setForm({
                                                                        ...form,
                                                                        authorIds: [...currentIds, author.authorId],
                                                                    });
                                                                } else {
                                                                    setForm({
                                                                        ...form,
                                                                        authorIds: currentIds.filter(
                                                                            (id) => id !== author.authorId
                                                                        ),
                                                                    });
                                                                }
                                                            }}
                                                            className="rounded"
                                                        />
                                                        <span className="text-sm">{author.name}</span>
                                                    </label>
                                                ))
                                            )}
                                        </div>
                                        {form.authorIds && form.authorIds.length > 0 && (
                                            <p className="text-xs text-gray-500 mt-1">
                                                {form.authorIds.length} author(s) selected
                                            </p>
                                        )}
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
