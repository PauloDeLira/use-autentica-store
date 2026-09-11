import { useEffect, useState } from "react";

interface AsyncState<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
}

export function useAsync<T>(fetcher: () => Promise<T>, deps: unknown[]): AsyncState<T> {
  const [state, setState] = useState<AsyncState<T>>({ data: null, loading: true, error: null });

  // oxlint-disable-next-line react-hooks/exhaustive-deps -- deps is intentionally caller-supplied
  useEffect(() => {
    let active = true;
    // oxlint-disable-next-line react/set-state-in-effect -- resets state before an async fetch, not derived from props/state
    setState({ data: null, loading: true, error: null });

    fetcher()
      .then((data) => {
        if (active) setState({ data, loading: false, error: null });
      })
      .catch((error: Error) => {
        if (active) setState({ data: null, loading: false, error });
      });

    return () => {
      active = false;
    };
    // oxlint-disable-next-line react-hooks/exhaustive-deps -- deps is intentionally caller-supplied
  }, deps);

  return state;
}
