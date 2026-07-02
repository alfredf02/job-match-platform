"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { register } from "@/lib/api/auth";

export default function RegisterPage() {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setIsSuccess(false);
    setLoading(true);

    try {
      await register({
        fullName: fullName || undefined,
        email,
        password,
      });
      setIsSuccess(true);
      setPassword("");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to register at the moment. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-md rounded-xl border bg-white p-6 shadow-sm">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold">Register</h1>
        <p className="text-sm text-gray-600">
          Create an account to manage your job matches.
        </p>
      </header>

      {isSuccess ? (
        <div className="mt-6 space-y-4">
          <p className="rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">
            Registration successful. You can now sign in with your new account.
          </p>
          <Link
            href="/login"
            className="inline-flex rounded-md bg-black px-4 py-2 text-sm text-white transition hover:bg-gray-800"
          >
            Go to Login
          </Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="fullName">
              Full Name
            </label>
            <input
              id="fullName"
              name="fullName"
              type="text"
              value={fullName}
              onChange={(event) => setFullName(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Jane Doe"
              autoComplete="name"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="email">
              Email
            </label>
            <input
              id="email"
              name="email"
              type="email"
              required
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="you@example.com"
              autoComplete="email"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              name="password"
              type="password"
              required
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Create a password"
              autoComplete="new-password"
            />
          </div>

          {error && (
            <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {error}
            </p>
          )}

          <button
            type="submit"
            className="w-full rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
            disabled={loading}
          >
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>
      )}

      <p className="mt-6 text-sm text-gray-600">
        Already registered? <Link href="/login">Login</Link> to continue.
      </p>
    </div>
  );
}
