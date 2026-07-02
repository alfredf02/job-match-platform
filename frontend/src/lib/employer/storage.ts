const EMPLOYER_STORAGE_KEY = "job-match-platform.employerId";
const EMPLOYER_CHANGE_EVENT = "job-match-platform:employer-change";

function isBrowser(): boolean {
  return typeof window !== "undefined" && typeof window.localStorage !== "undefined";
}

export function subscribeToEmployerId(listener: () => void): () => void {
  if (!isBrowser()) {
    return () => undefined;
  }

  const handleChange = () => {
    listener();
  };

  window.addEventListener(EMPLOYER_CHANGE_EVENT, handleChange);
  window.addEventListener("storage", handleChange);

  return () => {
    window.removeEventListener(EMPLOYER_CHANGE_EVENT, handleChange);
    window.removeEventListener("storage", handleChange);
  };
}

export function getEmployerIdSnapshot(): string | null {
  if (!isBrowser()) {
    return null;
  }

  return window.localStorage.getItem(EMPLOYER_STORAGE_KEY);
}

export function getEmployerIdServerSnapshot(): string | null {
  return null;
}

export function saveEmployerId(employerId: number | string): void {
  if (!isBrowser()) {
    return;
  }

  window.localStorage.setItem(EMPLOYER_STORAGE_KEY, String(employerId));
  window.dispatchEvent(new Event(EMPLOYER_CHANGE_EVENT));
}
