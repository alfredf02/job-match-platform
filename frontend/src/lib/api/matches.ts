import { Match } from "@/types/match"; // Shared Match interface for responses

const BASE_URL = "http://localhost:8083/api/matches"; // Matching-service endpoint

/**
 * Fetch all matches for a given user ID from the matching-service.
 */
export async function fetchMatchesByUserId(userId: number): Promise<Match[]> {
  const response = await fetch(`${BASE_URL}?userId=${userId}`);

  if (!response.ok) {
    // Bubble up a helpful error if the service is unreachable or returns non-2xx
    throw new Error(`Failed to fetch matches: ${response.status} ${response.statusText}`);
  }

  return response.json();
}

/**
 * Create a match with optional score/status overrides.
 */
export async function createMatch(payload: {
  userId: number;
  jobId: number;
  score?: number;
  status?: string;
}): Promise<Match> {
  const response = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(`Failed to create match: ${response.status} ${response.statusText} - ${message}`);
  }

  return response.json();
}