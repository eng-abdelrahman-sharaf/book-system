"use client";

import React, { useEffect, useState } from "react";
import { getAllPublishers } from "@/api/publishers/getAll";
import { createPublisher } from "@/api/publishers/create";
import { updatePublisher } from "@/api/publishers/update";
import { deletePublisher } from "@/api/publishers/delete";
import { Publisher, CreatePublisherPayload } from "@/types/publisher";
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

export default function ManagePublishersPage() {
    const router = useRouter();
    const [publishers, setPublishers] = useState<Publisher[]>([]);
    const [filteredPublishers, setFilteredPublishers] = useState<Publisher[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [editing, setEditing] = useState<Publisher | null>(null);
    const [creating, setCreating] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const modalOpen = editing !== null || creating;

    const [form, setForm] = useState<CreatePublisherPayload>({
        name: "",
        address: "",
        phone: "",
    });

    const load = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllPublishers();
            setPublishers(data);
            setFilteredPublishers(data);
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Failed to load publishers"
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
            setFilteredPublishers(publishers);
        } else {
            const filtered = publishers.filter(
                (publisher) =>
                    publisher.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                    (publisher.address &&
                        publisher.address.toLowerCase().includes(searchTerm.toLowerCase())) ||
                    (publisher.phone &&
                        publisher.phone.toLowerCase().includes(searchTerm.toLowerCase()))
            );
            setFilteredPublishers(filtered);
        }
    }, [searchTerm, publishers]);

    const startCreate = () => {
        setCreating(true);
        setEditing(null);
        setForm({
            name: "",
            address: "",
            phone: "",
        });
    };

    const startEdit = (publisher: Publisher) => {
        setEditing(publisher);
        setCreating(false);
        setForm({
            name: publisher.name,
            address: publisher.address ?? "",
            phone: publisher.phone ?? "",
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
                await updatePublisher(editing.publisherId, form);
                toast.success("Publisher updated");
            } else {
                await createPublisher(form);
                toast.success("Publisher created");
            }
            await load();
            setEditing(null);
            setCreating(false);
        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Failed to save publisher"
            );
        }
    };

    const handleDelete = async (publisherId: number, publisherName: string) => {
        if (!confirm(`Are you sure you want to delete "${publisherName}"? This action cannot be undone.`)) {
            return;
        }
        try {
            await deletePublisher(publisherId);
            toast.success("Publisher deleted");
            await load();
        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Failed to delete publisher"
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
                        <h1 className="text-2xl font-bold">Manage Publishers</h1>
                    </div>
                    <Button onClick={startCreate}>Add New Publisher</Button>
                </div>

                <div className="mb-4">
                    <Input
                        type="text"
                        placeholder="Search publishers by name, address, or phone..."
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
                                <TableHead>Address</TableHead>
                                <TableHead>Phone</TableHead>
                                <TableHead>Actions</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={5}>Loading...</TableCell>
                                </TableRow>
                            ) : publishers.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5}>No publishers found</TableCell>
                                </TableRow>
                            ) : filteredPublishers.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5}>
                                        No publishers match your search
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredPublishers.map((p) => (
                                    <TableRow key={p.publisherId}>
                                        <TableCell>{p.publisherId}</TableCell>
                                        <TableCell>{p.name}</TableCell>
                                        <TableCell>{p.address ?? "-"}</TableCell>
                                        <TableCell>{p.phone ?? "-"}</TableCell>
                                        <TableCell>
                                            <div className="flex gap-2">
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => startEdit(p)}
                                                >
                                                    Edit
                                                </Button>
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() =>
                                                        handleDelete(p.publisherId, p.name)
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
                                    {editing ? "Edit Publisher" : "Add Publisher"}
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
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Address
                                        </label>
                                        <Input
                                            value={form.address ?? ""}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    address: e.target.value || null,
                                                })
                                            }
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm mb-1">
                                            Phone
                                        </label>
                                        <Input
                                            value={form.phone ?? ""}
                                            onChange={(e) =>
                                                setForm({
                                                    ...form,
                                                    phone: e.target.value || null,
                                                })
                                            }
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

