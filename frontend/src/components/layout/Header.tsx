import { Link } from "react-router-dom";
import logo from "../../assets/logo.png";
import { buildWhatsAppContactLink } from "../../utils/whatsapp";
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
          href={buildWhatsAppContactLink()}
          target="_blank"
          rel="noopener noreferrer"
          className={styles.iconLink}
          aria-label="WhatsApp da use autêntica"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12.04 2C6.58 2 2.13 6.45 2.13 11.91c0 1.75.46 3.45 1.32 4.95L2 22l5.25-1.38a9.9 9.9 0 0 0 4.79 1.22h.01c5.46 0 9.91-4.45 9.91-9.91C21.96 6.45 17.5 2 12.04 2zm5.8 14.18c-.24.68-1.2 1.26-1.97 1.42-.53.11-1.21.2-3.51-.75-2.95-1.22-4.85-4.21-5-4.4-.14-.2-1.19-1.58-1.19-3.02 0-1.43.75-2.14 1.02-2.43.27-.29.58-.36.78-.36.19 0 .39 0 .56.01.18.01.42-.07.66.5.24.58.83 2.01.9 2.16.07.14.12.31.02.51-.1.2-.15.32-.29.49-.15.17-.31.38-.44.51-.14.14-.29.3-.13.58.17.29.74 1.22 1.59 1.98 1.09.97 2.01 1.27 2.3 1.42.29.14.45.12.62-.07.17-.2.71-.83.9-1.12.19-.29.38-.24.64-.14.26.09 1.68.79 1.97.94.29.14.48.22.55.34.07.12.07.7-.17 1.38z" />
          </svg>
        </a>
        <a
          href={INSTAGRAM_URL}
          target="_blank"
          rel="noopener noreferrer"
          className={styles.iconLink}
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
