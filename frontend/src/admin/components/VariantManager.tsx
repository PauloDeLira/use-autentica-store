import { useState, type FormEvent } from "react";
import { fetchSizes } from "../../api/sizes";
import { fetchColors } from "../../api/colors";
import { fetchInventory } from "../../api/admin/inventory";
import { createVariant, deleteVariant } from "../../api/admin/variants";
import { useAsync } from "../../hooks/useAsync";
import { ApiError } from "../../api/client";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import styles from "./VariantManager.module.css";

interface VariantManagerProps {
  productId: string;
}

export function VariantManager({ productId }: VariantManagerProps) {
  const [reloadKey, setReloadKey] = useState(0);
  const inventory = useAsync(() => fetchInventory(), [reloadKey]);
  const sizes = useAsync(() => fetchSizes(), []);
  const colors = useAsync(() => fetchColors(), []);

  const [sizeId, setSizeId] = useState("");
  const [colorId, setColorId] = useState("");
  const [stockQuantity, setStockQuantity] = useState(0);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const variants = (inventory.data ?? []).filter((item) => item.productId === productId);

  async function handleAdd(event: FormEvent) {
    event.preventDefault();
    if (!sizeId || !colorId) return;

    setSaving(true);
    setError(null);
    try {
      await createVariant(productId, { sizeId, colorId, stockQuantity });
      setSizeId("");
      setColorId("");
      setStockQuantity(0);
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível criar a variação.");
    } finally {
      setSaving(false);
    }
  }

  async function handleRemove(variantId: string) {
    if (!window.confirm("Remover esta variação?")) return;
    setError(null);
    try {
      await deleteVariant(productId, variantId);
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível remover a variação.");
    }
  }

  return (
    <div>
      {inventory.loading && <LoadingIndicator />}
      {inventory.error && <ErrorMessage />}
      {inventory.data && (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Tamanho</th>
              <th>Cor</th>
              <th>Estoque</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {variants.length === 0 && (
              <tr>
                <td colSpan={5}>Nenhuma variação cadastrada.</td>
              </tr>
            )}
            {variants.map((variant) => (
              <tr key={variant.id}>
                <td>{variant.size.name}</td>
                <td>
                  <span className={styles.swatch} style={{ backgroundColor: variant.color.hexCode }} />
                  {variant.color.name}
                </td>
                <td>{variant.stockQuantity}</td>
                <td>
                  <span className={`${styles.badge} ${variant.active ? "" : styles.badgeInactive}`}>
                    {variant.active ? "Ativa" : "Inativa"}
                  </span>
                </td>
                <td>
                  {variant.active && (
                    <button type="button" className={styles.linkButton} onClick={() => handleRemove(variant.id)}>
                      Remover
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <form className={styles.form} onSubmit={handleAdd}>
        <label className={styles.field}>
          Tamanho
          <select value={sizeId} onChange={(event) => setSizeId(event.target.value)} required>
            <option value="">Selecione</option>
            {sizes.data
              ?.filter((size) => size.active)
              .map((size) => (
                <option key={size.id} value={size.id}>
                  {size.name}
                </option>
              ))}
          </select>
        </label>

        <label className={styles.field}>
          Cor
          <select value={colorId} onChange={(event) => setColorId(event.target.value)} required>
            <option value="">Selecione</option>
            {colors.data
              ?.filter((color) => color.active)
              .map((color) => (
                <option key={color.id} value={color.id}>
                  {color.name}
                </option>
              ))}
          </select>
        </label>

        <label className={styles.field}>
          Estoque
          <input
            type="number"
            min={0}
            value={stockQuantity}
            onChange={(event) => setStockQuantity(Number(event.target.value))}
            required
          />
        </label>

        <button type="submit" className={styles.addButton} disabled={saving}>
          {saving ? "Adicionando..." : "Adicionar variação"}
        </button>
      </form>

      {error && <ErrorMessage message={error} />}
    </div>
  );
}
