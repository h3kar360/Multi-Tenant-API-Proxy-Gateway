import { createContext, useContext, useState } from "react";
import type { ReactNode } from "react";

interface AuthContextType {
  token: string | null;
  clientName: string | null;
  isLoggedIn: boolean;
  login: (accessToken: string, clientName: string) => void;
  logout: () => void;
  refresh: (accessToken: string) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(null);
  const [clientName, setClientName] = useState<string | null>(null);

  const login = (accessToken: string, clientName: string): void => {
    setToken(accessToken);
    setClientName(clientName);
  };

  const refresh = (accessToken: string) => {
    setToken(accessToken);
  };

  const logout = (): void => {
    setToken(null);
    setClientName(null);
  };

  return (
    <AuthContext.Provider
      value={{ token, clientName, isLoggedIn: !!token, login, logout, refresh }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
