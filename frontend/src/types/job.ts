export type WorkType = "ONSITE" | "HYBRID" | "REMOTE";

export type Seniority = "INTERN" | "JUNIOR" | "MID" | "SENIOR" | "LEAD";

export interface Employer {
  id: number;
  name: string;
  website?: string;
  description?: string;
  location?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateEmployerRequest {
  name: string;
  website?: string;
  description?: string;
  location?: string;
}

export interface Job {
  id: number;
  employer?: Employer;
  employerId?: number;
  company?: string;
  companyName?: string;
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

export interface CreateJobRequest {
  employerId: number;
  title: string;
  description: string;
  location: string;
  workType: WorkType;
  seniority: Seniority;
  salaryMin?: number;
  salaryMax?: number;
  skills: string[];
}

export interface UpdateJobRequest {
  title?: string;
  description?: string;
  location?: string;
  workType?: WorkType;
  seniority?: Seniority;
  salaryMin?: number;
  salaryMax?: number;
  skills?: string[];
}
