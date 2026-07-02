import { get } from "@/lib/api/client";
import { MATCHING_SERVICE_URL } from "@/lib/api/config";
import { JobMatch } from "@/types/match";

const MATCHES_BASE_URL = `${MATCHING_SERVICE_URL}/api/matches`;

function withLimit(url: string, limit?: number): string {
  if (limit === undefined) {
    return url;
  }

  const searchParams = new URLSearchParams({ limit: String(limit) });
  return `${url}?${searchParams.toString()}`;
}

export function getMatches(userId: number | string, limit?: number): Promise<JobMatch[]> {
  const url = withLimit(`${MATCHES_BASE_URL}/${userId}`, limit);
  return get<JobMatch[]>(url);
}
