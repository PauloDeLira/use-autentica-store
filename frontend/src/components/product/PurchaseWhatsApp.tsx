import { useState } from "react";
import type { ProductVariantSummary } from "../../types/variant";
import { buildWhatsAppLink } from "../../utils/whatsapp";
import styles from "./PurchaseWhatsApp.module.css";

interface PurchaseWhatsAppProps {
  productName: string;
  price: number;
  variant: ProductVariantSummary;
}

export function PurchaseWhatsApp({ productName, price, variant }: PurchaseWhatsAppProps) {
  const [quantity, setQuantity] = useState(1);

  function updateQuantity(value: number) {
    if (Number.isNaN(value)) return;
    setQuantity(Math.min(Math.max(value, 1), variant.stockQuantity));
  }

  return (
    <div className={styles.purchase}>
      <label className={styles.quantityLabel}>
        Quantidade
        <input
          type="number"
          min={1}
          max={variant.stockQuantity}
          value={quantity}
          onChange={(event) => updateQuantity(Number(event.target.value))}
          className={styles.quantityInput}
        />
      </label>

      <a
        href={buildWhatsAppLink(productName, variant, quantity, price)}
        target="_blank"
        rel="noopener noreferrer"
        className={styles.whatsappButton}
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
          <path d="M12.04 2C6.58 2 2.13 6.45 2.13 11.91c0 1.75.46 3.45 1.32 4.95L2 22l5.25-1.38a9.9 9.9 0 0 0 4.79 1.22h.01c5.46 0 9.91-4.45 9.91-9.91C21.96 6.45 17.5 2 12.04 2zm5.8 14.18c-.24.68-1.2 1.26-1.97 1.42-.53.11-1.21.2-3.51-.75-2.95-1.22-4.85-4.21-5-4.4-.14-.2-1.19-1.58-1.19-3.02 0-1.43.75-2.14 1.02-2.43.27-.29.58-.36.78-.36.19 0 .39 0 .56.01.18.01.42-.07.66.5.24.58.83 2.01.9 2.16.07.14.12.31.02.51-.1.2-.15.32-.29.49-.15.17-.31.38-.44.51-.14.14-.29.3-.13.58.17.29.74 1.22 1.59 1.98 1.09.97 2.01 1.27 2.3 1.42.29.14.45.12.62-.07.17-.2.71-.83.9-1.12.19-.29.38-.24.64-.14.26.09 1.68.79 1.97.94.29.14.48.22.55.34.07.12.07.7-.17 1.38z" />
        </svg>
        Comprar pelo WhatsApp
      </a>
    </div>
  );
}
