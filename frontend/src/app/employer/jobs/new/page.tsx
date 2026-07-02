"use client";

import Link from "next/link";
import { FormEvent, useEffect, useState, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { createJob } from "@/lib/api/jobs";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";
import {
  getEmployerIdServerSnapshot,
  getEmployerIdSnapshot,
  subscribeToEmployerId,
} from "@/lib/employer/storage";
import { Seniority, WorkType } from "@/types/job";

function parseSkills(value: string): string[] {
  return value
    .split(",")
    .map((skill) => skill.trim())
    .filter(Boolean);
}

export default function NewEmployerJobPage() {
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

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [salaryMin, setSalaryMin] = useState("");
  const [salaryMax, setSalaryMax] = useState("");
  const [workType, setWorkType] = useState<WorkType>("REMOTE");
  const [seniority, setSeniority] = useState<Seniority>("MID");
  const [skills, setSkills] = useState("");
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

    if (!storedEmployerId) {
      setError("Create an employer first before posting a job.");
      return;
    }

    setSubmitting(true);
    setError(null);
    setSuccess(null);

    try {
      const job = await createJob({
        employerId: Number(storedEmployerId),
        title,
        description,
        location,
        salaryMin: salaryMin ? Number(salaryMin) : undefined,
        salaryMax: salaryMax ? Number(salaryMax) : undefined,
        workType,
        seniority,
        skills: parseSkills(skills),
      });

      setSuccess(`Job "${job.title}" created successfully.`);
      setTitle("");
      setDescription("");
      setLocation("");
      setSalaryMin("");
      setSalaryMax("");
      setWorkType("REMOTE");
      setSeniority("MID");
      setSkills("");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to create the job posting right now. Please try again.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  if (!storedEmployerId) {
    return (
      <div className="mx-auto max-w-3xl space-y-6">
        <header className="space-y-2">
          <h1 className="text-2xl font-semibold">New Job Posting</h1>
          <p className="text-sm text-gray-600">
            You need an employer profile before you can publish jobs.
          </p>
        </header>

        <section className="rounded-xl border bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-700">
            No employer ID is stored for this browser yet.
          </p>
          <div className="mt-4 flex flex-wrap gap-3">
            <Link
              href="/employer"
              className="inline-flex rounded-md bg-black px-4 py-2 text-sm text-white transition hover:bg-gray-800"
            >
              Create Employer First
            </Link>
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div className="space-y-2">
          <h1 className="text-2xl font-semibold">New Job Posting</h1>
          <p className="text-sm text-gray-600">
            Create a job posting for employer ID {storedEmployerId}.
          </p>
        </div>
        <Link
          href="/employer/jobs"
          className="inline-flex rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:border-black hover:text-black"
        >
          Back to Employer Jobs
        </Link>
      </header>

      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="title">
              Title
            </label>
            <input
              id="title"
              name="title"
              type="text"
              required
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Senior Frontend Engineer"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="description">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              rows={5}
              required
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Describe the responsibilities and expectations for the role."
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
              required
              value={location}
              onChange={(event) => setLocation(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Sydney, Australia"
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="salaryMin">
                Salary Min
              </label>
              <input
                id="salaryMin"
                name="salaryMin"
                type="number"
                value={salaryMin}
                onChange={(event) => setSalaryMin(event.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
                placeholder="90000"
              />
            </div>

            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="salaryMax">
                Salary Max
              </label>
              <input
                id="salaryMax"
                name="salaryMax"
                type="number"
                value={salaryMax}
                onChange={(event) => setSalaryMax(event.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
                placeholder="140000"
              />
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="workType">
                Work Type
              </label>
              <select
                id="workType"
                name="workType"
                value={workType}
                onChange={(event) => setWorkType(event.target.value as WorkType)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              >
                <option value="REMOTE">REMOTE</option>
                <option value="HYBRID">HYBRID</option>
                <option value="ONSITE">ONSITE</option>
              </select>
            </div>

            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="seniority">
                Seniority
              </label>
              <select
                id="seniority"
                name="seniority"
                value={seniority}
                onChange={(event) => setSeniority(event.target.value as Seniority)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              >
                <option value="INTERN">INTERN</option>
                <option value="JUNIOR">JUNIOR</option>
                <option value="MID">MID</option>
                <option value="SENIOR">SENIOR</option>
                <option value="LEAD">LEAD</option>
              </select>
            </div>
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="skills">
              Skills
            </label>
            <input
              id="skills"
              name="skills"
              type="text"
              required
              value={skills}
              onChange={(event) => setSkills(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="React, TypeScript, Next.js"
            />
          </div>

          {error && (
            <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {error}
            </p>
          )}

          {success && (
            <div className="rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">
              <p>{success}</p>
              <Link href="/employer/jobs" className="mt-2 inline-flex underline">
                Go to Employer Jobs List
              </Link>
            </div>
          )}

          <button
            type="submit"
            className="rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
            disabled={submitting}
          >
            {submitting ? "Creating job..." : "Create Job Posting"}
          </button>
        </form>
      </section>
    </div>
  );
}
