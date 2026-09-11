import { Link } from "react-router-dom";
import styles from "./Footer.module.css";

const INSTAGRAM_URL = "https://www.instagram.com/_useautentica_/";

export function Footer() {
  return (
    <footer className={styles.footer}>
      <p className={styles.brand}>use autêntica</p>
      <p className={styles.tagline}>moda feminina</p>

      <a
        href={INSTAGRAM_URL}
        target="_blank"
        rel="noopener noreferrer"
        className={styles.instagramLink}
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
          <rect x="3" y="3" width="18" height="18" rx="5" />
          <circle cx="12" cy="12" r="4" />
          <circle cx="17.2" cy="6.8" r="1" fill="currentColor" stroke="none" />
        </svg>
        @_useautentica_
      </a>

      <div className={styles.ornament} aria-hidden="true">
        <span className={styles.line} />
        <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
        </svg>
        <span className={styles.line} />
      </div>

      <p className={styles.signature}>Autêntica em cada escolha.</p>

      <Link to="/admin/login" className={styles.adminLink}>
        Área administrativa
      </Link>
    </footer>
  );
}
