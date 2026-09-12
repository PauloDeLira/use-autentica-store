import { apiGet } from "../client";
import type { WhatsAppClickStats } from "../../types/dashboard";

export function fetchWhatsAppClickStats(): Promise<WhatsAppClickStats> {
  return apiGet<WhatsAppClickStats>("/api/admin/dashboard/whatsapp-clicks", { auth: true });
}
