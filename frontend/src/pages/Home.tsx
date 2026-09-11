import { Link } from "react-router-dom";
import { fetchCategories } from "../api/categories";
import { fetchProducts } from "../api/products";
import { useAsync } from "../hooks/useAsync";
import { LoadingIndicator } from "../components/common/LoadingIndicator";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { ProductCard } from "../components/product/ProductCard";
import logo from "../assets/use-autentica-logo.png";
import styles from "./Home.module.css";

const FEATURED_COUNT = 4;

export function Home() {
  const categories = useAsync(() => fetchCategories(), []);
  const products = useAsync(() => fetchProducts(), []);
  const activeCategories = categories.data?.filter((category) => category.active) ?? [];
  const featuredProducts = products.data?.slice(0, FEATURED_COUNT) ?? [];

  return (
    <>
      <section className={styles.hero}>
        <h1 className={styles.heroTitle}>
          <img src={logo} alt="use autêntica — moda feminina" className={styles.heroLogo} />
        </h1>
        <p className={styles.heroSubtitle}>Elegância em cada detalhe.</p>
        <Link to="/catalogo" className={styles.cta}>
          Ver catálogo
        </Link>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Categorias</h2>
        {categories.loading && <LoadingIndicator />}
        {categories.error && <ErrorMessage />}
        {categories.data && (
          <ul className={styles.categoryList}>
            {activeCategories.map((category) => (
              <li key={category.id} className={styles.categoryItem}>
                <Link to={`/catalogo?categoriaId=${category.id}`}>{category.name}</Link>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Destaques</h2>
        {products.loading && <LoadingIndicator />}
        {products.error && <ErrorMessage />}
        {products.data && featuredProducts.length === 0 && (
          <p className="status-message">Nenhum produto cadastrado ainda.</p>
        )}
        {featuredProducts.length > 0 && (
          <div className={styles.productGrid}>
            {featuredProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </section>
    </>
  );
}
