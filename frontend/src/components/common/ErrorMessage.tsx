interface ErrorMessageProps {
  message?: string;
}

export function ErrorMessage({ message }: ErrorMessageProps) {
  return (
    <p className="status-message status-message--error">
      {message ?? "Não foi possível carregar as informações. Tente novamente em instantes."}
    </p>
  );
}
