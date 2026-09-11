import { Link } from "react-router-dom";
import styles from "./Footer.module.css";

export function Footer() {
  return (
    <footer className={styles.footer}>
      <p>Use Autêntica — moda feminina</p>
      <Link to="/admin/login" className={styles.adminLink}>
        Área administrativa
      </Link>
    </footer>
  );
}
