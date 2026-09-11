import { createContext, useContext, useState, type ReactNode } from "react";
import { login as loginRequest } from "../api/auth";
import { clearSession, getStoredSession, storeSession, type AdminSession } from "./session";

interface AuthContextValue {
  session: AdminSession | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AdminSession | null>(() => getStoredSession());

  async function login(email: string, password: string) {
    const newSession = await loginRequest(email, password);
    storeSession(newSession);
    setSession(newSession);
  }

  function logout() {
    clearSession();
    setSession(null);
  }

  return <AuthContext.Provider value={{ session, login, logout }}>{children}</AuthContext.Provider>;
}

// oxlint-disable-next-line react/only-export-components -- hook belongs next to the context/provider it reads
export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve ser usado dentro de um AuthProvider");
  }
  return context;
}
