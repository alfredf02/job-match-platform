"use client";

import { useEffect, useState, type FormEvent } from "react";
import { createMatch, fetchMatchesByUserId } from "@/lib/api/matches";
import { Match } from "@/types/match";

// TODO: Replace this with real auth context/user data
const currentUserId = 1; // Hardcoded for now so we can test end-to-end

export default function MatchesPage() {
  const [matches, setMatches] = useState<Match[]>([]); // Loaded matches list
  const [loading, setLoading] = useState(true); // Loading flag for initial fetch
  const [error, setError] = useState<string | null>(null); // Error message holder

  // Simple form state for creating a dummy match
  const [jobIdInput, setJobIdInput] = useState("101");
  const [scoreInput, setScoreInput] = useState("0.8");
  const [statusInput, setStatusInput] = useState("SUGGESTED");

  useEffect(() => {
    // Load matches as soon as the component mounts
    const loadMatches = async () => {
      try {
        const data = await fetchMatchesByUserId(currentUserId);
        setMatches(data);
      } catch (err) {
        const message = err instanceof Error ? err.message : "Failed to load matches";
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    loadMatches();
  }, []);

  const handleCreateMatch = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null); // Reset any prior error before submission

    if (!jobIdInput) {
      setError("Please provide a job ID to create a match.");
      return;
    }

    try {
      const payload = {
        userId: currentUserId, // Placeholder until auth wiring is added
        jobId: Number(jobIdInput),
        score: scoreInput ? Number(scoreInput) : undefined,
        status: statusInput || undefined,
      };

      const created = await createMatch(payload);
      // Prepend the newly created match so it appears at the top
      setMatches((prev) => [created, ...prev]);

      // Reset the form to make multiple submissions easier
      setJobIdInput("");
      setScoreInput("");
      setStatusInput("SUGGESTED");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to create match";
      setError(message);
    }
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold">Your Matches</h1>

      {/* Creation form for quick end-to-end testing */}
      <form onSubmit={handleCreateMatch} className="space-y-2 border p-4 rounded">
        <div className="flex flex-col gap-1">
          <label className="font-medium">Job ID</label>
          <input
            className="border p-2 rounded"
            value={jobIdInput}
            onChange={(e) => setJobIdInput(e.target.value)}
            placeholder="Enter a job ID"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="font-medium">Score (optional)</label>
          <input
            className="border p-2 rounded"
            value={scoreInput}
            onChange={(e) => setScoreInput(e.target.value)}
            placeholder="e.g. 0.75"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="font-medium">Status (optional)</label>
          <input
            className="border p-2 rounded"
            value={statusInput}
            onChange={(e) => setStatusInput(e.target.value)}
            placeholder="e.g. SUGGESTED"
          />
        </div>
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Create Match
        </button>
      </form>

      {/* Loading and error states to guide the user */}
      {loading && <p>Loading matches...</p>}
      {error && <p className="text-red-600">{error}</p>}

      {/* Empty state message */}
      {!loading && matches.length === 0 && !error && <p>No matches found yet.</p>}

      {/* Matches list */}
      <ul className="space-y-3">
        {matches.map((match) => (
          <li key={match.id} className="border p-3 rounded">
            <p className="font-semibold">Status: {match.status}</p>
            <p>Score: {match.score}</p>
            <p>Job ID: {match.jobId}</p>
            <p className="text-sm text-gray-500">Created: {new Date(match.createdAt).toLocaleString()}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}