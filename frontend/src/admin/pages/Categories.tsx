import { useState, type FormEvent } from "react";
import { fetchCategories } from "../../api/categories";
import { createCategory, setCategoryActive, updateCategory } from "../../api/admin/categories";
import { useAsync } from "../../hooks/useAsync";
import { ApiError } from "../../api/client";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import { SuccessMessage } from "../../components/common/SuccessMessage";
import type { Category } from "../../types/category";
import styles from "./Categories.module.css";

export function Categories() {
  const [reloadKey, setReloadKey] = useState(0);
  const categories = useAsync(() => fetchCategories(), [reloadKey]);

  const [editing, setEditing] = useState<Category | null>(null);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  function startCreate() {
    setEditing(null);
    setName("");
    setDescription("");
    setError(null);
  }

  function startEdit(category: Category) {
    setEditing(category);
    setName(category.name);
    setDescription(category.description ?? "");
    setError(null);
    setSuccess(null);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const payload = { name, description: description || null };
      if (editing) {
        await updateCategory(editing.id, payload);
        setSuccess("Categoria atualizada.");
      } else {
        await createCategory(payload);
        setSuccess("Categoria criada.");
      }
      startCreate();
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível salvar a categoria.");
    } finally {
      setSaving(false);
    }
  }

  async function handleToggleActive(category: Category) {
    setError(null);
    try {
      await setCategoryActive(category.id, !category.active);
      setReloadKey((key) => key + 1);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível alterar a categoria.");
    }
  }

  return (
    <div>
      <div className={styles.header}>
        <span className={styles.eyebrow}>Organização do catálogo</span>
        <h1 className={styles.title}>Categorias</h1>
      </div>

      <div className={styles.layout}>
        <div>
          {categories.loading && <LoadingIndicator />}
          {categories.error && <ErrorMessage />}
          {categories.data && categories.data.length === 0 && (
            <div className={styles.panel}>
              <p className={styles.empty}>Nenhuma categoria cadastrada ainda.</p>
            </div>
          )}
          {categories.data && categories.data.length > 0 && (
            <div className={styles.panel}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {categories.data.map((category) => (
                  <tr key={category.id}>
                    <td className={styles.categoryName} data-label="Nome">{category.name}</td>
                    <td data-label="Status">
                      <span className={`${styles.badge} ${category.active ? "" : styles.badgeInactive}`}>
                        {category.active ? "Ativa" : "Inativa"}
                      </span>
                    </td>
                    <td data-label="Ações">
                      <div className={styles.actions}>
                        <button type="button" className={styles.linkButton} onClick={() => startEdit(category)}>
                          Editar
                        </button>
                        <button type="button" className={styles.linkButton} onClick={() => handleToggleActive(category)}>
                          {category.active ? "Desativar" : "Ativar"}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            </div>
          )}
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          <h2 className={styles.formTitle}>{editing ? "Editar categoria" : "Nova categoria"}</h2>

          <label className={styles.field}>
            Nome
            <input value={name} onChange={(event) => setName(event.target.value)} required maxLength={100} />
          </label>

          <label className={styles.field}>
            Descrição
            <textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              maxLength={255}
              rows={3}
            />
          </label>

          <div className={styles.formActions}>
            <button type="submit" className={styles.submit} disabled={saving}>
              {saving ? "Salvando..." : editing ? "Salvar alterações" : "Criar categoria"}
            </button>
            {editing && (
              <button type="button" className={styles.cancel} onClick={startCreate}>
                Cancelar
              </button>
            )}
          </div>

          {error && <ErrorMessage message={error} />}
          {success && <SuccessMessage message={success} />}
        </form>
      </div>
    </div>
  );
}
