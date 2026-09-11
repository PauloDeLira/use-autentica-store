import { apiGet, apiPatch } from "../client";
import type { InventoryItem } from "../../types/inventory";

export function fetchInventory(): Promise<InventoryItem[]> {
  return apiGet<InventoryItem[]>("/api/admin/inventory", { auth: true });
}

export function updateStock(variantId: string, stockQuantity: number): Promise<InventoryItem> {
  return apiPatch<InventoryItem>(`/api/admin/inventory/${variantId}`, { stockQuantity });
}
