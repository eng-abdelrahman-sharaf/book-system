"use client";

import React, { useEffect, useState } from "react";
import { getAllAuthors } from "@/api/authors/getAll";
import { createAuthor } from "@/api/authors/create";
import { updateAuthor } from "@/api/authors/update";
import { deleteAuthor } from "@/api/authors/delete";
import { Author, CreateAuthorPayload } from "@/types/author";
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
import { toast } from "react-hot-toast";
import { XIcon } from "lucide-react";
import { useRouter } from "next/navigation";
import { ArrowLeft } from "lucide-react";

export default function ManageAuthorsPage() {
    const router = useRouter();
    const [authors, setAuthors] = useState<Author[]>([]);
    const [filteredAuthors, setFilteredAuthors] = useState<Author[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [editing, setEditing] = useState<Author | null>(null);
    const [creating, setCreating] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const modalOpen = editing !== null || creating;

    const [form, setForm] = useState<CreateAuthorPayload>({
        name: "",
    });

    const load = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllAuthors();
            setAuthors(data);
            setFilteredAuthors(data);
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Failed to load authors"
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
            setFilteredAuthors(authors);
        } else {
            const filtered = authors.filter((author) =>
                author.name.toLowerCase().includes(searchTerm.toLowerCase())
            );
            setFilteredAuthors(filtered);
        }
    }, [searchTerm, authors]);

    const startCreate = () => {
        setCreating(true);
        setEditing(null);
        setForm({
            name: "",
        });
    };

    const startEdit = (author: Author) => {
        setEditing(author);
        setCreating(false);
        setForm({
            name: author.name,
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
                await updateAuthor(editing.authorId, form);
                toast.success("Author updated");
            } else {
                await createAuthor(form);
                toast.success("Author created");
            }
            await load();
            setEditing(null);
            setCreating(false);
        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Failed to save author"
            );
        }
    };

    const handleDelete = async (authorId: number, authorName: string) => {
        if (
            !confirm(
                `Are you sure you want to delete "${authorName}"? This action cannot be undone.`
            )
        ) {
            return;
        }
        try {
            await deleteAuthor(authorId);
            toast.success("Author deleted");
            await load();
        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Failed to delete author"
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
                        <h1 className="text-2xl font-bold">Manage Authors</h1>
                    </div>
                    <Button onClick={startCreate}>Add New Author</Button>
                </div>

                <div className="mb-4">
                    <Input
                        type="text"
                        placeholder="Search authors by name..."
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
                                <TableHead>ID</TableHead>
                                <TableHead>Name</TableHead>
                                <TableHead>Actions</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={3}>Loading...</TableCell>
                                </TableRow>
                            ) : authors.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={3}>
                                        No authors found
                                    </TableCell>
                                </TableRow>
                            ) : filteredAuthors.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={3}>
                                        No authors match your search
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredAuthors.map((a) => (
                                    <TableRow key={a.authorId}>
                                        <TableCell>{a.authorId}</TableCell>
                                        <TableCell>{a.name}</TableCell>
                                        <TableCell>
                                            <div className="flex gap-2">
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => startEdit(a)}
                                                >
                                                    Edit
                                                </Button>
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() =>
                                                        handleDelete(
                                                            a.authorId,
                                                            a.name
                                                        )
                                                    }
                                                    className="text-red-600 hover:text-red-700"
                                                >
                                                    Delete
                                                </Button>
                                            </div>
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
                        onClick={closeForm}
                    >
                        <div
                            className="relative w-full max-w-2xl rounded-lg bg-white shadow-2xl"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <button
                                type="button"
                                aria-label="Close"
                                className="absolute right-3 top-3 rounded-full p-1 text-gray-500 transition hover:bg-gray-100 hover:text-gray-700"
                                onClick={closeForm}
                            >
                                <XIcon />
                            </button>
                            <div className="border-b px-6 py-4">
                                <h2 className="text-lg font-semibold">
                                    {editing ? "Edit Author" : "Add Author"}
                                </h2>
                            </div>
                            <div className="px-6 py-4">
                                <form
                                    onSubmit={submit}
                                    className="grid grid-cols-1 gap-4"
                                >
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Name <span className="text-red-500">*</span>
                                        </label>
                                        <Input
                                            value={form.name}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    name: e.target.value,
                                                })
                                            }
                                            required
                                        />
                                    </div>
                                    <div className="flex justify-end gap-2 pt-4">
                                        <Button
                                            type="button"
                                            variant="outline"
                                            onClick={closeForm}
                                        >
                                            Cancel
                                        </Button>
                                        <Button type="submit">
                                            {editing ? "Update" : "Create"}
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

