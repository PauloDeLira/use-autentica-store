import { Link } from "react-router-dom";
import styles from "./Header.module.css";

export function Header() {
  return (
    <header className={styles.header}>
      <Link to="/" className={styles.logo}>
        use autêntica
        <span className={styles.tagline}>moda feminina</span>
      </Link>
      <nav className={styles.nav}>
        <Link to="/">Início</Link>
        <Link to="/catalogo">Catálogo</Link>
      </nav>
    </header>
  );
}
