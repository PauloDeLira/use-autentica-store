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
  const [pendingValues, setPendingValues] = useState<Record<string, string>>({});
  const [savingId, setSavingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function handleSave(variantId: string) {
    const value = pendingValues[variantId];
    if (value === undefined) return;

    setSavingId(variantId);
    setError(null);
    try {
      await updateStock(variantId, Number(value) || 0);
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
      <div className={styles.header}>
        <span className={styles.eyebrow}>Controle por variação</span>
        <h1 className={styles.title}>Estoque</h1>
      </div>

      {error && <ErrorMessage message={error} />}
      {inventory.loading && <LoadingIndicator />}
      {inventory.error && <ErrorMessage />}
      {inventory.data && (
        <div className={styles.panel}>
          {inventory.data.length === 0 ? (
            <p className={styles.empty}>Nenhuma variação cadastrada ainda.</p>
          ) : (
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
                  const currentValue = pendingValues[item.id] ?? String(item.stockQuantity);
                  const isDirty =
                    pendingValues[item.id] !== undefined &&
                    (Number(pendingValues[item.id]) || 0) !== item.stockQuantity;

                  return (
                    <tr key={item.id}>
                      <td className={styles.productName}>{item.productName}</td>
                      <td className={styles.sizeCell}>{item.size.name}</td>
                      <td>
                        <span className={styles.swatch} style={{ backgroundColor: item.color.hexCode }} />
                        {item.color.name}
                      </td>
                      <td>
                        <div className={styles.stockCell}>
                          <input
                            type="number"
                            min={0}
                            className={styles.stockInput}
                            value={currentValue}
                            onChange={(event) =>
                              setPendingValues((prev) => ({ ...prev, [item.id]: event.target.value }))
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
                        </div>
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
      )}
    </div>
  );
}
