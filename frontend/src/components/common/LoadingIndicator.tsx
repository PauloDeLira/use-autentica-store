import styles from "./LoadingIndicator.module.css";

export function LoadingIndicator() {
  return (
    <div className={styles.wrapper}>
      <span className={styles.spinner} aria-hidden="true" />
      <span className={styles.label}>Carregando</span>
    </div>
  );
}
