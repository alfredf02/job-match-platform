import { Job } from "@/types/jobs";

const JOB_SERVICE_BASE_URL = "http://localhost:8082";

export async function fetchJobs(): Promise<Job[]> {
  const response = await fetch(`${JOB_SERVICE_BASE_URL}/api/jobs`, {
    method: "GET",
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  return response.json();
}

interface CreateJobPayload {
  title: string;
  description: string;
  location: string;
  employmentType: string;
  salaryMin?: number;
  salaryMax?: number;
}

export async function createJob(payload: CreateJobPayload): Promise<Job> {
  const response = await fetch(`${JOB_SERVICE_BASE_URL}/api/jobs`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  return response.json();
}