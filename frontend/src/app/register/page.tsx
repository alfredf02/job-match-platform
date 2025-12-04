"use client";

import Link from "next/link";

export default function RegisterPage() {
  return (
    <div className="max-w-md space-y-6">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold">Register</h1>
        <p className="text-sm text-gray-600">
          Create an account to manage your job matches.
        </p>
      </header>

      <div className="space-y-3 text-sm text-gray-700">
        <p>
          Registration is not implemented yet. Please contact your administrator
          to create an account.
        </p>
        <p>
          Already registered? <Link href="/login">Login</Link> to continue.
        </p>
      </div>
    </div>
  );
}