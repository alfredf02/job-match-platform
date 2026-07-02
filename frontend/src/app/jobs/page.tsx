"use client";

import { FormEvent, useEffect, useState } from "react";

import { searchJobs } from "@/lib/api/jobs";
import { Job, Seniority, WorkType } from "@/types/job";

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

export default function JobsPage() {
  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [location, setLocation] = useState("");
  const [workType, setWorkType] = useState<WorkType | "">("");
  const [seniority, setSeniority] = useState<Seniority | "">("");

  const loadJobs = async (filters?: {
    location?: string;
    workType?: WorkType;
    seniority?: Seniority;
  }) => {
    try {
      const data = await searchJobs(filters);
      setJobs(data);
    } catch (err) {
      console.error("Failed to fetch jobs", err);
      setError(
        err instanceof Error
          ? err.message
          : "Unable to load jobs right now. Please try again.",
      );
    } finally {
      setLoading(false);
      setSubmitting(false);
    }
  };

  useEffect(() => {
    void loadJobs();
  }, []);

  const handleSearch = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    await loadJobs({
      location: location || undefined,
      workType: workType || undefined,
      seniority: seniority || undefined,
    });
  };

  const handleReset = async () => {
    setLocation("");
    setWorkType("");
    setSeniority("");
    setSubmitting(true);
    setError(null);

    await loadJobs();
  };

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold">Jobs</h1>
        <p className="text-sm text-gray-600">
          Browse available roles using the supported public search filters.
        </p>
      </header>

      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <form onSubmit={handleSearch} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-3">
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
              <label className="block text-sm font-medium" htmlFor="workType">
                Work Type
              </label>
              <select
                id="workType"
                name="workType"
                value={workType}
                onChange={(event) => setWorkType(event.target.value as WorkType | "")}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              >
                <option value="">Any</option>
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
                onChange={(event) => setSeniority(event.target.value as Seniority | "")}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              >
                <option value="">Any</option>
                <option value="INTERN">INTERN</option>
                <option value="JUNIOR">JUNIOR</option>
                <option value="MID">MID</option>
                <option value="SENIOR">SENIOR</option>
                <option value="LEAD">LEAD</option>
              </select>
            </div>
          </div>

          <div className="flex flex-wrap gap-3">
            <button
              type="submit"
              className="rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
              disabled={submitting}
            >
              {submitting ? "Searching..." : "Search Jobs"}
            </button>

            <button
              type="button"
              onClick={() => void handleReset()}
              className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 transition hover:border-black hover:text-black disabled:cursor-not-allowed disabled:opacity-60"
              disabled={submitting}
            >
              Reset Filters
            </button>
          </div>
        </form>
      </section>

      {loading && (
        <section className="rounded-xl border bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-600">Loading jobs...</p>
        </section>
      )}

      {error && (
        <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
          {error}
        </p>
      )}

      {!loading && !error && jobs.length === 0 ? (
        <section className="rounded-xl border bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-700">No jobs found for the current filters.</p>
        </section>
      ) : null}

      {!loading && !error && jobs.length > 0 ? (
        <ul className="space-y-4">
          {jobs.map((job) => (
            <li key={job.id} className="rounded-xl border bg-white p-6 shadow-sm">
              <div className="space-y-2">
                <h2 className="text-lg font-semibold">{job.title}</h2>
                {(job.company || job.companyName || job.employer?.name) && (
                  <p className="text-sm text-gray-700">
                    {job.company ?? job.companyName ?? job.employer?.name}
                  </p>
                )}
                <p className="text-sm text-gray-700">{job.location}</p>

                <div className="flex flex-wrap gap-2 text-sm text-gray-600">
                  {job.workType && <span>{job.workType}</span>}
                  {job.seniority && <span>{job.seniority}</span>}
                  {formatSalaryRange(job) && <span>{formatSalaryRange(job)}</span>}
                </div>

                {job.skills && job.skills.length > 0 && (
                  <p className="text-sm text-gray-600">
                    Skills: {job.skills.join(", ")}
                  </p>
                )}
              </div>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  );
}
