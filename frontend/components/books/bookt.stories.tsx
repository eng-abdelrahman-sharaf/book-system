import type { Meta, StoryObj } from "@storybook/react";

import BookComponent from "./bookt";

type Book = {
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
};

const meta = {
    title: "Components/BookComponent",
    component: BookComponent,
    parameters: {
        layout: "centered",
        docs: {
            description: {
                component:
                    "A book card component that displays book information including ISBN, title, publisher, category, price, and stock levels.",
            },
        },
    },
    tags: ["autodocs"],
} satisfies Meta<typeof BookComponent>;

export default meta;
type Story = StoryObj<typeof meta>;

// Sample book data
const sampleBook: Book = {
    isbn: "13",
    title: "hi",
    publisherId: 1,
    publisherName: "Pearson Education",
    authorName: null,
    publicationYear: 2018,
    sellingPrice: 45.99,
    category: "Science",
    numberOfBooks: 20,
    threshold: 5,
};

const sampleBookWithAuthor: Book = {
    ...sampleBook,
    title: "Advanced TypeScript",
    authorName: "John Doe",
    sellingPrice: 59.99,
    category: "Programming",
};

const lowStockBook: Book = {
    ...sampleBook,
    title: "Rare Books Collection",
    numberOfBooks: 3,
    sellingPrice: 120.99,
};

// Basic story
export const Default: Story = {
    args: {
        book: sampleBook,
    },
};

// Book with author
export const WithAuthor: Story = {
    args: {
        book: sampleBookWithAuthor,
    },
};

// Low stock warning
export const LowStock: Story = {
    args: {
        book: lowStockBook,
    },
};

// Grid showcase - Multiple books
export const BookGrid: Story = {
    args: {
        book: sampleBook,
    },
    render: () => (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
            <BookComponent book={sampleBook} />
            <BookComponent book={sampleBookWithAuthor} />
            <BookComponent book={lowStockBook} />
        </div>
    ),
    parameters: {
        layout: "fullscreen",
        docs: {
            description: {
                story: "Display multiple books in a responsive grid layout.",
            },
        },
    },
};
