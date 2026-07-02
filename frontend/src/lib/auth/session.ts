import { LoginResponse } from "@/types/auth";

const SESSION_STORAGE_KEY = "job-match-platform.session";
const SESSION_CHANGE_EVENT = "job-match-platform:session-change";

interface StoredSession {
  token?: string;
  userId?: number;
  email?: string;
}

type SessionSource = LoginResponse & {
  accessToken?: string;
  jwt?: string;
  id?: number;
};

function isBrowser(): boolean {
  return typeof window !== "undefined" && typeof window.localStorage !== "undefined";
}

function notifySessionChange(): void {
  if (!isBrowser()) {
    return;
  }

  window.dispatchEvent(new Event(SESSION_CHANGE_EVENT));
}

function readSession(): StoredSession | null {
  if (!isBrowser()) {
    return null;
  }

  const rawSession = window.localStorage.getItem(SESSION_STORAGE_KEY);

  if (!rawSession) {
    return null;
  }

  try {
    const parsed = JSON.parse(rawSession) as StoredSession;
    return parsed;
  } catch {
    window.localStorage.removeItem(SESSION_STORAGE_KEY);
    return null;
  }
}

function resolveToken(response: SessionSource): string | undefined {
  return response.token ?? response.accessToken ?? response.jwt;
}

function resolveUserId(response: SessionSource): number | undefined {
  return response.userId ?? response.id;
}

export function saveSession(response: SessionSource): void {
  if (!isBrowser()) {
    return;
  }

  const session: StoredSession = {
    token: resolveToken(response),
    userId: resolveUserId(response),
    email: response.email,
  };

  // LocalStorage JWT storage is acceptable for this MVP, but it is less secure
  // than using HttpOnly cookies for production authentication flows.
  window.localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
  notifySessionChange();
}

export function getToken(): string | null {
  return readSession()?.token ?? null;
}

export function getCurrentUserId(): number | null {
  return readSession()?.userId ?? null;
}

export function isAuthenticated(): boolean {
  return Boolean(getToken());
}

export function clearSession(): void {
  if (!isBrowser()) {
    return;
  }

  // LocalStorage JWT storage is acceptable for this MVP, but it is less secure
  // than using HttpOnly cookies for production authentication flows.
  window.localStorage.removeItem(SESSION_STORAGE_KEY);
  notifySessionChange();
}

export function getSession(): StoredSession | null {
  return readSession();
}

export function getSessionSnapshot(): StoredSession | null {
  return readSession();
}

export function getSessionServerSnapshot(): StoredSession | null {
  return null;
}

export function subscribeToSession(listener: () => void): () => void {
  if (!isBrowser()) {
    return () => undefined;
  }

  const handleChange = () => {
    listener();
  };

  window.addEventListener(SESSION_CHANGE_EVENT, handleChange);
  window.addEventListener("storage", handleChange);

  return () => {
    window.removeEventListener(SESSION_CHANGE_EVENT, handleChange);
    window.removeEventListener("storage", handleChange);
  };
}
