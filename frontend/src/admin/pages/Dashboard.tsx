import { Link } from "react-router-dom";
import { fetchAdminProducts } from "../../api/admin/products";
import { fetchInventory } from "../../api/admin/inventory";
import { fetchWhatsAppClickStats } from "../../api/admin/dashboard";
import { useAsync } from "../../hooks/useAsync";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import styles from "./Dashboard.module.css";

export function Dashboard() {
  const products = useAsync(() => fetchAdminProducts(), []);
  const inventory = useAsync(() => fetchInventory(), []);
  // Metrica de intencao de compra: cliques no WhatsApp nao sao venda (o
  // checkout acontece fora do site), mas e o unico sinal de interesse que
  // da pra medir. Carrega a parte, sem travar o resto do dashboard.
  const whatsappStats = useAsync(() => fetchWhatsAppClickStats(), []);

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
  const semEstoque = inventoryList.filter((item) => item.active && !item.available);

  const clicksToday = whatsappStats.data?.clicksToday ?? 0;
  const clicksThisWeek = whatsappStats.data?.clicksThisWeek ?? 0;
  const topProducts = whatsappStats.data?.topProducts ?? [];

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
        <div className={styles.card}>
          <div className={styles.value}>{clicksToday}</div>
          <div className={styles.label}>Cliques no WhatsApp hoje</div>
        </div>
        <div className={styles.card}>
          <div className={styles.value}>{clicksThisWeek}</div>
          <div className={styles.label}>Cliques no WhatsApp na semana</div>
        </div>
      </div>

      <div className={styles.secondary}>
        <div className={styles.panel}>
          <h2 className={styles.panelTitle}>Ações rápidas</h2>
          <div className={styles.actions}>
            <Link to="/admin/produtos/novo" className={styles.actionButton}>
              + Novo produto
            </Link>
            <Link to="/admin/estoque" className={styles.actionButtonGhost}>
              Ver estoque
            </Link>
            <Link to="/admin/categorias" className={styles.actionButtonGhost}>
              Gerenciar categorias
            </Link>
          </div>
        </div>

        <div className={styles.panel}>
          <h2 className={styles.panelTitle}>Sem estoque</h2>
          {semEstoque.length === 0 ? (
            <p className={styles.emptyState}>Nenhuma variação sem estoque no momento.</p>
          ) : (
            <ul className={styles.statList}>
              {semEstoque.slice(0, 5).map((item) => (
                <li key={item.id}>
                  <span>{item.productName}</span>
                  <span className={styles.statMeta}>
                    {item.size.name} · {item.color.name}
                  </span>
                </li>
              ))}
            </ul>
          )}
          {semEstoque.length > 0 && (
            <Link to="/admin/estoque" className={styles.panelLink}>
              Ver estoque completo
            </Link>
          )}
        </div>

        <div className={styles.panel}>
          <h2 className={styles.panelTitle}>Produtos mais clicados no WhatsApp</h2>
          {topProducts.length === 0 ? (
            <p className={styles.emptyState}>Ainda não há cliques registrados.</p>
          ) : (
            <ul className={styles.statList}>
              {topProducts.map((item) => (
                <li key={item.productId}>
                  <span>{item.productName}</span>
                  <span className={styles.statMeta}>{item.clicks} cliques</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
