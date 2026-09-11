import { useState, type FormEvent } from "react";
import { deleteImage, uploadImage } from "../../api/admin/images";
import { resolveAssetUrl, ApiError } from "../../api/client";
import { ErrorMessage } from "../../components/common/ErrorMessage";
import type { ProductImage } from "../../types/product";
import { FileInput } from "./FileInput";
import styles from "./ImageManager.module.css";

interface ImageManagerProps {
  productId: string;
  images: ProductImage[];
  onChanged: () => void;
}

export function ImageManager({ productId, images, onChanged }: ImageManagerProps) {
  const [file, setFile] = useState<File | null>(null);
  const [altText, setAltText] = useState("");
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleUpload(event: FormEvent) {
    event.preventDefault();
    if (!file) return;

    setUploading(true);
    setError(null);
    try {
      await uploadImage(productId, file, altText || undefined);
      setFile(null);
      setAltText("");
      onChanged();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível enviar a imagem.");
    } finally {
      setUploading(false);
    }
  }

  async function handleRemove(imageId: string) {
    if (!window.confirm("Remover esta imagem?")) return;
    setError(null);
    try {
      await deleteImage(productId, imageId);
      onChanged();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Não foi possível remover a imagem.");
    }
  }

  return (
    <div>
      <div className={styles.grid}>
        {images.map((image) => (
          <div key={image.id} className={styles.item}>
            <img className={styles.thumb} src={resolveAssetUrl(image.url)} alt={image.altText ?? ""} />
            <button type="button" className={styles.removeButton} onClick={() => handleRemove(image.id)}>
              ×
            </button>
          </div>
        ))}
      </div>

      <form className={styles.form} onSubmit={handleUpload}>
        <div className={styles.field}>
          <label htmlFor="product-image-file">Arquivo</label>
          <FileInput
            id="product-image-file"
            accept="image/jpeg,image/png,image/webp"
            value={file}
            onChange={setFile}
            required
          />
        </div>

        <label className={styles.field}>
          Texto alternativo
          <input value={altText} onChange={(event) => setAltText(event.target.value)} />
        </label>

        <button type="submit" className={styles.uploadButton} disabled={uploading || !file}>
          {uploading ? "Enviando..." : "Adicionar imagem"}
        </button>
      </form>

      {error && <ErrorMessage message={error} />}
    </div>
  );
}
