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
        Comprar pelo WhatsApp
      </a>
    </div>
  );
}
