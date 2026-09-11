import type { Category } from "./category";

export interface ProductImage {
  id: string;
  url: string;
  altText: string | null;
  displayOrder: number;
}

export interface ProductSummary {
  id: string;
  name: string;
  price: number;
  active: boolean;
  categoryName: string;
  coverImageUrl: string | null;
  available: boolean;
}

export interface Product {
  id: string;
  name: string;
  description: string | null;
  price: number;
  active: boolean;
  category: Category;
  images: ProductImage[];
  createdAt: string;
  updatedAt: string;
}
