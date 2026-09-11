import { useState, type FormEvent } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";
import { ApiError } from "../../api/client";
import logo from "../../assets/logo.png";
import styles from "./Login.module.css";

export function Login() {
  const { session, login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (session) {
    return <Navigate to="/admin" replace />;
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(email, password);
      navigate("/admin");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível fazer login.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className={styles.wrapper}>
      <form className={styles.card} onSubmit={handleSubmit}>
        <img src={logo} alt="use autêntica — moda feminina" className={styles.logo} />
        <p className={styles.title}>Área administrativa</p>

        <label className={styles.field}>
          Email
          <input
            type="email"
            required
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </label>

        <label className={styles.field}>
          Senha
          <input
            type="password"
            required
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </label>

        <button type="submit" className={styles.submit} disabled={loading}>
          {loading ? "Entrando..." : "Entrar"}
        </button>

        {error && <p className={styles.error}>{error}</p>}

        <Link to="/" className={styles.backLink}>
          ← Voltar ao site
        </Link>
      </form>
    </div>
  );
}
