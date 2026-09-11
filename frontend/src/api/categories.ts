import { apiGet } from "./client";
import type { Category } from "../types/category";

export function fetchCategories(): Promise<Category[]> {
  return apiGet<Category[]>("/api/categories");
}
