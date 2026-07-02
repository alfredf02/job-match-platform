"use client";

import { FormEvent, useEffect, useState, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { createEmployer } from "@/lib/api/jobs";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";

const EMPLOYER_STORAGE_KEY = "job-match-platform.employerId";
const EMPLOYER_CHANGE_EVENT = "job-match-platform:employer-change";

function isBrowser(): boolean {
  return typeof window !== "undefined" && typeof window.localStorage !== "undefined";
}

function subscribeToEmployerId(listener: () => void): () => void {
  if (!isBrowser()) {
    return () => undefined;
  }

  const handleChange = () => {
    listener();
  };

  window.addEventListener(EMPLOYER_CHANGE_EVENT, handleChange);
  window.addEventListener("storage", handleChange);

  return () => {
    window.removeEventListener(EMPLOYER_CHANGE_EVENT, handleChange);
    window.removeEventListener("storage", handleChange);
  };
}

function getEmployerIdSnapshot(): string | null {
  if (!isBrowser()) {
    return null;
  }

  return window.localStorage.getItem(EMPLOYER_STORAGE_KEY);
}

function getEmployerIdServerSnapshot(): string | null {
  return null;
}

function saveEmployerId(employerId: number): void {
  if (!isBrowser()) {
    return;
  }

  window.localStorage.setItem(EMPLOYER_STORAGE_KEY, String(employerId));
  window.dispatchEvent(new Event(EMPLOYER_CHANGE_EVENT));
}

export default function EmployerPage() {
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const storedEmployerId = useSyncExternalStore(
    subscribeToEmployerId,
    getEmployerIdSnapshot,
    getEmployerIdServerSnapshot,
  );
  const authenticated = Boolean(session?.token);

  const [name, setName] = useState("");
  const [website, setWebsite] = useState("");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
    }
  }, [authenticated, router]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setSuccess(null);

    try {
      const employer = await createEmployer({
        name,
        website: website || undefined,
        description: description || undefined,
        location: location || undefined,
      });

      saveEmployerId(employer.id);
      setSuccess(`Employer created successfully. Saved employer ID ${employer.id} for this browser.`);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to create employer right now. Please try again.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold">Employer Setup</h1>
        <p className="text-sm text-gray-600">
          Create your company profile once, then reuse the saved employer ID when posting jobs.
        </p>
      </header>

      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <h2 className="text-lg font-semibold">Current Employer</h2>
        <p className="mt-2 text-sm text-gray-600">
          {storedEmployerId
            ? `Stored employer ID for this browser: ${storedEmployerId}`
            : "No employer ID is currently stored for this browser."}
        </p>
      </section>

      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <div className="space-y-1">
          <h2 className="text-lg font-semibold">Create Employer</h2>
          <p className="text-sm text-gray-600">
            Add the company details that match the job-service employer record.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="name">
              Company Name
            </label>
            <input
              id="name"
              name="name"
              type="text"
              required
              value={name}
              onChange={(event) => setName(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Acme Labs"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="website">
              Website
            </label>
            <input
              id="website"
              name="website"
              type="url"
              value={website}
              onChange={(event) => setWebsite(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="https://example.com"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="location">
              Location
            </label>
            <input
              id="location"
              name="location"
              type="text"
              value={location}
              onChange={(event) => setLocation(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Sydney, Australia"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="description">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              rows={4}
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Short overview of the company"
            />
          </div>

          {error && (
            <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {error}
            </p>
          )}

          {success && (
            <p className="rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">
              {success}
            </p>
          )}

          <button
            type="submit"
            className="rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
            disabled={submitting}
          >
            {submitting ? "Creating employer..." : "Create Employer"}
          </button>
        </form>
      </section>
    </div>
  );
}
