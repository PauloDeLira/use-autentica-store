import { apiPatch, apiPost, apiPut } from "../client";
import type { Size } from "../../types/size";

export interface SizePayload {
  name: string;
  displayOrder: number;
}

export function createSize(payload: SizePayload): Promise<Size> {
  return apiPost<Size>("/api/admin/sizes", payload);
}

export function updateSize(id: string, payload: SizePayload): Promise<Size> {
  return apiPut<Size>(`/api/admin/sizes/${id}`, payload);
}

export function setSizeActive(id: string, active: boolean): Promise<Size> {
  return apiPatch<Size>(`/api/admin/sizes/${id}/active`, { active });
}
