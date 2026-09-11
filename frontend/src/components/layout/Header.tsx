import { Link } from "react-router-dom";
import logo from "../../assets/logo.png";
import styles from "./Header.module.css";

const INSTAGRAM_URL = "https://www.instagram.com/_useautentica_/";

export function Header() {
  return (
    <header className={styles.header}>
      <Link to="/" className={styles.logo}>
        <img src={logo} alt="use autêntica — moda feminina" className={styles.logoImage} />
      </Link>
      <nav className={styles.nav}>
        <Link to="/">Início</Link>
        <Link to="/catalogo">Catálogo</Link>
        <a
          href={INSTAGRAM_URL}
          target="_blank"
          rel="noopener noreferrer"
          className={styles.instagramLink}
          aria-label="Instagram da use autêntica"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
            <rect x="3" y="3" width="18" height="18" rx="5" />
            <circle cx="12" cy="12" r="4" />
            <circle cx="17.2" cy="6.8" r="1" fill="currentColor" stroke="none" />
          </svg>
        </a>
      </nav>
    </header>
  );
}
