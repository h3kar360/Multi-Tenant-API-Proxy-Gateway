import React, { useState, useEffect } from "react";
import ProxyKeyTextBox from "./ProxyKeyTextBox";
import { useAuth } from "../context/AuthContext";
import { refreshProcess } from "../util/AuthRefresh";
import { useNavigate } from "react-router-dom";

const ProxyKey = () => {
    const { token, refresh, logout, initialProxyKey } = useAuth();
    const [getProxyKey, setGetProxyKey] = useState<boolean>(!!initialProxyKey);
    const [proxyKey, setProxyKey] = useState<string>(initialProxyKey);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (initialProxyKey) {
            setProxyKey(initialProxyKey);
            setGetProxyKey(true);
        }
    }, [initialProxyKey]);

    const makeRequest = async (authToken: string | null): Promise<Response> => {
        return fetch(
            `${import.meta.env.VITE_API_URL}/proxy/v1/key/regenerate`,
            {
                method: "POST",
                headers: { Authorization: `Bearer ${authToken}` },
            },
        );
    };

    const proxyRequest = async () => {
        setIsLoading(true);

        try {
            let response = await makeRequest(token);

            if (response.status === 403 || response.status === 401) {
                const newToken = await refreshProcess(refresh);
                if (!newToken) {
                    logout();
                    navigate("/login");
                    return;
                }

                response = await makeRequest(newToken);

                if (response.status === 403 || response.status === 401) {
                    logout();
                    navigate("/login");
                    return;
                }
            }

            if (!response.ok) {
                throw new Error(`HTTP Error, status=${response.status}`);
            }

            const { proxyKey }: { proxyKey: string } = await response.json();
            setProxyKey(proxyKey);
            setGetProxyKey(true);
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            <div className="flex flex-col justify-center items-center gap-10 mt-10">
                <p className="text-6xl">Your API Key</p>
                {getProxyKey ? (
                    <ProxyKeyTextBox proxyKey={proxyKey} />
                ) : (
                    <button
                        onClick={proxyRequest}
                        disabled={isLoading}
                        className="bg-gray-500 px-3 py-1 rounded hover:bg-gray-400"
                    >
                        Regenerate New Proxy Key
                    </button>
                )}
            </div>
        </>
    );
};

export default ProxyKey;
