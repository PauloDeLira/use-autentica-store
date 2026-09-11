import type { Size } from "./size";
import type { Color } from "./color";

export interface InventoryItem {
  id: string;
  productId: string;
  productName: string;
  sku: string;
  size: Size;
  color: Color;
  stockQuantity: number;
  active: boolean;
  available: boolean;
}
