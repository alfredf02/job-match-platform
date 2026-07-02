"use client";

import { useEffect, useState, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { getMatches } from "@/lib/api/matches";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";
import { JobMatch } from "@/types/match";

function formatScore(score: number): string {
  const percentage = score <= 1 ? score * 100 : score;
  return `${Math.round(percentage)}%`;
}

export default function RecommendationsPage() {
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const authenticated = Boolean(session?.token);
  const userId = session?.userId ?? null;

  const [matches, setMatches] = useState<JobMatch[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
      return;
    }

    if (!userId) {
      setError("Your session does not include a user ID. Please log in again.");
      setIsLoading(false);
      return;
    }

    let isCancelled = false;

    const loadRecommendations = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const data = await getMatches(userId, 20);

        if (!isCancelled) {
          setMatches(data);
        }
      } catch (err) {
        if (!isCancelled) {
          setError(
            err instanceof Error
              ? err.message
              : "Unable to load recommended jobs right now. Please try again.",
          );
        }
      } finally {
        if (!isCancelled) {
          setIsLoading(false);
        }
      }
    };

    void loadRecommendations();

    return () => {
      isCancelled = true;
    };
  }, [authenticated, router, userId]);

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold">Recommended Jobs</h1>
        <p className="text-sm text-gray-600">
          These roles are ordered by explainable matching against your current
          profile and preferences.
        </p>
      </header>

      {isLoading && <p className="text-sm text-gray-600">Loading recommendations...</p>}

      {error && (
        <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
          {error}
        </p>
      )}

      {!isLoading && !error && matches.length === 0 && (
        <section className="rounded-xl border bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-700">No recommended jobs are available yet.</p>
        </section>
      )}

      {!isLoading && !error && matches.length > 0 && (
        <div className="space-y-4">
          {matches.map((match) => (
            <section key={match.jobId} className="rounded-xl border bg-white p-6 shadow-sm">
              <div className="space-y-2">
                <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h2 className="text-lg font-semibold">{match.title}</h2>
                    <p className="text-sm text-gray-700">{match.company}</p>
                  </div>
                  <p className="text-sm font-medium text-black">
                    Match Score: {formatScore(match.score)}
                  </p>
                </div>

                <div className="space-y-2 text-sm text-gray-600">
                  <p>
                    Matched Skills:{" "}
                    {match.matchedSkills.length > 0
                      ? match.matchedSkills.join(", ")
                      : "None"}
                  </p>
                  <p>
                    Missing Skills:{" "}
                    {match.missingSkills.length > 0
                      ? match.missingSkills.join(", ")
                      : "None"}
                  </p>
                </div>
              </div>
            </section>
          ))}
        </div>
      )}
    </div>
  );
}
