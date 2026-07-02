export type ApiMethod = "GET" | "POST" | "PUT" | "DELETE";

type ApiHeaders = Record<string, string>;

interface ApiRequestOptions {
  method?: ApiMethod;
  body?: unknown;
  headers?: ApiHeaders;
  token?: string;
  fetcher?: typeof fetch;
}

function buildHeaders(options: ApiRequestOptions): ApiHeaders {
  const headers: ApiHeaders = { ...(options.headers ?? {}) };

  if (options.body !== undefined && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }

  if (options.token) {
    headers.Authorization = `Bearer ${options.token}`;
  }

  return headers;
}

async function parseResponse(response: Response): Promise<unknown> {
  if (response.status === 204 || response.status === 205) {
    return undefined;
  }

  const text = await response.text();

  if (!text) {
    return undefined;
  }

  const contentType = response.headers.get("Content-Type") ?? "";

  if (contentType.includes("application/json")) {
    return JSON.parse(text);
  }

  return text;
}

function toErrorMessage(method: ApiMethod, url: string, status: number, payload: unknown): string {
  if (typeof payload === "string" && payload.trim()) {
    return `${method} ${url} failed with ${status}: ${payload}`;
  }

  if (payload && typeof payload === "object" && "message" in payload) {
    const message = payload.message;
    if (typeof message === "string" && message.trim()) {
      return `${method} ${url} failed with ${status}: ${message}`;
    }
  }

  return `${method} ${url} failed with ${status}`;
}

export async function request<T>(
  url: string,
  options: ApiRequestOptions = {},
): Promise<T> {
  const method = options.method ?? "GET";
  const fetcher = options.fetcher ?? fetch;

  const response = await fetcher(url, {
    method,
    headers: buildHeaders(options),
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  });

  const payload = await parseResponse(response);

  if (!response.ok) {
    throw new Error(toErrorMessage(method, url, response.status, payload));
  }

  return payload as T;
}

export function get<T>(
  url: string,
  options: Omit<ApiRequestOptions, "body" | "method"> = {},
): Promise<T> {
  return request<T>(url, { ...options, method: "GET" });
}

export function post<T>(
  url: string,
  options: Omit<ApiRequestOptions, "method"> = {},
): Promise<T> {
  return request<T>(url, { ...options, method: "POST" });
}

export function put<T>(
  url: string,
  options: Omit<ApiRequestOptions, "method"> = {},
): Promise<T> {
  return request<T>(url, { ...options, method: "PUT" });
}

export function del<T>(
  url: string,
  options: Omit<ApiRequestOptions, "body" | "method"> = {},
): Promise<T> {
  return request<T>(url, { ...options, method: "DELETE" });
}
