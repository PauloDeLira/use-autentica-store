import { apiPatch, apiPost, apiPut } from "../client";
import type { Color } from "../../types/color";

export interface ColorPayload {
  name: string;
  hexCode: string;
}

export function createColor(payload: ColorPayload): Promise<Color> {
  return apiPost<Color>("/api/admin/colors", payload);
}

export function updateColor(id: string, payload: ColorPayload): Promise<Color> {
  return apiPut<Color>(`/api/admin/colors/${id}`, payload);
}

export function setColorActive(id: string, active: boolean): Promise<Color> {
  return apiPatch<Color>(`/api/admin/colors/${id}/active`, { active });
}
