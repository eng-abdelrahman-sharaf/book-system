'use client';
import React, { useEffect, useState, useCallback } from "react";
import CartList from "./CartList";
import CartSummary from "./CartSummary";
import { CartBookPrice } from "../../lib/mockCartData";
import { getCartItems, getCartTotal, removeBook, clearCart } from "../../lib/cartApi";
import { useRouter } from "next/navigation";
const CartPage: React.FC = () => {
    const router = useRouter(); // <-- initialize router
    const [cartItems, setCartItems] = useState<CartBookPrice[]>([]);
    const [total, setTotal] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(true);

    const fetchCart = useCallback(async () => {
        try {
            setLoading(true);
            const items = await getCartItems();
            const totalPrice = await getCartTotal();
            setCartItems(items);
            setTotal(totalPrice);
        } catch (err) {
            console.error("Error loading cart:", err);
            alert("Failed to load cart from backend");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchCart();
    }, [fetchCart]);

    const handleRemove = async (isbn: string) => {
        try {
            await removeBook(isbn);
            fetchCart(); // refresh cart
        } catch (err) {
            console.error("Remove book error:", err);
            alert("Failed to remove book");
        }
    };

    const handleClear = async () => {
        try {
            await clearCart();
            fetchCart(); // refresh cart
        } catch (err) {
            console.error("Clear cart error:", err);
            alert("Failed to clear cart");
        }
    };

    const handleCheckout = () => {
        router.push("/checkout");
    };

    if (loading) return <p>Loading cart...</p>;

    return (
        <div className="max-w-2xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Shopping Cart</h1>

            {cartItems.length === 0 ? (
                <p>Your cart is empty.</p>
            ) : (
                <>
                    <CartList items={cartItems} onRemove={handleRemove} />
                    <CartSummary total={total} onClear={handleClear} onCheckout={handleCheckout} />
                </>
            )}
        </div>
    );
};

export default CartPage;
