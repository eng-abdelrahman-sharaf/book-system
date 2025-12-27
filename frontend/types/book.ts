// Book type definition matching the backend entity
export interface Book {
    isbn: string;
    title: string;
    publisherId: number;
    publisherName: string;
    authorName: string | null;
    publicationYear: number | null;
    sellingPrice: number;
    category: "Science" | "Art" | "Religion" | "History" | "Geography";
    numberOfBooks: number;
    threshold: number;
}

// Search parameters
export interface BookSearchParams {
    title?: string;
    author?: string;
    isbn?: string;
    category?: string;
    publisher?: string;
}
