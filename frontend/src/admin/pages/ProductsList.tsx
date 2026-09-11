import { useState } from "react";
import { Link } from "react-router-dom";
import { deleteProduct, fetchAdminProducts, setProductActive } from "../../api/admin/products";
import { useAsync } from "../../hooks/useAsync";
import { ApiError } from "../../api/client";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import { formatPrice } from "../../utils/currency";
import styles from "./ProductsList.module.css";

export function ProductsList() {
  const [reloadKey, setReloadKey] = useState(0);
  const products = useAsync(() => fetchAdminProducts(), [reloadKey]);
  const [error, setError] = useState<string | null>(null);

  async function handleToggleActive(id: string, active: boolean) {
    setError(null);
    try {
      await setProductActive(id, !active);
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível alterar o produto.");
    }
  }

  async function handleDelete(id: string) {
    if (!window.confirm("Excluir este produto? Essa ação não pode ser desfeita.")) {
      return;
    }
    setError(null);
    try {
      await deleteProduct(id);
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível excluir o produto.");
    }
  }

  return (
    <div>
      <div className={styles.header}>
        <h1 className={styles.title}>Produtos</h1>
        <Link to="/admin/produtos/novo" className={styles.newButton}>
          Novo produto
        </Link>
      </div>

      {error && <ErrorMessage message={error} />}
      {products.loading && <LoadingIndicator />}
      {products.error && <ErrorMessage />}
      {products.data && (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Categoria</th>
              <th>Preço</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {products.data.map((product) => (
              <tr key={product.id}>
                <td>{product.name}</td>
                <td>{product.categoryName}</td>
                <td>{formatPrice(product.price)}</td>
                <td>
                  <span className={`${styles.badge} ${product.active ? "" : styles.badgeInactive}`}>
                    {product.active ? "Ativo" : "Inativo"}
                  </span>
                </td>
                <td>
                  <div className={styles.actions}>
                    <Link to={`/admin/produtos/${product.id}/editar`} className={styles.linkButton}>
                      Editar
                    </Link>
                    <button
                      type="button"
                      className={styles.linkButton}
                      onClick={() => handleToggleActive(product.id, product.active)}
                    >
                      {product.active ? "Desativar" : "Ativar"}
                    </button>
                    <button
                      type="button"
                      className={`${styles.linkButton} ${styles.linkButtonDanger}`}
                      onClick={() => handleDelete(product.id)}
                    >
                      Excluir
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
