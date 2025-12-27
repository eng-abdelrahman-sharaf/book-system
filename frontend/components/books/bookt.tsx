"use client";

import React, { useState } from "react";
import { toast } from "react-hot-toast";
import { Book } from "@/types/book";
import { addBooks } from "@/api/cart/add-books";

interface BookComponentProps {
    book: Book;
    onAddToCart?: (book: Book) => void;
}

const BookComponent: React.FC<BookComponentProps> = ({ book, onAddToCart }) => {
    const [adding, setAdding] = useState(false);

    const handleAddToCart = async () => {
        if (onAddToCart) {
            onAddToCart(book);
            return;
        }

        if (book.numberOfBooks === 0 || adding) return;

        setAdding(true);
        try {
            await addBooks([{ isbn: book.isbn, quantity: 1 }]);
            toast.success(`Added "${book.title}" to cart`);
        } catch (err) {
            console.error("Failed to add to cart", err);
            toast.error("Failed to add to cart");
        } finally {
            setAdding(false);
        }
    };

    return (
        <div className="border rounded-lg shadow-md overflow-hidden max-w-sm min-w-[200px] hover:shadow-lg transition-shadow flex flex-col">
            <div className="relative w-full h-48 bg-gray-300 flex items-center justify-center p-6">
                <h3 className="text-2xl font-bold text-gray-700 text-center line-clamp-3">
                    {book.title}
                </h3>
            </div>

            <div className="p-4 flex flex-col grow justify-between">
                <div className="mb-3">
                    <h2 className="text-xl font-bold text-gray-800">
                        {book.title}
                    </h2>
                    {book.authorName && (
                        <p className="text-sm text-gray-600">
                            by {book.authorName}
                        </p>
                    )}
                </div>
                <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                        <span className="text-gray-600">ISBN:</span>
                        <span className="font-semibold">{book.isbn}</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="text-gray-600">Publisher:</span>
                        <span className="font-semibold">
                            {book.publisherName}
                        </span>
                    </div>
                    <div className="flex justify-between">
                        <span className="text-gray-600">Category:</span>
                        <span className="font-semibold">{book.category}</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="text-gray-600">Publication Year:</span>
                        <span className="font-semibold">
                            {book.publicationYear}
                        </span>
                    </div>
                    <div className="flex justify-between">
                        <span className="text-gray-600">Price:</span>
                        <span className="font-semibold text-green-600">
                            ${book.sellingPrice}
                        </span>
                    </div>
                    <div className="flex justify-between">
                        <span className="text-gray-600">In Stock:</span>
                        <span
                            className={`font-semibold ${
                                book.numberOfBooks > book.threshold
                                    ? "text-green-600"
                                    : "text-red-600"
                            }`}>
                            {book.numberOfBooks}
                        </span>
                    </div>
                    {book.numberOfBooks <= book.threshold && (
                        <div className="text-xs text-orange-600 font-semibold mt-2">
                            Low Stock Alert
                        </div>
                    )}
                </div>

                <button
                    onClick={handleAddToCart}
                    disabled={book.numberOfBooks === 0 || adding}
                    className={`w-full mt-4 py-2 px-4 rounded-lg font-semibold transition-colors ${
                        book.numberOfBooks === 0
                            ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                            : "bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-800"
                    }`}>
                    {book.numberOfBooks === 0
                        ? "Out of Stock"
                        : adding
                        ? "Adding..."
                        : "Add to Cart"}
                </button>
            </div>
        </div>
    );
};

export default BookComponent;
