import { createContext, useContext, useState, useEffect } from "react";
import type { ReactNode } from "react";

interface AuthContextType {
    token: string | null;
    clientName: string | null;
    isLoggedIn: boolean;
    initialProxyKey: string;
    handleSignup: (proxykey: string) => void;
    login: (accessToken: string, clientName: string) => void;
    logout: () => void;
    refresh: (accessToken: string) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [token, setToken] = useState<string | null>(null);
    const [clientName, setClientName] = useState<string | null>(null);
    const [initialProxyKey, setInitialProxyKey] = useState<string>("");
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

    const handleSignup = (proxyKey: string) => {
        setInitialProxyKey(proxyKey);
    };

    const login = (accessToken: string, clientName: string): void => {
        setToken(accessToken);
        setClientName(clientName);
    };

    const refresh = (accessToken: string) => {
        setToken(accessToken);
    };

    const logout = async (): Promise<void> => {
        setIsLoading(true);

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/auth/logout`,
                {
                    method: "POST",
                    credentials: "include",
                },
            );

            if (!response.ok)
                throw new Error(`HTTP Error, status=${response.status}`);

            setToken(null);
            setClientName(null);
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <AuthContext.Provider
            value={{
                token,
                clientName,
                isLoggedIn: !!token,
                initialProxyKey,
                handleSignup,
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
