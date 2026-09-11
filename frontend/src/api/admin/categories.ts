import { apiPatch, apiPost, apiPut } from "../client";
import type { Category } from "../../types/category";

export interface CategoryPayload {
  name: string;
  description: string | null;
}

export function createCategory(payload: CategoryPayload): Promise<Category> {
  return apiPost<Category>("/api/admin/categories", payload);
}

export function updateCategory(id: string, payload: CategoryPayload): Promise<Category> {
  return apiPut<Category>(`/api/admin/categories/${id}`, payload);
}

export function setCategoryActive(id: string, active: boolean): Promise<Category> {
  return apiPatch<Category>(`/api/admin/categories/${id}/active`, { active });
}
