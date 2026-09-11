import { Link } from "react-router-dom";
import { resolveAssetUrl } from "../../api/client";
import type { ProductSummary } from "../../types/product";
import { formatPrice } from "../../utils/currency";
import styles from "./ProductCard.module.css";

interface ProductCardProps {
  product: ProductSummary;
}

export function ProductCard({ product }: ProductCardProps) {
  return (
    <Link to={`/produtos/${product.id}`} className={styles.card}>
      {!product.available && <span className={styles.unavailableBadge}>Indisponível</span>}
      {product.coverImageUrl ? (
        <img
          className={styles.image}
          src={resolveAssetUrl(product.coverImageUrl)}
          alt={product.name}
        />
      ) : (
        <div className={styles.placeholder}>use autêntica</div>
      )}
      <div className={styles.body}>
        <span className={styles.category}>{product.categoryName}</span>
        <h3 className={styles.name}>{product.name}</h3>
        <p className={styles.price}>{formatPrice(product.price)}</p>
      </div>
    </Link>
  );
}
