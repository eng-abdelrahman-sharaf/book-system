'use client';
import React, { useState, useEffect } from "react";
import { UserUpdate, updateProfile, getProfile } from "../../lib/updateProfileApi";
import { useRouter } from "next/navigation";
import LogoutButton from "@/components/LogoutButton";

const ProfilePage: React.FC = () => {
  const router = useRouter();
  const [form, setForm] = useState<UserUpdate>({
    userId: 0,
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    phone: "",
    shippingAddress: ""
  });
  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  // Fetch user profile on mount
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await getProfile();
        setForm(data);
      } catch (err) {
        console.error("Failed to fetch profile:", err);
        setMessage("Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setMessage("");
    setSuccess(false);

    try {
      const result = await updateProfile(form);

      if (result.toLowerCase().includes("success")) {
        setSuccess(true);
        setMessage(result);

        setTimeout(() => {
          router.push("/home");
        }, 2000);
      } else {
        setMessage(result);
      }
    } catch (err: any) {
      setMessage(err.message || "Update failed");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <p>Loading profile...</p>;

  return (
    <div className="max-w-xl mx-auto p-4">
      <div className="flex justify-end mb-4">
        <LogoutButton />
      </div>
      <h1 className="text-2xl font-bold mb-4">Update Profile</h1>

      {message && (
        <p
          className={`mb-4 p-2 rounded ${
            success ? "bg-green-200 text-green-800" : "bg-red-200 text-red-800"
          }`}
        >
          {message}
        </p>
      )}

      <form onSubmit={handleSubmit} className="space-y-3">
        <div>
          <label className="block font-semibold">First Name</label>
          <input
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            className="w-full border p-2 rounded"
            required
          />
        </div>

        <div>
          <label className="block font-semibold">Last Name</label>
          <input
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            className="w-full border p-2 rounded"
            required
          />
        </div>

        <div>
          <label className="block font-semibold">Username</label>
          <input
            name="username"
            value={form.username}
            onChange={handleChange}
            className="w-full border p-2 rounded"
            required
          />
        </div>

        <div>
          <label className="block font-semibold">Email</label>
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            className="w-full border p-2 rounded"
            required
          />
        </div>

        <div>
          <label className="block font-semibold">Phone</label>
          <input
            name="phone"
            value={form.phone}
            onChange={handleChange}
            className="w-full border p-2 rounded"
          />
        </div>

        <div>
          <label className="block font-semibold">Shipping Address</label>
          <input
            name="shippingAddress"
            value={form.shippingAddress}
            onChange={handleChange}
            className="w-full border p-2 rounded"
          />
        </div>

        <div className="flex gap-2 mt-4">
          <button
            type="submit"
            disabled={submitting}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            {submitting ? "Updating..." : "Update Profile"}
          </button>

          <button
            type="button"
            onClick={() => router.push("/profile/password")}
            className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
          >
            Change Password
          </button>

          <button
            type="button"
            onClick={() => router.push("/home")}
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          >
            Back to Home
          </button>

          <button
            type="button"
            onClick={() => router.push("/cart")}
            className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
          >
            Go to Cart
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProfilePage;
