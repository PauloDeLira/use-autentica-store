import { Link } from "react-router-dom";
import logo from "../../assets/use-autentica-logo.png";
import styles from "./Header.module.css";

export function Header() {
  return (
    <header className={styles.header}>
      <Link to="/" className={styles.logo}>
        <img src={logo} alt="use autêntica — moda feminina" className={styles.logoImage} />
      </Link>
      <nav className={styles.nav}>
        <Link to="/">Início</Link>
        <Link to="/catalogo">Catálogo</Link>
      </nav>
    </header>
  );
}
