// Match domain model returned by the matching-service
export interface Match {
  id: number; // Primary key of the match
  userId: number; // User identifier (from user-service)
  jobId: number; // Job identifier (from job-service)
  score: number; // Relevance score for the match
  status: string; // Status such as SUGGESTED/APPLIED
  createdAt: string; // ISO timestamp string
}