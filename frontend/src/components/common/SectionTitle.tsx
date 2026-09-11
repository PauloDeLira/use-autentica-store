import type { ReactNode } from "react";
import styles from "./SectionTitle.module.css";

interface SectionTitleProps {
  children: ReactNode;
  eyebrow?: string;
  as?: "h1" | "h2";
}

export function SectionTitle({ children, eyebrow, as: Tag = "h2" }: SectionTitleProps) {
  return (
    <div className={styles.wrapper}>
      {eyebrow && <span className={styles.eyebrow}>{eyebrow}</span>}
      <Tag className={styles.title}>{children}</Tag>
      <span className={styles.ornament} aria-hidden="true">
        <span className={styles.line} />
        <svg width="13" height="13" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
        </svg>
        <span className={styles.line} />
      </span>
    </div>
  );
}
