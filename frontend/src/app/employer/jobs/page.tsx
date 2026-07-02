"use client";

import Link from "next/link";
import { useEffect, useState, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { deleteJob, getEmployerJobs } from "@/lib/api/jobs";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";
import { Job } from "@/types/job";

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

function formatSalaryRange(job: Job): string | null {
  if (!job.salaryMin && !job.salaryMax) {
    return null;
  }

  if (job.salaryMin && job.salaryMax) {
    return `$${job.salaryMin} - $${job.salaryMax}`;
  }

  if (job.salaryMin) {
    return `From $${job.salaryMin}`;
  }

  return `Up to $${job.salaryMax}`;
}

export default function EmployerJobsPage() {
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

  const [jobs, setJobs] = useState<Job[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [deletingJobId, setDeletingJobId] = useState<number | null>(null);

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
    }
  }, [authenticated, router]);

  useEffect(() => {
    if (!authenticated || !storedEmployerId) {
      setIsLoading(false);
      return;
    }

    let isCancelled = false;

    const loadJobs = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const data = await getEmployerJobs(Number(storedEmployerId));

        if (!isCancelled) {
          setJobs(data);
        }
      } catch (err) {
        if (!isCancelled) {
          setError(
            err instanceof Error
              ? err.message
              : "Unable to load employer jobs right now. Please try again.",
          );
        }
      } finally {
        if (!isCancelled) {
          setIsLoading(false);
        }
      }
    };

    void loadJobs();

    return () => {
      isCancelled = true;
    };
  }, [authenticated, storedEmployerId]);

  const handleDelete = async (jobId: number) => {
    setDeletingJobId(jobId);
    setError(null);
    setSuccess(null);

    try {
      await deleteJob(jobId);
      setJobs((currentJobs) => currentJobs.filter((job) => job.id !== jobId));
      setSuccess("Job deleted successfully.");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to delete the job right now. Please try again.",
      );
    } finally {
      setDeletingJobId(null);
    }
  };

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  if (!storedEmployerId) {
    return (
      <div className="mx-auto max-w-4xl space-y-6">
        <header className="space-y-2">
          <h1 className="text-2xl font-semibold">Employer Jobs</h1>
          <p className="text-sm text-gray-600">
            Create an employer profile before managing job postings.
          </p>
        </header>

        <section className="rounded-xl border bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-700">
            No employer ID is stored for this browser yet.
          </p>
          <Link
            href="/employer"
            className="mt-4 inline-flex rounded-md bg-black px-4 py-2 text-sm text-white transition hover:bg-gray-800"
          >
            Create Employer First
          </Link>
        </section>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div className="space-y-2">
          <h1 className="text-2xl font-semibold">Employer Jobs</h1>
          <p className="text-sm text-gray-600">
            Manage job postings for employer ID {storedEmployerId}.
          </p>
        </div>

        <Link
          href="/employer/jobs/new"
          className="inline-flex rounded-md bg-black px-4 py-2 text-sm text-white transition hover:bg-gray-800"
        >
          Create Job Posting
        </Link>
      </header>

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

      {isLoading ? (
        <p className="text-sm text-gray-600">Loading employer jobs...</p>
      ) : jobs.length === 0 ? (
        <section className="rounded-xl border bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-700">No job postings found yet.</p>
        </section>
      ) : (
        <div className="space-y-4">
          {jobs.map((job) => {
            const salaryRange = formatSalaryRange(job);

            return (
              <section key={job.id} className="rounded-xl border bg-white p-6 shadow-sm">
                <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                  <div className="space-y-2">
                    <h2 className="text-lg font-semibold">{job.title}</h2>
                    <p className="text-sm text-gray-700">{job.location}</p>
                    <div className="flex flex-wrap gap-2 text-sm text-gray-600">
                      {job.workType && <span>{job.workType}</span>}
                      {job.seniority && <span>{job.seniority}</span>}
                      {salaryRange && <span>{salaryRange}</span>}
                    </div>
                    {job.skills && job.skills.length > 0 && (
                      <p className="text-sm text-gray-600">
                        Skills: {job.skills.join(", ")}
                      </p>
                    )}
                  </div>

                  <div className="flex gap-3">
                    <Link
                      href={`/employer/jobs/${job.id}/edit`}
                      className="rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:border-black hover:text-black"
                    >
                      Edit
                    </Link>
                    <button
                      type="button"
                      onClick={() => handleDelete(job.id)}
                      className="rounded-md border border-red-300 px-4 py-2 text-sm text-red-700 transition hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-60"
                      disabled={deletingJobId === job.id}
                    >
                      {deletingJobId === job.id ? "Deleting..." : "Delete"}
                    </button>
                  </div>
                </div>
              </section>
            );
          })}
        </div>
      )}
    </div>
  );
}
