"use client";

import React from "react";

interface Book {
    isbn: string;
    title: string;
    publisherId: number;
    publisherName: string;
    authorName: string | null;
    publicationYear: number;
    sellingPrice: number;
    category: string;
    numberOfBooks: number;
    threshold: number;
}

interface BookComponentProps {
    book: Book;
    onAddToCart?: (book: Book) => void;
}

const BookComponent: React.FC<BookComponentProps> = ({ book, onAddToCart }) => {
    const handleAddToCart = () => {
        if (onAddToCart) {
            onAddToCart(book);
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
                    disabled={book.numberOfBooks === 0}
                    className={`w-full mt-4 py-2 px-4 rounded-lg font-semibold transition-colors ${
                        book.numberOfBooks === 0
                            ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                            : "bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-800"
                    }`}>
                    {book.numberOfBooks === 0 ? "Out of Stock" : "Add to Cart"}
                </button>
            </div>
        </div>
    );
};

export default BookComponent;
