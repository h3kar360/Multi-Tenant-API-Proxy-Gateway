import React, { useState } from "react";
import { Check, Copy, Eye, EyeOff } from "lucide-react";

interface ProxyKeyValue {
  proxyKey: string;
}

const ProxyKeyTextBox = ({ proxyKey }: ProxyKeyValue) => {
  const [copied, setCopied] = useState<boolean>(false);
  const [viewToken, setViewToken] = useState<boolean>(false);

  const copyProxyKey = async () => {
    try {
      await navigator.clipboard.writeText(proxyKey);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (error) {
      console.error(error);
    }
  };

  const partiallyHideKey = (): string => {
    const first = proxyKey.slice(0, 6);
    const last = proxyKey.slice(-4);
    const dots = "•".repeat(proxyKey.length - 10);
    return `${first}${dots}${last}`;
  };

  const displayedValue = viewToken ? proxyKey : partiallyHideKey();

  return (
    <div className="w-96">
      <div className="flex items-center gap-2 w-full bg-gray-500 px-3 py-1 rounded">
        <div onClick={() => setViewToken(!viewToken)}>
          {!viewToken ? <EyeOff /> : <Eye />}
        </div>
        <input
          type="text"
          value={displayedValue}
          readOnly
          className="flex-1 bg-transparent outline-none"
        />
        <div onClick={copyProxyKey}>{!copied ? <Copy /> : <Check />}</div>
      </div>
    </div>
  );
};

export default ProxyKeyTextBox;
