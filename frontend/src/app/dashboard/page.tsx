"use client";

import Link from "next/link";
import { useEffect, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";

const dashboardLinks = [
  {
    title: "Profile",
    description: "Update your details, skills, salary range, and role preferences.",
    href: "/profile",
    cta: "Manage Profile",
  },
  {
    title: "Recommended Jobs",
    description: "Review explainable job matches based on your current profile.",
    href: "/recommendations",
    cta: "View Recommendations",
  },
  {
    title: "Employer Jobs",
    description: "Create and manage employer postings when you are hiring.",
    href: "/employer/jobs",
    cta: "Manage Employer Jobs",
  },
  {
    title: "Browse Jobs",
    description: "Explore the public job catalogue and current search filters.",
    href: "/jobs",
    cta: "Browse Jobs",
  },
];

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
    <div className="mx-auto max-w-5xl space-y-6">
      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <div className="space-y-2">
          <h1 className="text-2xl font-semibold">Dashboard</h1>
          <p className="text-sm text-gray-700">
            Welcome back{userId ? `, user ${userId}` : ""}. Choose the next step for your
            candidate or employer workflow.
          </p>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-2">
        {dashboardLinks.map((card) => (
          <div key={card.href} className="rounded-xl border bg-white p-6 shadow-sm">
            <div className="space-y-3">
              <h2 className="text-lg font-semibold text-black">{card.title}</h2>
              <p className="text-sm text-gray-600">{card.description}</p>
              <Link
                href={card.href}
                className="inline-flex rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 transition hover:border-black hover:text-black"
              >
                {card.cta}
              </Link>
            </div>
          </div>
        ))}
      </section>
    </div>
  );
}
