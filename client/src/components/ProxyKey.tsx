import React, { useState } from "react";
import ProxyKeyTextBox from "./ProxyKeyTextBox";
import { useAuth } from "../context/AuthContext";
import { refreshProcess } from "../util/AuthRefresh";

const ProxyKey = () => {
  const [getProxyKey, setGetProxyKey] = useState<boolean>(false);
  const [proxyKey, setProxyKey] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { token, refresh } = useAuth();

  const proxyRequest = async () => {
    setIsLoading(true);

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/proxy/v1/generate`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        },
      );

      if (response.status === 403 || response.status === 401) {
        await refreshProcess(refresh);
        return await proxyRequest();
      }

      if (!response.ok)
        throw new Error(`HTTP Error, status=${response.status}`);

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
            Get Proxy Key
          </button>
        )}
      </div>
    </>
  );
};

export default ProxyKey;
