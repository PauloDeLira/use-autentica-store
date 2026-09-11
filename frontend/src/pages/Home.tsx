import { Link } from "react-router-dom";
import { fetchCategories } from "../api/categories";
import { fetchProducts } from "../api/products";
import { useAsync } from "../hooks/useAsync";
import { LoadingIndicator } from "../components/common/LoadingIndicator";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { SectionTitle } from "../components/common/SectionTitle";
import { ProductCard } from "../components/product/ProductCard";
import logo from "../assets/logo.png";
import logoLight from "../assets/logo-clara.png";
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
        <div className={styles.heroContent}>
          <img src={logo} alt="use autêntica — moda feminina" className={styles.heroLogo} />
          <h1 className={styles.heroTitle}>
            <span>Moda que valoriza</span>
            <span>o que te torna única</span>
          </h1>
          <p className={styles.heroSubtitle}>Elegância em cada detalhe.</p>
          <Link to="/catalogo" className={styles.heroCta}>
            Ver catálogo
          </Link>
        </div>
        <div className={styles.heroVisual} aria-hidden="true">
          <div className={styles.heroVisualFrame}>
            <img src={logoLight} alt="" className={styles.heroVisualLogo} />
            <span className={styles.heroVisualCaption}>Autêntica em cada escolha</span>
          </div>
        </div>
      </section>

      <section className={styles.section}>
        <SectionTitle eyebrow="Navegue por">Categorias</SectionTitle>
        {categories.loading && <LoadingIndicator />}
        {categories.error && <ErrorMessage />}
        {categories.data && activeCategories.length === 0 && (
          <p className="status-message">Nenhuma categoria cadastrada ainda.</p>
        )}
        {activeCategories.length > 0 && (
          <ul className={styles.categoryList}>
            {activeCategories.map((category) => (
              <li key={category.id} className={styles.categoryItem}>
                <Link to={`/catalogo?categoriaId=${category.id}`}>{category.name}</Link>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className={`${styles.section} ${styles.sectionAlt}`}>
        <div className={styles.sectionInner}>
          <SectionTitle eyebrow="Selecionados para você">Destaques</SectionTitle>
          {products.loading && <LoadingIndicator />}
          {products.error && <ErrorMessage />}
          {products.data && featuredProducts.length === 0 && (
            <p className="status-message">Nenhum produto cadastrado ainda.</p>
          )}
          {featuredProducts.length > 0 && (
            <>
              <div className={styles.productGrid}>
                {featuredProducts.map((product) => (
                  <ProductCard key={product.id} product={product} />
                ))}
              </div>
              <div className={styles.sectionFooter}>
                <Link to="/catalogo" className={styles.ghostLink}>
                  Ver catálogo completo
                </Link>
              </div>
            </>
          )}
        </div>
      </section>
    </>
  );
}
