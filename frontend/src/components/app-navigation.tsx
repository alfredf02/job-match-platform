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
  { href: "/dashboard", label: "Dashboard" },
  { href: "/profile", label: "Profile" },
  { href: "/matches", label: "Recommended Jobs" },
  { href: "/employer/jobs", label: "Employer Jobs" },
  { href: "/jobs/create", label: "Create Job" },
];

function linkClassName(isActive: boolean): string {
  return isActive ? "font-medium text-black" : "text-gray-600 hover:text-black";
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
          className={linkClassName(pathname === link.href)}
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
