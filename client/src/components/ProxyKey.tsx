import React, { useState } from "react";
import ProxyKeyTextBox from "./ProxyKeyTextBox";

interface ProxyKeyValue {
  proxyKey: string;
}

const ProxyKey = ({ proxyKey }: ProxyKeyValue) => {
  const [getProxyKey, setGetProxyKey] = useState(false);
  return (
    <>
      <div className="flex flex-col justify-center items-center gap-10 mt-10">
        <p className="text-6xl">Your API Key</p>
        {getProxyKey ? (
          <ProxyKeyTextBox proxyKey={proxyKey} />
        ) : (
          <button
            onClick={() => setGetProxyKey(true)}
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
