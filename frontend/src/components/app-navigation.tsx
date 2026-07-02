"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useSyncExternalStore } from "react";
import {
  clearSession,
  getSessionServerSnapshot,
  getSessionSnapshot,
  subscribeToSession,
} from "@/lib/auth/session";

const authenticatedLinks = [
  { href: "/dashboard", label: "Dashboard", match: ["/dashboard"] },
  { href: "/profile", label: "Profile", match: ["/profile"] },
  {
    href: "/recommendations",
    label: "Recommended Jobs",
    match: ["/recommendations", "/matches"],
  },
  {
    href: "/employer/jobs",
    label: "Employer Jobs",
    match: ["/employer/jobs"],
  },
  {
    href: "/employer/jobs/new",
    label: "Create Job",
    match: ["/employer/jobs/new", "/jobs/create"],
  },
  { href: "/jobs", label: "Browse Jobs", match: ["/jobs"] },
];

function isActivePath(pathname: string, matchers: string[]): boolean {
  return matchers.some((matcher) => pathname === matcher || pathname.startsWith(`${matcher}/`));
}

function linkClassName(isActive: boolean): string {
  return isActive
    ? "font-medium text-black"
    : "text-gray-600 transition hover:text-black";
}

export default function AppNavigation() {
  const pathname = usePathname();
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const authenticated = Boolean(session?.token);

  const handleLogout = () => {
    clearSession();
    router.push("/login");
  };

  if (!authenticated) {
    return (
      <div className="flex items-center gap-4 text-sm">
        <Link href="/jobs" className={linkClassName(pathname === "/jobs")}>
          Browse Jobs
        </Link>
        <Link href="/login" className={linkClassName(pathname === "/login")}>
          Login
        </Link>
        <Link href="/register" className={linkClassName(pathname === "/register")}>
          Register
        </Link>
      </div>
    );
  }

  return (
    <div className="flex flex-wrap items-center gap-4 text-sm">
      {authenticatedLinks.map((link) => (
        <Link
          key={link.href}
          href={link.href}
          className={linkClassName(isActivePath(pathname, link.match))}
        >
          {link.label}
        </Link>
      ))}

      <button
        type="button"
        onClick={handleLogout}
        className="text-gray-600 transition hover:text-black"
      >
        Logout
      </button>
    </div>
  );
}
