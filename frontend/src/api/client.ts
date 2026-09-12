import { clearSession, getStoredSession } from "../auth/session";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;

  constructor(body: ApiErrorBody) {
    super(body.message);
    this.status = body.status;
    this.code = body.error;
  }
}

interface RequestOptions {
  method?: string;
  body?: unknown;
  formData?: FormData;
  auth?: boolean;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {};
  let body: BodyInit | undefined;

  if (options.formData) {
    body = options.formData;
  } else if (options.body !== undefined) {
    headers["Content-Type"] = "application/json";
    body = JSON.stringify(options.body);
  }

  if (options.auth) {
    const session = getStoredSession();
    if (session) {
      headers.Authorization = `${session.tokenType} ${session.token}`;
    }
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers,
    body,
  });

  if (response.status === 401 && options.auth) {
    clearSession();
    window.location.href = "/admin/login";
  }

  if (!response.ok) {
    const errorBody = (await response.json()) as ApiErrorBody;
    throw new ApiError(errorBody);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export function apiGet<T>(path: string, options: { auth?: boolean } = {}): Promise<T> {
  return request<T>(path, { method: "GET", auth: options.auth ?? false });
}

export function apiPost<T>(path: string, body: unknown, options: { auth?: boolean } = {}): Promise<T> {
  return request<T>(path, { method: "POST", body, auth: options.auth ?? true });
}

export function apiPut<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, { method: "PUT", body, auth: true });
}

export function apiPatch<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, { method: "PATCH", body, auth: true });
}

export function apiDelete(path: string): Promise<void> {
  return request<void>(path, { method: "DELETE", auth: true });
}

export function apiUpload<T>(path: string, formData: FormData): Promise<T> {
  return request<T>(path, { method: "POST", formData, auth: true });
}

export function resolveAssetUrl(path: string): string {
  // Em produção o Cloudinary já retorna uma URL absoluta; em dev o backend
  // retorna um caminho relativo (/uploads/...) que precisa do host da API.
  if (/^https?:\/\//.test(path)) {
    return path;
  }
  return `${API_BASE_URL}${path}`;
}
