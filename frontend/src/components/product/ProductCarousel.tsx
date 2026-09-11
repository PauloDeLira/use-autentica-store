import { useEffect, useRef, useState } from "react";
import type { ProductSummary } from "../../types/product";
import { ProductCard } from "./ProductCard";
import styles from "./ProductCarousel.module.css";

interface ProductCarouselProps {
  products: ProductSummary[];
}

export function ProductCarousel({ products }: ProductCarouselProps) {
  const trackRef = useRef<HTMLDivElement>(null);
  const [canScrollPrev, setCanScrollPrev] = useState(false);
  const [canScrollNext, setCanScrollNext] = useState(false);

  function updateScrollState() {
    const track = trackRef.current;
    if (!track) return;
    // O scroll-snap encaixa o primeiro card já no load, consumindo o
    // padding lateral como "rolado" — a tolerância acompanha esse padding
    // em vez de um valor fixo pequeno.
    const tolerance = Number.parseFloat(getComputedStyle(track).paddingLeft || "0") + 4;
    setCanScrollPrev(track.scrollLeft > tolerance);
    setCanScrollNext(track.scrollLeft + track.clientWidth < track.scrollWidth - tolerance);
  }

  useEffect(() => {
    updateScrollState();
    const track = trackRef.current;
    if (!track) return;

    const observer = new ResizeObserver(updateScrollState);
    observer.observe(track);
    window.addEventListener("resize", updateScrollState);
    return () => {
      observer.disconnect();
      window.removeEventListener("resize", updateScrollState);
    };
    // oxlint-disable-next-line react-hooks/exhaustive-deps -- só precisa reavaliar quando a lista de produtos muda
  }, [products]);

  function scrollByCards(direction: 1 | -1) {
    const track = trackRef.current;
    if (!track) return;
    const item = track.querySelector<HTMLElement>(`.${styles.item}`);
    const step = (item?.offsetWidth ?? 240) + 24;
    track.scrollBy({ left: direction * step * 2, behavior: "smooth" });
  }

  return (
    <div className={styles.wrapper}>
      <button
        type="button"
        className={`${styles.arrow} ${styles.arrowPrev}`}
        onClick={() => scrollByCards(-1)}
        disabled={!canScrollPrev}
        aria-label="Ver produtos anteriores"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
          <path d="M15 5l-7 7 7 7" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </button>

      <div className={styles.track} ref={trackRef} onScroll={updateScrollState}>
        {products.map((product) => (
          <div key={product.id} className={styles.item}>
            <ProductCard product={product} />
          </div>
        ))}
      </div>

      <button
        type="button"
        className={`${styles.arrow} ${styles.arrowNext}`}
        onClick={() => scrollByCards(1)}
        disabled={!canScrollNext}
        aria-label="Ver mais produtos"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
          <path d="M9 5l7 7-7 7" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </button>
    </div>
  );
}
