import { Link } from "react-router-dom";

export function NotFound() {
  return (
    <div className="status-message">
      <p>Página não encontrada.</p>
      <Link to="/">Voltar para a home</Link>
    </div>
  );
}
