import styles from "./FileInput.module.css";

interface FileInputProps {
  id: string;
  accept?: string;
  required?: boolean;
  value: File | null;
  onChange: (file: File | null) => void;
}

export function FileInput({ id, accept, required, value, onChange }: FileInputProps) {
  return (
    <div className={styles.wrapper}>
      <input
        id={id}
        type="file"
        accept={accept}
        required={required}
        className={styles.hiddenInput}
        onChange={(event) => onChange(event.target.files?.[0] ?? null)}
      />
      <label htmlFor={id} className={styles.button}>
        Escolher arquivo
      </label>
      <span className={styles.fileName}>{value ? value.name : "Nenhum arquivo selecionado"}</span>
    </div>
  );
}
