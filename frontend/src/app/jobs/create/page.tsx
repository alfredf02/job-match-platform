"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";

import { createJob } from "@/lib/api/jobs";
import { Seniority, WorkType } from "@/types/job";

function parseSkills(value: string): string[] {
  return value
    .split(",")
    .map((skill) => skill.trim())
    .filter(Boolean);
}

export default function CreateJobPage() {
  const router = useRouter();
  // keep track of each field so inputs stay controlled
  const [employerId, setEmployerId] = useState("1");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [workType, setWorkType] = useState<WorkType>("REMOTE");
  const [seniority, setSeniority] = useState<Seniority>("MID");
  const [skills, setSkills] = useState("");
  const [salaryMin, setSalaryMin] = useState<string>("");
  const [salaryMax, setSalaryMax] = useState<string>("");
  // flags for the UI state
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      // send the form data to the job-service
      await createJob({
        employerId: Number(employerId),
        title,
        description,
        location,
        workType,
        seniority,
        skills: parseSkills(skills),
        salaryMin: salaryMin ? Number(salaryMin) : undefined,
        salaryMax: salaryMax ? Number(salaryMax) : undefined,
      });

      // navigate back to the jobs list after a successful creation
      router.push("/jobs");
    } catch (err) {
      console.error("Failed to create job", err);
      setError(
        err instanceof Error
          ? err.message
          : "Unable to create job right now. Please try again.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold">Create Job</h1>
      <form className="space-y-4" onSubmit={handleSubmit}>
        <div className="space-y-2">
          <label className="block text-sm font-medium" htmlFor="employerId">
            Employer ID
          </label>
          <input
            className="w-full rounded border px-3 py-2"
            id="employerId"
            name="employerId"
            required
            min="1"
            value={employerId}
            onChange={(event) => setEmployerId(event.target.value)}
            type="number"
            placeholder="Employer ID"
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium" htmlFor="title">
            Title
          </label>
          <input
            className="w-full rounded border px-3 py-2"
            id="title"
            name="title"
            required
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            type="text"
            placeholder="Job title"
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium" htmlFor="description">
            Description
          </label>
          <textarea
            className="w-full rounded border px-3 py-2"
            id="description"
            name="description"
            required
            rows={4}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="Describe the role"
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium" htmlFor="location">
            Location
          </label>
          <input
            className="w-full rounded border px-3 py-2"
            id="location"
            name="location"
            required
            value={location}
            onChange={(event) => setLocation(event.target.value)}
            type="text"
            placeholder="City, Country"
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium" htmlFor="workType">
            Work Type
          </label>
          <select
            className="w-full rounded border px-3 py-2"
            id="workType"
            name="workType"
            value={workType}
            onChange={(event) => setWorkType(event.target.value as WorkType)}
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
            className="w-full rounded border px-3 py-2"
            id="seniority"
            name="seniority"
            value={seniority}
            onChange={(event) => setSeniority(event.target.value as Seniority)}
          >
            <option value="INTERN">INTERN</option>
            <option value="JUNIOR">JUNIOR</option>
            <option value="MID">MID</option>
            <option value="SENIOR">SENIOR</option>
            <option value="LEAD">LEAD</option>
          </select>
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium" htmlFor="skills">
            Skills
          </label>
          <input
            className="w-full rounded border px-3 py-2"
            id="skills"
            name="skills"
            required
            value={skills}
            onChange={(event) => setSkills(event.target.value)}
            type="text"
            placeholder="React, TypeScript, Spring Boot"
          />
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="salaryMin">
              Salary Min
            </label>
            <input
              className="w-full rounded border px-3 py-2"
              id="salaryMin"
              name="salaryMin"
              type="number"
              value={salaryMin}
              onChange={(event) => setSalaryMin(event.target.value)}
              placeholder="Optional"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="salaryMax">
              Salary Max
            </label>
            <input
              className="w-full rounded border px-3 py-2"
              id="salaryMax"
              name="salaryMax"
              type="number"
              value={salaryMax}
              onChange={(event) => setSalaryMax(event.target.value)}
              placeholder="Optional"
            />
          </div>
        </div>

        {error && <p className="text-sm text-red-600">Error: {error}</p>}

        <button
          className="rounded bg-black px-4 py-2 text-white disabled:opacity-60"
          disabled={submitting}
          type="submit"
        >
          {submitting ? "Creating..." : "Create Job"}
        </button>
      </form>
    </div>
  );
}
