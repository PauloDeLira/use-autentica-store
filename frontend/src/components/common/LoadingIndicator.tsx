import { useEffect, useState } from "react";
import styles from "./LoadingIndicator.module.css";

const SLOW_HINT_DELAY_MS = 4000;

export function LoadingIndicator() {
  const [slow, setSlow] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setSlow(true), SLOW_HINT_DELAY_MS);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className={styles.wrapper}>
      <span className={styles.spinner} aria-hidden="true" />
      <span className={styles.label}>Carregando</span>
      {slow && (
        <p className={styles.slowHint}>
          O servidor pode estar "acordando" depois de um tempo sem visitas — isso pode levar
          cerca de um minuto na primeira vez.
        </p>
      )}
    </div>
  );
}
