import { useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { fetchProducts } from "../api/products";
import { fetchCategories } from "../api/categories";
import { fetchSizes } from "../api/sizes";
import { fetchColors } from "../api/colors";
import { useAsync } from "../hooks/useAsync";
import { LoadingIndicator } from "../components/common/LoadingIndicator";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { SectionTitle } from "../components/common/SectionTitle";
import { ProductCard } from "../components/product/ProductCard";
import styles from "./Catalog.module.css";

type SortOption = "none" | "price-asc" | "price-desc";

export function Catalog() {
  const [searchParams, setSearchParams] = useSearchParams();
  const categoryId = searchParams.get("categoriaId") ?? "";
  const sizeId = searchParams.get("tamanhoId") ?? "";
  const colorId = searchParams.get("corId") ?? "";
  const [sort, setSort] = useState<SortOption>("none");

  const products = useAsync(
    () => fetchProducts({ categoryId, sizeId, colorId }),
    [categoryId, sizeId, colorId],
  );
  const categories = useAsync(() => fetchCategories(), []);
  const sizes = useAsync(() => fetchSizes(), []);
  const colors = useAsync(() => fetchColors(), []);

  const sortedProducts = useMemo(() => {
    if (!products.data) return [];
    if (sort === "price-asc") return [...products.data].sort((a, b) => a.price - b.price);
    if (sort === "price-desc") return [...products.data].sort((a, b) => b.price - a.price);
    return products.data;
  }, [products.data, sort]);

  function updateFilter(key: "categoriaId" | "tamanhoId" | "corId", value: string) {
    const next = new URLSearchParams(searchParams);
    if (value) {
      next.set(key, value);
    } else {
      next.delete(key);
    }
    setSearchParams(next);
  }

  return (
    <div className={styles.page}>
      <SectionTitle eyebrow="Coleção completa" as="h1">
        Catálogo
      </SectionTitle>

      <div className={styles.filters}>
        <label className={styles.field}>
          <span className={styles.fieldLabel}>Categoria</span>
          <select
            className={styles.select}
            value={categoryId}
            onChange={(event) => updateFilter("categoriaId", event.target.value)}
          >
            <option value="">Todas</option>
            {categories.data
              ?.filter((category) => category.active)
              .map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
          </select>
        </label>

        <label className={styles.field}>
          <span className={styles.fieldLabel}>Tamanho</span>
          <select
            className={styles.select}
            value={sizeId}
            onChange={(event) => updateFilter("tamanhoId", event.target.value)}
          >
            <option value="">Todos</option>
            {sizes.data
              ?.filter((size) => size.active)
              .map((size) => (
                <option key={size.id} value={size.id}>
                  {size.name}
                </option>
              ))}
          </select>
        </label>

        <label className={styles.field}>
          <span className={styles.fieldLabel}>Cor</span>
          <select
            className={styles.select}
            value={colorId}
            onChange={(event) => updateFilter("corId", event.target.value)}
          >
            <option value="">Todas</option>
            {colors.data
              ?.filter((color) => color.active)
              .map((color) => (
                <option key={color.id} value={color.id}>
                  {color.name}
                </option>
              ))}
          </select>
        </label>

        <label className={styles.field}>
          <span className={styles.fieldLabel}>Ordenar</span>
          <select
            className={styles.select}
            value={sort}
            onChange={(event) => setSort(event.target.value as SortOption)}
          >
            <option value="none">Relevância</option>
            <option value="price-asc">Menor preço</option>
            <option value="price-desc">Maior preço</option>
          </select>
        </label>
      </div>

      {products.loading && <LoadingIndicator />}
      {products.error && <ErrorMessage />}
      {products.data && sortedProducts.length === 0 && (
        <p className="status-message">Nenhum produto encontrado com esses filtros.</p>
      )}
      {sortedProducts.length > 0 && (
        <>
          <p className={styles.resultCount}>
            {sortedProducts.length} {sortedProducts.length === 1 ? "peça" : "peças"}
          </p>
          <div className={styles.grid}>
            {sortedProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        </>
      )}
    </div>
  );
}
