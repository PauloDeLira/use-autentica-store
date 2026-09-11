import type { ProductVariantSummary } from "../types/variant";
import { formatPrice } from "./currency";

const WHATSAPP_NUMBER = import.meta.env.VITE_WHATSAPP_NUMBER;

export function buildWhatsAppLink(
  productName: string,
  variant: ProductVariantSummary,
  quantity: number,
  price: number,
): string {
  const message = [
    "Olá! Tenho interesse no seguinte produto:",
    "",
    `Produto: ${productName}`,
    `Cor: ${variant.colorName}`,
    `Tamanho: ${variant.sizeName}`,
    `Quantidade: ${quantity}`,
    `Valor: ${formatPrice(price)}`,
    "",
    "Gostaria de verificar a disponibilidade.",
  ].join("\n");

  return `https://wa.me/${WHATSAPP_NUMBER}?text=${encodeURIComponent(message)}`;
}
