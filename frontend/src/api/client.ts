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

export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`);

  if (!response.ok) {
    const body = (await response.json()) as ApiErrorBody;
    throw new ApiError(body);
  }

  return (await response.json()) as T;
}

export function resolveAssetUrl(path: string): string {
  return `${API_BASE_URL}${path}`;
}
