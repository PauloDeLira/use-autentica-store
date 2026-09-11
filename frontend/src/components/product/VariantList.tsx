import type { ProductVariantSummary } from "../../types/variant";
import styles from "./VariantList.module.css";

interface VariantListProps {
  variants: ProductVariantSummary[];
}

export function VariantList({ variants }: VariantListProps) {
  if (variants.length === 0) {
    return <p className="status-message">Nenhuma variação cadastrada para este produto.</p>;
  }

  return (
    <ul className={styles.list}>
      {variants.map((variant) => (
        <li key={variant.id} className={styles.row}>
          <span className={styles.swatch} style={{ backgroundColor: variant.colorHexCode }} />
          <span className={styles.size}>{variant.sizeName}</span>
          <span className={styles.colorName}>{variant.colorName}</span>
          <span className={`${styles.badge} ${variant.available ? "" : styles.badgeUnavailable}`}>
            {variant.available ? `${variant.stockQuantity} em estoque` : "Indisponível"}
          </span>
        </li>
      ))}
    </ul>
  );
}
