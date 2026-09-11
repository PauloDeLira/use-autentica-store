const STORAGE_KEY = "useautentica.admin.session";

export interface AdminSession {
  token: string;
  tokenType: string;
  name: string;
  email: string;
  role: string;
}

export function getStoredSession(): AdminSession | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw) as AdminSession;
  } catch {
    return null;
  }
}

export function storeSession(session: AdminSession): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

export function clearSession(): void {
  localStorage.removeItem(STORAGE_KEY);
}
