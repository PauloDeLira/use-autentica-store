import type { ProductVariantSummary } from "../types/variant";
import { formatPrice } from "./currency";

const WHATSAPP_NUMBER = import.meta.env.VITE_WHATSAPP_NUMBER;

export function buildWhatsAppLink(
  productName: string,
  variant: ProductVariantSummary,
  quantity: number,
  unitPrice: number,
): string {
  const message = [
    "Olá! Tenho interesse no seguinte produto:",
    "",
    `Produto: ${productName}`,
    `Cor: ${variant.colorName}`,
    `Tamanho: ${variant.sizeName}`,
    `Quantidade: ${quantity}`,
    `Valor: ${formatPrice(unitPrice * quantity)}`,
    "",
    "Gostaria de verificar a disponibilidade.",
  ].join("\n");

  return `https://wa.me/${WHATSAPP_NUMBER}?text=${encodeURIComponent(message)}`;
}

export function buildWhatsAppContactLink(message = "Olá! Gostaria de mais informações."): string {
  return `https://wa.me/${WHATSAPP_NUMBER}?text=${encodeURIComponent(message)}`;
}

export function formatWhatsAppNumber(): string {
  // 55 87 9 9990-1960 a partir de 5587999901960
  const digits = WHATSAPP_NUMBER.replace(/\D/g, "");
  const ddd = digits.slice(2, 4);
  const rest = digits.slice(4);
  const firstPart = rest.length > 8 ? rest.slice(0, -8) + " " + rest.slice(-8, -4) : rest.slice(0, -4);
  const lastPart = rest.slice(-4);
  return `(${ddd}) ${firstPart}-${lastPart}`;
}
