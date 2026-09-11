import { apiDelete, apiGet, apiPatch, apiPost, apiPut } from "../client";
import type { Product, ProductSummary } from "../../types/product";

export interface ProductPayload {
  name: string;
  description: string | null;
  price: number;
  categoryId: string;
}

export function fetchAdminProducts(): Promise<ProductSummary[]> {
  return apiGet<ProductSummary[]>("/api/admin/products", { auth: true });
}

export function fetchAdminProduct(id: string): Promise<Product> {
  return apiGet<Product>(`/api/admin/products/${id}`, { auth: true });
}

export function createProduct(payload: ProductPayload): Promise<Product> {
  return apiPost<Product>("/api/admin/products", payload);
}

export function updateProduct(id: string, payload: ProductPayload): Promise<Product> {
  return apiPut<Product>(`/api/admin/products/${id}`, payload);
}

export function deleteProduct(id: string): Promise<void> {
  return apiDelete(`/api/admin/products/${id}`);
}

export function setProductActive(id: string, active: boolean): Promise<Product> {
  return apiPatch<Product>(`/api/admin/products/${id}/active`, { active });
}
