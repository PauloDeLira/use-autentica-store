import { useEffect, useState } from "react";
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import logo from "../assets/logo-clara.png";
import styles from "./AdminLayout.module.css";

export function AdminLayout() {
  const { session, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    if (!menuOpen) return;

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === "Escape") setMenuOpen(false);
    }

    document.addEventListener("keydown", handleKeyDown);
    // Trava a rolagem do conteúdo atrás do menu enquanto ele está aberto.
    document.body.style.overflow = "hidden";

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      document.body.style.overflow = "";
    };
  }, [menuOpen]);

  function handleLogout() {
    logout();
    navigate("/admin/login");
  }

  return (
    <div className={styles.shell}>
      <header className={styles.topbar}>
        <button
          type="button"
          className={styles.menuButton}
          onClick={() => setMenuOpen(true)}
          aria-label="Abrir menu"
          aria-expanded={menuOpen}
          aria-controls="admin-sidebar"
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M3 6h18M3 12h18M3 18h18" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
          </svg>
        </button>
        <Link to="/" className={styles.topbarLogo}>
          <img src={logo} alt="use autêntica — moda feminina" className={styles.topbarLogoImage} />
        </Link>
      </header>

      {menuOpen && <div className={styles.backdrop} onClick={() => setMenuOpen(false)} />}

      <aside id="admin-sidebar" className={`${styles.sidebar} ${menuOpen ? styles.sidebarOpen : ""}`}>
        <div className={styles.sidebarHeader}>
          <Link to="/" className={styles.logo}>
            <img src={logo} alt="use autêntica — moda feminina" className={styles.logoImage} />
          </Link>
          <button
            type="button"
            className={styles.closeButton}
            onClick={() => setMenuOpen(false)}
            aria-label="Fechar menu"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden="true">
              <path d="M6 6l12 12M18 6L6 18" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
            </svg>
          </button>
        </div>

        <span className={styles.sidebarLabel}>Gerenciamento</span>
        <nav className={styles.nav} onClick={() => setMenuOpen(false)}>
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
