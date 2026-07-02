import { del, get, post, put } from "@/lib/api/client";
import { JOB_SERVICE_URL } from "@/lib/api/config";
import {
  CreateEmployerRequest,
  CreateJobRequest,
  Employer,
  Job,
  Seniority,
  UpdateJobRequest,
  WorkType,
} from "@/types/job";

interface EmployerResponse {
  id: number;
  name: string;
  website?: string;
  description?: string;
  location?: string;
  createdAt?: string;
  updatedAt?: string;
}

interface JobResponse {
  id: number;
  employer?: EmployerResponse;
  title: string;
  description: string;
  location: string;
  salaryMin?: number;
  salaryMax?: number;
  workType?: WorkType;
  seniority?: Seniority;
  skills?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface SearchJobsFilters {
  location?: string;
  workType?: WorkType;
  seniority?: Seniority;
  salaryMin?: number;
  salaryMax?: number;
  skill?: string;
  limit?: number;
  token?: string;
}

const EMPLOYERS_BASE_URL = `${JOB_SERVICE_URL}/api/employers`;
const JOBS_BASE_URL = `${JOB_SERVICE_URL}/api/jobs`;

function normalizeEmployer(employer: EmployerResponse): Employer {
  return {
    id: employer.id,
    name: employer.name,
    website: employer.website,
    description: employer.description,
    location: employer.location,
    createdAt: employer.createdAt,
    updatedAt: employer.updatedAt,
  };
}

function normalizeJob(job: JobResponse): Job {
  const employer = job.employer ? normalizeEmployer(job.employer) : undefined;

  return {
    id: job.id,
    employer,
    employerId: employer?.id,
    company: employer?.name,
    companyName: employer?.name,
    title: job.title,
    description: job.description,
    location: job.location,
    salaryMin: job.salaryMin,
    salaryMax: job.salaryMax,
    workType: job.workType,
    seniority: job.seniority,
    skills: job.skills ?? [],
    createdAt: job.createdAt,
    updatedAt: job.updatedAt,
  };
}

function withQueryParams(baseUrl: string, filters?: SearchJobsFilters): string {
  if (!filters) {
    return baseUrl;
  }

  const searchParams = new URLSearchParams();

  if (filters.location) {
    searchParams.set("location", filters.location);
  }
  if (filters.workType) {
    searchParams.set("workType", filters.workType);
  }
  if (filters.seniority) {
    searchParams.set("seniority", filters.seniority);
  }
  if (filters.salaryMin !== undefined) {
    searchParams.set("salaryMin", String(filters.salaryMin));
  }
  if (filters.salaryMax !== undefined) {
    searchParams.set("salaryMax", String(filters.salaryMax));
  }
  if (filters.skill) {
    searchParams.set("skill", filters.skill);
  }
  if (filters.limit !== undefined) {
    searchParams.set("limit", String(filters.limit));
  }

  const query = searchParams.toString();
  return query ? `${baseUrl}?${query}` : baseUrl;
}

export async function createEmployer(
  request: CreateEmployerRequest,
  token?: string,
): Promise<Employer> {
  const response = await post<EmployerResponse>(EMPLOYERS_BASE_URL, {
    token,
    body: request,
  });

  return normalizeEmployer(response);
}

export async function getEmployer(id: number, token?: string): Promise<Employer> {
  const response = await get<EmployerResponse>(`${EMPLOYERS_BASE_URL}/${id}`, { token });
  return normalizeEmployer(response);
}

export async function getEmployerJobs(
  employerId: number,
  token?: string,
): Promise<Job[]> {
  const response = await get<JobResponse[]>(`${EMPLOYERS_BASE_URL}/${employerId}/jobs`, {
    token,
  });

  return response.map(normalizeJob);
}

export async function createJob(
  request: CreateJobRequest,
  token?: string,
): Promise<Job> {
  const response = await post<JobResponse>(JOBS_BASE_URL, {
    token,
    body: request,
  });

  return normalizeJob(response);
}

export async function getJob(id: number, token?: string): Promise<Job> {
  const response = await get<JobResponse>(`${JOBS_BASE_URL}/${id}`, { token });
  return normalizeJob(response);
}

export async function updateJob(
  id: number,
  request: UpdateJobRequest,
  token?: string,
): Promise<Job> {
  const response = await put<JobResponse>(`${JOBS_BASE_URL}/${id}`, {
    token,
    body: request,
  });

  return normalizeJob(response);
}

export function deleteJob(id: number, token?: string): Promise<void> {
  return del<void>(`${JOBS_BASE_URL}/${id}`, { token });
}

export async function searchJobs(filters?: SearchJobsFilters): Promise<Job[]> {
  const url = withQueryParams(JOBS_BASE_URL, filters);
  const response = await get<JobResponse[]>(url, { token: filters?.token });
  return response.map(normalizeJob);
}

// Compatibility helpers for earlier pages that already import these names.
export function fetchJobs(): Promise<Job[]> {
  return searchJobs();
}
