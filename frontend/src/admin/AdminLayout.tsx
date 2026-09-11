import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import logo from "../assets/logo-clara.png";
import styles from "./AdminLayout.module.css";

export function AdminLayout() {
  const { session, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/admin/login");
  }

  return (
    <div className={styles.shell}>
      <aside className={styles.sidebar}>
        <Link to="/" className={styles.logo}>
          <img src={logo} alt="use autêntica — moda feminina" className={styles.logoImage} />
        </Link>

        <span className={styles.sidebarLabel}>Gerenciamento</span>
        <nav className={styles.nav}>
          <NavLink to="/admin" end className={({ isActive }) => (isActive ? styles.navActive : undefined)}>
            Dashboard
          </NavLink>
          <NavLink to="/admin/produtos" className={({ isActive }) => (isActive ? styles.navActive : undefined)}>
            Produtos
          </NavLink>
          <NavLink to="/admin/categorias" className={({ isActive }) => (isActive ? styles.navActive : undefined)}>
            Categorias
          </NavLink>
          <NavLink to="/admin/estoque" className={({ isActive }) => (isActive ? styles.navActive : undefined)}>
            Estoque
          </NavLink>
          <Link to="/" className={styles.backToSiteLink}>
            ← Voltar ao site
          </Link>
        </nav>

        <div className={styles.user}>
          <span className={styles.userName}>{session?.name}</span>
          <span className={styles.userRole}>Administradora</span>
          <button type="button" className={styles.logoutButton} onClick={handleLogout}>
            Sair
          </button>
        </div>
      </aside>
      <div className={styles.content}>
        <Outlet />
      </div>
    </div>
  );
}
