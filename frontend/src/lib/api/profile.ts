import { get, put } from "@/lib/api/client";
import { USER_SERVICE_URL } from "@/lib/api/config";
import {
  ProfileListField,
  UpdatePreferencesRequest,
  UpdateProfileRequest,
  UserProfile,
} from "@/types/profile";

const PROFILE_BASE_URL = `${USER_SERVICE_URL}/api/profile`;

function toStringArray(value: ProfileListField | undefined): string[] {
  if (!value) {
    return [];
  }

  if (Array.isArray(value)) {
    return value.map((item) => item.trim()).filter(Boolean);
  }

  return value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function toCommaSeparated(value: ProfileListField | undefined): string | undefined {
  if (value === undefined) {
    return undefined;
  }

  const items = toStringArray(value);
  return items.length > 0 ? items.join(", ") : "";
}

function normalizeProfile(profile: UserProfile): UserProfile {
  const salaryMin = profile.salaryMin ?? profile.minSalary;
  const salaryMax = profile.salaryMax ?? profile.maxSalary;

  return {
    ...profile,
    salaryMin,
    salaryMax,
    minSalary: salaryMin,
    maxSalary: salaryMax,
    skills: toStringArray(profile.skills),
    desiredRoles: toStringArray(profile.desiredRoles),
  };
}

function serializeProfileRequest(
  request: UpdateProfileRequest | UpdatePreferencesRequest,
): Record<string, string | number | undefined> {
  const salaryMin = request.salaryMin ?? request.minSalary;
  const salaryMax = request.salaryMax ?? request.maxSalary;

  return {
    location: request.location,
    skills: toCommaSeparated(request.skills),
    desiredRoles: toCommaSeparated(request.desiredRoles),
    minSalary: salaryMin,
    maxSalary: salaryMax,
  };
}

export function getMyProfile(token: string): Promise<UserProfile> {
  return get<UserProfile>(`${PROFILE_BASE_URL}/me`, { token }).then(normalizeProfile);
}

export function updateProfile(
  token: string,
  request: UpdateProfileRequest,
): Promise<UserProfile> {
  return put<UserProfile>(PROFILE_BASE_URL, {
    token,
    body: serializeProfileRequest(request),
  }).then(normalizeProfile);
}

export function updatePreferences(
  token: string,
  request: UpdatePreferencesRequest,
): Promise<UserProfile> {
  return put<UserProfile>(`${PROFILE_BASE_URL}/preferences`, {
    token,
    body: serializeProfileRequest(request),
  }).then(normalizeProfile);
}
