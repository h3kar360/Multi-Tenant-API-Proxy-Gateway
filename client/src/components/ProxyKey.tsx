import React from "react";
import ProxyKeyTextBox from "./ProxyKeyTextBox";

interface ProxyKeyValue {
    proxyKey: string;
}

const ProxyKey = ({ proxyKey }: ProxyKeyValue) => {
    return (
        <div className="flex flex-col justify-center items-center gap-10 mt-10">
            <p className="text-6xl">Your API Key</p>
            <ProxyKeyTextBox proxyKey={proxyKey} />
        </div>
    );
};

export default ProxyKey;
