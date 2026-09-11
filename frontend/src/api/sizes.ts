import { apiGet } from "./client";
import type { Size } from "../types/size";

export function fetchSizes(): Promise<Size[]> {
  return apiGet<Size[]>("/api/sizes");
}
