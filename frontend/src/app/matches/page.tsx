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

export default function MatchesPage() {
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const authenticated = Boolean(session?.token);
  const userId = session?.userId ?? null;

  const [matches, setMatches] = useState<JobMatch[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
      return;
    }

    if (!userId) {
      setError("No user ID is available for loading matches.");
      setLoading(false);
      return;
    }

    let isCancelled = false;

    const loadMatches = async () => {
      setLoading(true);
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
              : "Failed to load matches.",
          );
        }
      } finally {
        if (!isCancelled) {
          setLoading(false);
        }
      }
    };

    void loadMatches();

    return () => {
      isCancelled = true;
    };
  }, [authenticated, router, userId]);

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  return (
    <div className="space-y-4">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold">Your Matches</h1>
        <p className="text-sm text-gray-600">
          Recommended roles based on your current profile and preferences.
        </p>
      </div>

      {loading && <p>Loading matches...</p>}
      {error && <p className="text-red-600">{error}</p>}

      {!loading && matches.length === 0 && !error && <p>No matches found yet.</p>}

      <ul className="space-y-3">
        {matches.map((match) => (
          <li key={match.jobId} className="rounded border p-4">
            <div className="space-y-1">
              <h2 className="text-lg font-medium">{match.title}</h2>
              <p className="text-sm text-gray-700">{match.company}</p>
              <p className="text-sm text-gray-700">Score: {match.score}</p>
            </div>

            <div className="mt-3 space-y-2 text-sm text-gray-600">
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
          </li>
        ))}
      </ul>
    </div>
  );
}
