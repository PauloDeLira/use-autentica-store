import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { fetchProductById, fetchProductVariants } from "../api/products";
import { useAsync } from "../hooks/useAsync";
import { ApiError } from "../api/client";
import { LoadingIndicator } from "../components/common/LoadingIndicator";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { ImageGallery } from "../components/product/ImageGallery";
import { VariantList } from "../components/product/VariantList";
import { PurchaseWhatsApp } from "../components/product/PurchaseWhatsApp";
import { formatPrice } from "../utils/currency";
import type { ProductVariantSummary } from "../types/variant";
import styles from "./ProductDetail.module.css";

export function ProductDetail() {
  const { id = "" } = useParams<{ id: string }>();
  const [selectedVariant, setSelectedVariant] = useState<ProductVariantSummary | null>(null);

  const product = useAsync(() => fetchProductById(id), [id]);
  const variants = useAsync(() => fetchProductVariants(id), [id]);
  const availableVariants = variants.data?.filter((variant) => variant.available) ?? [];

  // Com uma única opção disponível não há escolha real a fazer — seleciona
  // automaticamente pra não exigir um clique extra antes de comprar.
  useEffect(() => {
    if (availableVariants.length === 1 && !selectedVariant) {
      setSelectedVariant(availableVariants[0]);
    }
    // oxlint-disable-next-line react-hooks/exhaustive-deps -- so deve reagir a novos dados de variacao, nao a selectedVariant
  }, [variants.data]);

  if (product.loading) {
    return <LoadingIndicator />;
  }

  if (product.error) {
    const notFound = product.error instanceof ApiError && product.error.status === 404;
    return <ErrorMessage message={notFound ? "Produto não encontrado." : undefined} />;
  }

  const data = product.data!;
  const hasAvailableVariant = availableVariants.length > 0;

  return (
    <div className={styles.wrapper}>
      <ImageGallery images={data.images} productName={data.name} />

      <div className={styles.info}>
        <Link to="/catalogo" className={styles.breadcrumb}>
          ← Voltar ao catálogo
        </Link>

        <div>
          <span className={styles.category}>{data.category.name}</span>
          <h1 className={styles.name}>{data.name}</h1>
          <p className={styles.price}>{formatPrice(data.price)}</p>
        </div>

        {data.description && (
          <>
            <div className={styles.divider} />
            <p className={styles.description}>{data.description}</p>
          </>
        )}

        <h2 className={styles.sectionTitle}>Tamanhos e cores</h2>
        {variants.loading && <LoadingIndicator />}
        {variants.error && <ErrorMessage />}
        {variants.data && !hasAvailableVariant && (
          <p className={styles.unavailable}>Produto indisponível no momento.</p>
        )}
        {variants.data && hasAvailableVariant && (
          <VariantList
            variants={variants.data}
            selectedVariantId={selectedVariant?.id ?? null}
            onSelect={setSelectedVariant}
          />
        )}

        {selectedVariant && (
          <PurchaseWhatsApp
            key={selectedVariant.id}
            productName={data.name}
            price={data.price}
            variant={selectedVariant}
          />
        )}
      </div>
    </div>
  );
}
