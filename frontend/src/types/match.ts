export interface JobMatch {
  jobId: number;
  title: string;
  company: string;
  score: number;
  matchedSkills: string[];
  missingSkills: string[];
}
