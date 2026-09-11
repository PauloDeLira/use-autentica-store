import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { fetchCategories } from "../../api/categories";
import { createProduct, fetchAdminProduct, updateProduct } from "../../api/admin/products";
import { uploadImage } from "../../api/admin/images";
import { useAsync } from "../../hooks/useAsync";
import { ApiError } from "../../api/client";
import { LoadingIndicator } from "../../components/common/LoadingIndicator";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import { SuccessMessage } from "../../components/common/SuccessMessage";
import { VariantManager } from "../components/VariantManager";
import { ImageManager } from "../components/ImageManager";
import styles from "./ProductForm.module.css";

export function ProductForm() {
  const { id } = useParams<{ id: string }>();
  const isEditing = Boolean(id);
  const navigate = useNavigate();

  const [reloadKey, setReloadKey] = useState(0);
  const product = useAsync(
    () => (id ? fetchAdminProduct(id) : Promise.resolve(null)),
    [id, reloadKey],
  );
  const categories = useAsync(() => fetchCategories(), []);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [newProductImage, setNewProductImage] = useState<File | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (product.data) {
      // oxlint-disable-next-line react/set-state-in-effect -- seeds editable fields once from fetched data, not derived state
      setName(product.data.name);
      setDescription(product.data.description ?? "");
      setPrice(String(product.data.price));
      setCategoryId(product.data.category.id);
    }
  }, [product.data]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);
    const payload = {
      name,
      description: description || null,
      price: Number(price),
      categoryId,
    };

    try {
      if (id) {
        await updateProduct(id, payload);
        setSuccess("Produto atualizado.");
        setReloadKey((key) => key + 1);
      } else {
        const created = await createProduct(payload);
        if (newProductImage) {
          try {
            await uploadImage(created.id, newProductImage);
          } catch {
            // produto já foi criado; a foto pode ser adicionada na tela de edição
          }
        }
        navigate(`/admin/produtos/${created.id}/editar`);
      }
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível salvar o produto.");
    } finally {
      setSaving(false);
    }
  }

  if (isEditing && product.loading) {
    return <LoadingIndicator />;
  }

  if (isEditing && product.error) {
    return <ErrorMessage message="Produto não encontrado." />;
  }

  return (
    <div>
      <div className={styles.header}>
        <Link to="/admin/produtos" className={styles.backLink}>
          ← Voltar para produtos
        </Link>
        <h1 className={styles.title}>{isEditing ? "Editar produto" : "Novo produto"}</h1>
      </div>

      <form className={styles.form} onSubmit={handleSubmit}>
        <label className={styles.field}>
          Nome
          <input value={name} onChange={(event) => setName(event.target.value)} required maxLength={150} />
        </label>

        <label className={styles.field}>
          Descrição
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            maxLength={2000}
            rows={4}
          />
        </label>

        <label className={styles.field}>
          Preço
          <input
            type="number"
            min={0.01}
            step={0.01}
            value={price}
            onChange={(event) => setPrice(event.target.value)}
            required
          />
        </label>

        <label className={styles.field}>
          Categoria
          <select value={categoryId} onChange={(event) => setCategoryId(event.target.value)} required>
            <option value="">Selecione</option>
            {categories.data?.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
                {!category.active ? " (inativa)" : ""}
              </option>
            ))}
          </select>
        </label>

        {!isEditing && (
          <label className={styles.field}>
            Foto (opcional)
            <input
              type="file"
              accept="image/jpeg,image/png,image/webp"
              onChange={(event) => setNewProductImage(event.target.files?.[0] ?? null)}
            />
          </label>
        )}

        <button type="submit" className={styles.submit} disabled={saving}>
          {saving ? "Salvando..." : isEditing ? "Salvar alterações" : "Criar produto"}
        </button>

        {error && <ErrorMessage message={error} />}
        {success && <SuccessMessage message={success} />}
      </form>

      {isEditing && id && product.data && (
        <>
          <section className={styles.section}>
            <h2 className={styles.sectionTitle}>Variações</h2>
            <VariantManager productId={id} />
          </section>

          <section className={styles.section}>
            <h2 className={styles.sectionTitle}>Imagens</h2>
            <ImageManager
              productId={id}
              images={product.data.images}
              onChanged={() => setReloadKey((key) => key + 1)}
            />
          </section>
        </>
      )}

      {!isEditing && (
        <p className={styles.hint}>
          Salve o produto primeiro para poder cadastrar variações e mais imagens.
        </p>
      )}
    </div>
  );
}
