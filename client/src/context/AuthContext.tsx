import { createContext, useContext, useState, useEffect } from "react";
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
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        const refreshAccessToken = async () => {
            try {
                const response = await fetch(
                    `${import.meta.env.VITE_API_URL}/auth/refresh`,
                    {
                        method: "POST",
                        credentials: "include",
                    },
                );

                if (!response.ok) {
                    setToken(null);
                    setClientName(null);
                    return;
                }

                const data = await response.json();
                setToken(data.newAccessToken);
                if (data.clientName) {
                    setClientName(data.clientName);
                }
            } catch (error) {
                console.error("Auto-refresh failed:", error);
            } finally {
                setIsLoading(false);
            }
        };

        refreshAccessToken();
    }, []);

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
            value={{
                token,
                clientName,
                isLoggedIn: !!token,
                login,
                logout,
                refresh,
            }}
        >
            {!isLoading && children}
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
