'use client';
import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getPastOrders, OrderDetails } from "../../lib/orderApi";

const OrdersPage: React.FC = () => {
    const router = useRouter();
    const [orders, setOrders] = useState<OrderDetails[]>([]);
    const [loading, setLoading] = useState(true);
    const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null);

    useEffect(() => {
        const loadOrders = async () => {
            const data = await getPastOrders();
            setOrders(data);
            setLoading(false);
        };

        loadOrders();
    }, []);

    if (loading) {
        return <p className="p-4">Loading orders...</p>;
    }

    return (
        <div className="max-w-4xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">My Orders</h1>

            {orders.length === 0 ? (
                <p>You have no past orders.</p>
            ) : (
                orders.map(order => (
                    <div
                        key={order.orderId}
                        className="border rounded-lg p-4 mb-4"
                    >
                        {/* Order Summary */}
                        <div className="flex justify-between items-center">
                            <div>
                                <p className="font-semibold">
                                    Order #{order.orderId}
                                </p>
                                <p className="text-sm text-gray-600">
                                    {new Date(order.orderDate).toLocaleString()}
                                </p>
                                <p className="mt-1">
                                    Total:{" "}
                                    <strong>
                                        ${order.totalAmount.toFixed(2)}
                                    </strong>
                                </p>
                            </div>

                            <button
                                className="text-blue-600 underline"
                                onClick={() =>
                                    setExpandedOrderId(
                                        expandedOrderId === order.orderId
                                            ? null
                                            : order.orderId
                                    )
                                }
                            >
                                {expandedOrderId === order.orderId
                                    ? "Hide Details"
                                    : "View Details"}
                            </button>
                        </div>

                        {/* Order Items */}
                        {expandedOrderId === order.orderId && (
                            <div className="mt-4">
                                <h3 className="font-semibold mb-2">
                                    Books in this order
                                </h3>

                                <table className="w-full border text-sm">
                                    <thead className="bg-gray-100">
                                        <tr>
                                            <th className="border p-2 text-left">
                                                Title
                                            </th>
                                            <th className="border p-2 text-left">
                                                ISBN
                                            </th>
                                            <th className="border p-2">
                                                Quantity
                                            </th>
                                            <th className="border p-2">
                                                Price
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {order.items.map((item, index) => (
                                            <tr key={index}>
                                                <td className="border p-2">
                                                    {item.title}
                                                </td>
                                                <td className="border p-2">
                                                    {item.isbn}
                                                </td>
                                                <td className="border p-2 text-center">
                                                    {item.quantity}
                                                </td>
                                                <td className="border p-2">
                                                    ${item.price.toFixed(2)}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                ))
            )}

            <button
                onClick={() => router.push("/cart")}
                className="mt-4 bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700"
            >
                Back to Cart
            </button>
        </div>
    );
};

export default OrdersPage;
