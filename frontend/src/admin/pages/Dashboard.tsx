import { fetchAdminProducts } from "../../api/admin/products";
import { fetchInventory } from "../../api/admin/inventory";
import { useAsync } from "../../hooks/useAsync";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import styles from "./Dashboard.module.css";

export function Dashboard() {
  const products = useAsync(() => fetchAdminProducts(), []);
  const inventory = useAsync(() => fetchInventory(), []);

  if (products.loading || inventory.loading) {
    return <LoadingIndicator />;
  }

  if (products.error || inventory.error) {
    return <ErrorMessage />;
  }

  const productList = products.data ?? [];
  const inventoryList = inventory.data ?? [];

  const totalProdutos = productList.length;
  const produtosAtivos = productList.filter((product) => product.active).length;
  const produtosSemEstoque = productList.filter((product) => product.active && !product.available).length;
  const itensDisponiveis = inventoryList
    .filter((item) => item.available)
    .reduce((sum, item) => sum + item.stockQuantity, 0);

  return (
    <div>
      <div className={styles.header}>
        <span className={styles.eyebrow}>Visão geral da loja</span>
        <h1 className={styles.title}>Dashboard</h1>
      </div>

      <div className={styles.grid}>
        <div className={styles.card}>
          <div className={styles.value}>{totalProdutos}</div>
          <div className={styles.label}>Total de produtos</div>
        </div>
        <div className={styles.card}>
          <div className={styles.value}>{produtosAtivos}</div>
          <div className={styles.label}>Produtos ativos</div>
        </div>
        <div className={`${styles.card} ${produtosSemEstoque > 0 ? styles.cardAlert : ""}`}>
          <div className={styles.value}>{produtosSemEstoque}</div>
          <div className={styles.label}>Produtos sem estoque</div>
        </div>
        <div className={styles.card}>
          <div className={styles.value}>{itensDisponiveis}</div>
          <div className={styles.label}>Itens disponíveis</div>
        </div>
      </div>
    </div>
  );
}
