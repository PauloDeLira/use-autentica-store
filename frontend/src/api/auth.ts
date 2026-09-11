import { apiPost } from "./client";
import type { AdminSession } from "../auth/session";

export function login(email: string, password: string): Promise<AdminSession> {
  return apiPost<AdminSession>("/api/auth/login", { email, password }, { auth: false });
}
