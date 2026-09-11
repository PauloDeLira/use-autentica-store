import { apiGet } from "./client";
import type { Color } from "../types/color";

export function fetchColors(): Promise<Color[]> {
  return apiGet<Color[]>("/api/colors");
}
