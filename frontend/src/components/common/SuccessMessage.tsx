interface SuccessMessageProps {
  message: string;
}

export function SuccessMessage({ message }: SuccessMessageProps) {
  return <p className="status-message status-message--success">{message}</p>;
}
