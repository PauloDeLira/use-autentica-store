import { useState } from "react";
import { fetchInventory, updateStock } from "../../api/admin/inventory";
import { useAsync } from "../../hooks/useAsync";
import { ApiError } from "../../api/client";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import styles from "./Inventory.module.css";

const LOW_STOCK_THRESHOLD = 5;

export function Inventory() {
  const [reloadKey, setReloadKey] = useState(0);
  const inventory = useAsync(() => fetchInventory(), [reloadKey]);
  const [pendingValues, setPendingValues] = useState<Record<string, number>>({});
  const [savingId, setSavingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function handleSave(variantId: string) {
    const value = pendingValues[variantId];
    if (value === undefined) return;

    setSavingId(variantId);
    setError(null);
    try {
      await updateStock(variantId, value);
      setPendingValues((prev) => {
        const next = { ...prev };
        delete next[variantId];
        return next;
      });
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível atualizar o estoque.");
    } finally {
      setSavingId(null);
    }
  }

  return (
    <div>
      <h1 className={styles.title}>Estoque</h1>

      {error && <ErrorMessage message={error} />}
      {inventory.loading && <LoadingIndicator />}
      {inventory.error && <ErrorMessage />}
      {inventory.data && (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Produto</th>
              <th>Tamanho</th>
              <th>Cor</th>
              <th>Estoque</th>
              <th>Situação</th>
            </tr>
          </thead>
          <tbody>
            {inventory.data.map((item) => {
              const currentValue = pendingValues[item.id] ?? item.stockQuantity;
              const isDirty = pendingValues[item.id] !== undefined && pendingValues[item.id] !== item.stockQuantity;

              return (
                <tr key={item.id}>
                  <td>{item.productName}</td>
                  <td>{item.size.name}</td>
                  <td>
                    <span className={styles.swatch} style={{ backgroundColor: item.color.hexCode }} />
                    {item.color.name}
                  </td>
                  <td>
                    <input
                      type="number"
                      min={0}
                      className={styles.stockInput}
                      value={currentValue}
                      onChange={(event) =>
                        setPendingValues((prev) => ({ ...prev, [item.id]: Number(event.target.value) }))
                      }
                    />
                    {isDirty && (
                      <button
                        type="button"
                        className={styles.saveButton}
                        disabled={savingId === item.id}
                        onClick={() => handleSave(item.id)}
                      >
                        Salvar
                      </button>
                    )}
                  </td>
                  <td>
                    {item.stockQuantity === 0 || !item.active ? (
                      <span className={`${styles.badge} ${styles.badgeWarning}`}>Sem estoque</span>
                    ) : item.stockQuantity <= LOW_STOCK_THRESHOLD ? (
                      <span className={`${styles.badge} ${styles.badgeLow}`}>Estoque baixo</span>
                    ) : (
                      <span className={styles.badge}>Disponível</span>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}
