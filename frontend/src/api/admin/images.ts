import { apiDelete, apiUpload } from "../client";
import type { ProductImage } from "../../types/product";

export function uploadImage(productId: string, file: File, altText?: string): Promise<ProductImage> {
  const formData = new FormData();
  formData.append("file", file);
  if (altText) {
    formData.append("altText", altText);
  }
  return apiUpload<ProductImage>(`/api/admin/products/${productId}/images`, formData);
}

export function deleteImage(productId: string, imageId: string): Promise<void> {
  return apiDelete(`/api/admin/products/${productId}/images/${imageId}`);
}
