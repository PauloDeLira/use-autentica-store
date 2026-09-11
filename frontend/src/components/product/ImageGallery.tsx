import { useState } from "react";
import { resolveAssetUrl } from "../../api/client";
import type { ProductImage } from "../../types/product";
import styles from "./ImageGallery.module.css";

interface ImageGalleryProps {
  images: ProductImage[];
  productName: string;
}

export function ImageGallery({ images, productName }: ImageGalleryProps) {
  const sorted = [...images].sort((a, b) => a.displayOrder - b.displayOrder);
  const [activeIndex, setActiveIndex] = useState(0);

  if (sorted.length === 0) {
    return (
      <div className={styles.placeholder}>
        <span>UA</span>
      </div>
    );
  }

  const active = sorted[activeIndex];

  return (
    <div className={styles.gallery}>
      <div className={styles.mainFrame}>
        <img
          className={styles.main}
          src={resolveAssetUrl(active.url)}
          alt={active.altText ?? productName}
        />
      </div>
      {sorted.length > 1 && (
        <div className={styles.thumbnails}>
          {sorted.map((image, index) => (
            <img
              key={image.id}
              className={`${styles.thumbnail} ${index === activeIndex ? styles.thumbnailActive : ""}`}
              src={resolveAssetUrl(image.url)}
              alt={image.altText ?? productName}
              onClick={() => setActiveIndex(index)}
            />
          ))}
        </div>
      )}
    </div>
  );
}
