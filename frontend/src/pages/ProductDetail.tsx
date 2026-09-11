import { useParams } from "react-router-dom";
import { fetchProductById, fetchProductVariants } from "../api/products";
import { useAsync } from "../hooks/useAsync";
import { ApiError } from "../api/client";
import { LoadingIndicator } from "../components/common/LoadingIndicator";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { ImageGallery } from "../components/product/ImageGallery";
import { VariantList } from "../components/product/VariantList";
import { formatPrice } from "../utils/currency";
import styles from "./ProductDetail.module.css";

export function ProductDetail() {
  const { id = "" } = useParams<{ id: string }>();

  const product = useAsync(() => fetchProductById(id), [id]);
  const variants = useAsync(() => fetchProductVariants(id), [id]);

  if (product.loading) {
    return <LoadingIndicator />;
  }

  if (product.error) {
    const notFound = product.error instanceof ApiError && product.error.status === 404;
    return (
      <ErrorMessage
        message={notFound ? "Produto não encontrado." : undefined}
      />
    );
  }

  const data = product.data!;

  return (
    <div className={styles.wrapper}>
      <ImageGallery images={data.images} productName={data.name} />

      <div>
        <span className={styles.category}>{data.category.name}</span>
        <h1 className={styles.name}>{data.name}</h1>
        <p className={styles.price}>{formatPrice(data.price)}</p>
        {data.description && <p className={styles.description}>{data.description}</p>}

        <h2 className={styles.sectionTitle}>Tamanhos e cores disponíveis</h2>
        {variants.loading && <LoadingIndicator />}
        {variants.error && <ErrorMessage />}
        {variants.data && <VariantList variants={variants.data} />}
      </div>
    </div>
  );
}
