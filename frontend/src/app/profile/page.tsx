"use client";

import { useEffect, useState, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { getMyProfile, updatePreferences, updateProfile } from "@/lib/api/profile";
import {
  getSessionServerSnapshot,
  getSessionSnapshot,
  getToken,
  subscribeToSession,
} from "@/lib/auth/session";

function toCommaSeparated(value: string[] | string | undefined): string {
  if (!value) {
    return "";
  }

  return Array.isArray(value) ? value.join(", ") : value;
}

function parseCommaSeparated(value: string): string[] {
  return value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

export default function ProfilePage() {
  const router = useRouter();
  const session = useSyncExternalStore(
    subscribeToSession,
    getSessionSnapshot,
    getSessionServerSnapshot,
  );
  const authenticated = Boolean(session?.token);

  const [fullName, setFullName] = useState("");
  const [location, setLocation] = useState("");
  const [salaryMin, setSalaryMin] = useState("");
  const [salaryMax, setSalaryMax] = useState("");
  const [skillsText, setSkillsText] = useState("");
  const [desiredRolesText, setDesiredRolesText] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSavingProfile, setIsSavingProfile] = useState(false);
  const [isSavingPreferences, setIsSavingPreferences] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [preferencesError, setPreferencesError] = useState<string | null>(null);
  const [profileSuccess, setProfileSuccess] = useState<string | null>(null);
  const [preferencesSuccess, setPreferencesSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (!authenticated) {
      router.replace("/login");
      return;
    }

    const token = getToken();

    if (!token) {
      router.replace("/login");
      return;
    }

    let isCancelled = false;

    const loadProfile = async () => {
      setIsLoading(true);
      setLoadError(null);

      try {
        const profile = await getMyProfile(token);

        if (isCancelled) {
          return;
        }

        setFullName(profile.fullName ?? "");
        setLocation(profile.location ?? "");
        setSalaryMin(String(profile.salaryMin ?? ""));
        setSalaryMax(String(profile.salaryMax ?? ""));
        setSkillsText(toCommaSeparated(profile.skills));
        setDesiredRolesText(toCommaSeparated(profile.desiredRoles));
      } catch (err) {
        if (isCancelled) {
          return;
        }

        setLoadError(
          err instanceof Error
            ? err.message
            : "Unable to load your profile right now. Please try again.",
        );
      } finally {
        if (!isCancelled) {
          setIsLoading(false);
        }
      }
    };

    void loadProfile();

    return () => {
      isCancelled = true;
    };
  }, [authenticated, router]);

  const handleProfileSave = async () => {
    const token = getToken();

    if (!token) {
      router.replace("/login");
      return;
    }

    setIsSavingProfile(true);
    setProfileError(null);
    setProfileSuccess(null);

    try {
      const updatedProfile = await updateProfile(token, {
        location,
        salaryMin: salaryMin ? Number(salaryMin) : undefined,
        salaryMax: salaryMax ? Number(salaryMax) : undefined,
      });

      setLocation(updatedProfile.location ?? "");
      setSalaryMin(String(updatedProfile.salaryMin ?? ""));
      setSalaryMax(String(updatedProfile.salaryMax ?? ""));
      setProfileSuccess("Profile details saved.");
    } catch (err) {
      setProfileError(
        err instanceof Error
          ? err.message
          : "Unable to save your profile right now. Please try again.",
      );
    } finally {
      setIsSavingProfile(false);
    }
  };

  const handlePreferencesSave = async () => {
    const token = getToken();

    if (!token) {
      router.replace("/login");
      return;
    }

    setIsSavingPreferences(true);
    setPreferencesError(null);
    setPreferencesSuccess(null);

    try {
      const updatedProfile = await updatePreferences(token, {
        skills: parseCommaSeparated(skillsText),
        desiredRoles: parseCommaSeparated(desiredRolesText),
      });

      setSkillsText(toCommaSeparated(updatedProfile.skills));
      setDesiredRolesText(toCommaSeparated(updatedProfile.desiredRoles));
      setPreferencesSuccess("Preferences saved.");
    } catch (err) {
      setPreferencesError(
        err instanceof Error
          ? err.message
          : "Unable to save your preferences right now. Please try again.",
      );
    } finally {
      setIsSavingPreferences(false);
    }
  };

  if (!authenticated) {
    return <p className="text-sm text-gray-600">Redirecting to login...</p>;
  }

  if (isLoading) {
    return <p className="text-sm text-gray-600">Loading your profile...</p>;
  }

  if (loadError) {
    return (
      <div className="mx-auto max-w-3xl rounded-xl border border-red-200 bg-red-50 p-6">
        <h1 className="text-2xl font-semibold text-black">Profile</h1>
        <p className="mt-3 text-sm text-red-700">{loadError}</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold">Profile</h1>
        <p className="text-sm text-gray-600">
          Update your profile details and matching preferences for a better job fit.
        </p>
      </header>

      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <div className="space-y-1">
          <h2 className="text-lg font-semibold">Profile Details</h2>
          <p className="text-sm text-gray-600">
            Keep your basic information and salary expectations up to date.
          </p>
        </div>

        <div className="mt-6 space-y-4">
          {fullName && (
            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="fullName">
                Full Name
              </label>
              <input
                id="fullName"
                name="fullName"
                type="text"
                value={fullName}
                onChange={(event) => setFullName(event.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              />
            </div>
          )}

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="location">
              Location
            </label>
            <input
              id="location"
              name="location"
              type="text"
              value={location}
              onChange={(event) => setLocation(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Sydney, Australia"
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="salaryMin">
                Salary Min
              </label>
              <input
                id="salaryMin"
                name="salaryMin"
                type="number"
                value={salaryMin}
                onChange={(event) => setSalaryMin(event.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
                placeholder="90000"
              />
            </div>

            <div className="space-y-2">
              <label className="block text-sm font-medium" htmlFor="salaryMax">
                Salary Max
              </label>
              <input
                id="salaryMax"
                name="salaryMax"
                type="number"
                value={salaryMax}
                onChange={(event) => setSalaryMax(event.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
                placeholder="140000"
              />
            </div>
          </div>

          {profileError && (
            <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {profileError}
            </p>
          )}

          {profileSuccess && (
            <p className="rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">
              {profileSuccess}
            </p>
          )}

          <button
            type="button"
            onClick={handleProfileSave}
            className="rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
            disabled={isSavingProfile}
          >
            {isSavingProfile ? "Saving profile..." : "Save Profile"}
          </button>
        </div>
      </section>

      <section className="rounded-xl border bg-white p-6 shadow-sm">
        <div className="space-y-1">
          <h2 className="text-lg font-semibold">Preferences</h2>
          <p className="text-sm text-gray-600">
            Enter skills and desired roles as comma-separated values.
          </p>
        </div>

        <div className="mt-6 space-y-4">
          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="skills">
              Skills
            </label>
            <textarea
              id="skills"
              name="skills"
              rows={4}
              value={skillsText}
              onChange={(event) => setSkillsText(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="TypeScript, React, Spring Boot"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium" htmlFor="desiredRoles">
              Desired Roles
            </label>
            <textarea
              id="desiredRoles"
              name="desiredRoles"
              rows={3}
              value={desiredRolesText}
              onChange={(event) => setDesiredRolesText(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-black"
              placeholder="Frontend Engineer, Full Stack Developer"
            />
          </div>

          {preferencesError && (
            <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {preferencesError}
            </p>
          )}

          {preferencesSuccess && (
            <p className="rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">
              {preferencesSuccess}
            </p>
          )}

          <button
            type="button"
            onClick={handlePreferencesSave}
            className="rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
            disabled={isSavingPreferences}
          >
            {isSavingPreferences ? "Saving preferences..." : "Save Preferences"}
          </button>
        </div>
      </section>
    </div>
  );
}
