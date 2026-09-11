import { apiGet } from "./client";
import type { Product, ProductSummary } from "../types/product";
import type { ProductVariantSummary } from "../types/variant";

export interface ProductFilters {
  categoryId?: string;
  sizeId?: string;
  colorId?: string;
}

export function fetchProducts(filters: ProductFilters = {}): Promise<ProductSummary[]> {
  const params = new URLSearchParams();
  if (filters.categoryId) params.set("categoryId", filters.categoryId);
  if (filters.sizeId) params.set("sizeId", filters.sizeId);
  if (filters.colorId) params.set("colorId", filters.colorId);

  const query = params.toString();
  return apiGet<ProductSummary[]>(`/api/products${query ? `?${query}` : ""}`);
}

export function fetchProductById(id: string): Promise<Product> {
  return apiGet<Product>(`/api/products/${id}`);
}

export function fetchProductVariants(id: string): Promise<ProductVariantSummary[]> {
  return apiGet<ProductVariantSummary[]>(`/api/products/${id}/variants`);
}
