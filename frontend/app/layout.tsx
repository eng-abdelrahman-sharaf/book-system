import type { Metadata } from "next";
import "./globals.css";
import { Toaster } from "react-hot-toast";

export const metadata: Metadata = {
    title: "Book Store App",
    description:
        "Discover and manage your favorite books. Browse, search, and organize your personal library with our intuitive book management system.",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en">
            <body className="antialiased">
                {children}
                <Toaster
                    position="bottom-left"
                    reverseOrder={false}
                    gutter={8}
                    containerClassName=""
                    containerStyle={{}}
                    toasterId="default"
                    toastOptions={{
                        // Define default options
                        className: "",
                        duration: 5000,
                        removeDelay: 1000,
                        style: {
                            background: "#363636",
                            color: "#fff",
                        },

                        // Default options for specific types
                        success: {
                            duration: 3000,
                            iconTheme: {
                                primary: "green",
                                secondary: "black",
                            },
                        },
                    }}
                />
            </body>
        </html>
    );
}
