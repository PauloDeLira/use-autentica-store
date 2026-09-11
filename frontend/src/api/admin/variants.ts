import { apiDelete, apiPost, apiPut } from "../client";
import type { InventoryItem } from "../../types/inventory";

export interface VariantPayload {
  sizeId: string;
  colorId: string;
  stockQuantity: number;
}

export function createVariant(productId: string, payload: VariantPayload): Promise<InventoryItem> {
  return apiPost<InventoryItem>(`/api/admin/products/${productId}/variants`, payload);
}

export function updateVariant(productId: string, variantId: string, payload: VariantPayload): Promise<InventoryItem> {
  return apiPut<InventoryItem>(`/api/admin/products/${productId}/variants/${variantId}`, payload);
}

export function deleteVariant(productId: string, variantId: string): Promise<void> {
  return apiDelete(`/api/admin/products/${productId}/variants/${variantId}`);
}
