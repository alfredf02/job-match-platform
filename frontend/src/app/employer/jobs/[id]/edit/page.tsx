"use client";

import Link from "next/link";
import { FormEvent, useEffect, useState, useSyncExternalStore } from "react";
import { useParams, useRouter } from "next/navigation";
import { getJob, updateJob } from "@/lib/api/jobs";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";
import { Seniority, WorkType } from "@/types/job";

function toSkillsText(skills: string[] | undefined): string {
  return skills?.join(", ") ?? "";
}

function parseSkills(value: string): string[] {
  return value
    .split(",")
    .map((skill) => skill.trim())
    .filter(Boolean);
}

function formatSalaryValue(value: number | undefined): string {
  return value !== undefined ? String(value) : "";
}

export default function EditEmployerJobPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const authenticated = Boolean(session?.token);
  const jobId = Number(params.id);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [salaryMin, setSalaryMin] = useState("");
  const [salaryMax, setSalaryMax] = useState("");
  const [workType, setWorkType] = useState<WorkType>("REMOTE");
  const [seniority, setSeniority] = useState<Seniority>("MID");
  const [skills, setSkills] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
      return;
    }

    if (!Number.isFinite(jobId)) {
      setError("Invalid job ID.");
      setIsLoading(false);
      return;
    }

    let isCancelled = false;

    const loadJob = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const job = await getJob(jobId);

        if (isCancelled) {
          return;
        }

        setTitle(job.title);
        setDescription(job.description);
        setLocation(job.location);
        setSalaryMin(formatSalaryValue(job.salaryMin));
        setSalaryMax(formatSalaryValue(job.salaryMax));
        setWorkType(job.workType ?? "REMOTE");
        setSeniority(job.seniority ?? "MID");
        setSkills(toSkillsText(job.skills));
      } catch (err) {
        if (!isCancelled) {
          setError(
            err instanceof Error
              ? err.message
              : "Unable to load the job right now. Please try again.",
          );
        }
      } finally {
        if (!isCancelled) {
          setIsLoading(false);
        }
      }
    };

    void loadJob();

    return () => {
      isCancelled = true;
    };
  }, [authenticated, jobId, router]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const updatedJob = await updateJob(jobId, {
        title,
        description,
        location,
        salaryMin: salaryMin ? Number(salaryMin) : undefined,
        salaryMax: salaryMax ? Number(salaryMax) : undefined,
        workType,
        seniority,
        skills: parseSkills(skills),
      });

      setTitle(updatedJob.title);
      setDescription(updatedJob.description);
      setLocation(updatedJob.location);
      setSalaryMin(formatSalaryValue(updatedJob.salaryMin));
      setSalaryMax(formatSalaryValue(updatedJob.salaryMax));
      setWorkType(updatedJob.workType ?? workType);
      setSeniority(updatedJob.seniority ?? seniority);
      setSkills(toSkillsText(updatedJob.skills));
      setSuccess("Job updated successfully.");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to update the job right now. Please try again.",
      );
    } finally {
      setIsSaving(false);
    }
  };

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  if (isLoading) {
    return <p className="text-sm text-gray-600">Loading job details...</p>;
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div className="space-y-2">
          <h1 className="text-2xl font-semibold">Edit Job Posting</h1>
          <p className="text-sm text-gray-600">
            Update the role details and save changes when you are ready.
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
            disabled={isSaving}
          >
            {isSaving ? "Saving changes..." : "Save Changes"}
          </button>
        </form>
      </section>
    </div>
  );
}
