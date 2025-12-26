'use client';
import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import { submitCheckout, getSavedCard } from "../../lib/checkoutApi";

const CheckoutPage: React.FC = () => {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [savedCard, setSavedCard] = useState<string>("");
    const [useSavedCard, setUseSavedCard] = useState(true);

    const [cardNumber, setCardNumber] = useState("");
    const [expirationDate, setExpirationDate] = useState("");
    const [billingAddress, setBillingAddress] = useState("");

    useEffect(() => {
        const fetchSavedCard = async () => {
            try {
                const card = await getSavedCard();
                if (card) {
                    setSavedCard(card);
                    setCardNumber(card);
                } else {
                    setUseSavedCard(false);
                }
            } catch (err) {
                console.error("Error fetching saved card:", err);
                setUseSavedCard(false);
            } finally {
                setLoading(false);
            }
        };

        fetchSavedCard();
    }, []);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!useSavedCard && (!cardNumber || !expirationDate || !billingAddress)) {
            alert("Please fill all required fields");
            return;
        }

        const payload = useSavedCard
            ? {} // backend will use stored card
            : { cardNumber, expirationDate, billingAddress };

        try {
            const result = await submitCheckout(payload);
            alert(result.message);
            router.push("/cart");
        } catch (err: any) {
            alert(err.message || "Checkout failed");
        }
    };

    if (loading) return <p>Loading checkout...</p>;

    return (
        <div className="max-w-xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Checkout</h1>

            {savedCard && (
                <div className="mb-4">
                    <label>
                        <input
                            type="checkbox"
                            checked={useSavedCard}
                            onChange={() => setUseSavedCard(!useSavedCard)}
                        /> Use saved card ending with {savedCard.slice(-4)}
                    </label>
                </div>
            )}

            <form onSubmit={handleSubmit}>
                {!useSavedCard && (
                    <>
                        <div className="mb-2">
                            <label className="block font-semibold mb-1">Card Number</label>
                            <input
                                type="text"
                                value={cardNumber}
                                onChange={(e) => setCardNumber(e.target.value)}
                                className="w-full border p-2 rounded"
                                placeholder="1234123412341234"
                                required
                            />
                        </div>

                        <div className="mb-2">
                            <label className="block font-semibold mb-1">Expiration Date</label>
                            <input
                                type="date"
                                value={expirationDate}
                                onChange={(e) => setExpirationDate(e.target.value)}
                                className="w-full border p-2 rounded"
                                required
                            />
                        </div>

                        <div className="mb-2">
                            <label className="block font-semibold mb-1">Billing Address</label>
                            <input
                                type="text"
                                value={billingAddress}
                                onChange={(e) => setBillingAddress(e.target.value)}
                                className="w-full border p-2 rounded"
                                placeholder="Your billing address"
                                required
                            />
                        </div>
                    </>
                )}

                <div className="flex gap-2 mt-4">
                    <button
                        type="submit"
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                    >
                        Place Order
                    </button>

                    <button
                        type="button"
                        className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
                        onClick={() => router.push("/cart")}
                    >
                        Back to Cart
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CheckoutPage;
