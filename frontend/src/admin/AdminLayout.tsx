import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
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
        <span className={styles.logo}>use autêntica</span>
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
        </nav>
        <button type="button" className={styles.logoutButton} onClick={handleLogout}>
          Sair ({session?.name})
        </button>
      </aside>
      <div className={styles.content}>
        <Outlet />
      </div>
    </div>
  );
}
