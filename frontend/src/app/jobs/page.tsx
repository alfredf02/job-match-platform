"use client";
import Link from "next/link";
import { useEffect, useState } from "react";

import { fetchJobs } from "@/lib/api/jobs";
import { Job } from "@/types/jobs";

export default function JobsPage() {
  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // fetch the current jobs as soon as the page mounts
    const loadJobs = async () => {
      try {
        const data = await fetchJobs();
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
      }
    };

    loadJobs();
  }, []);

  if (loading) {
    return <p>Loading jobs...</p>;
  }

  if (error) {
    return <p className="text-red-600">Error: {error}</p>;
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Jobs</h1>
        <Link className="text-blue-600 underline" href="/jobs/create">
          Create Job
        </Link>
      </div>

      {jobs.length === 0 ? (
        <p>No jobs available.</p>
      ) : (
        <ul className="space-y-3">
          {jobs.map((job) => (
            <li key={job.id} className="rounded border p-3">
              <h2 className="text-lg font-medium">{job.title}</h2>
              <p className="text-sm text-gray-700">{job.location}</p>
              <p className="text-sm text-gray-700">{job.employmentType}</p>
              {(job.salaryMin || job.salaryMax) && (
                <p className="text-sm text-gray-700">
                  Salary: {job.salaryMin ? `$${job.salaryMin}` : ""}
                  {job.salaryMin && job.salaryMax ? " - " : ""}
                  {job.salaryMax ? `$${job.salaryMax}` : ""}
                </p>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}