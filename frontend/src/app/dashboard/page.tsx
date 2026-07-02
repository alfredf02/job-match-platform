"use client";

import { useEffect, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";

export default function DashboardPage() {
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const authenticated = Boolean(session?.token);
  const userId = session?.userId ?? null;

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
    }
  }, [authenticated, router]);

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  return (
    <div className="space-y-6 rounded-xl border bg-white p-6 shadow-sm">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold">Dashboard</h1>
        <p className="text-sm text-gray-700">
          Welcome back{userId ? `, user ${userId}` : ""}. Your account is ready
          for the next milestone.
        </p>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <div className="rounded-lg border border-gray-200 p-4">
          <h2 className="text-sm font-semibold text-black">Profile</h2>
          <p className="mt-2 text-sm text-gray-600">
            Profile management will be connected in a later milestone.
          </p>
        </div>

        <div className="rounded-lg border border-gray-200 p-4">
          <h2 className="text-sm font-semibold text-black">Jobs and Matches</h2>
          <p className="mt-2 text-sm text-gray-600">
            Use the navigation above to move into recommended jobs, employer
            jobs, or job creation flows.
          </p>
        </div>
      </div>
    </div>
  );
}
