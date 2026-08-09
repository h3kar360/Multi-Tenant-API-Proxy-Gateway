import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { refreshProcess } from "../util/AuthRefresh";
import { useNavigate } from "react-router-dom";

interface NewApi {
  apiName: string;
  apiUrl: string;
  connectTimeout: number | undefined;
  readTimeout: number | undefined;
}

const AddApiComponent = () => {
  const [apiName, setApiName] = useState<string>("");
  const [apiUrl, setApiUrl] = useState<string>("");
  const [connectTimeout, setConnectTimeout] = useState<number | undefined>();
  const [readTimeout, setReadTimeout] = useState<number | undefined>();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { token, refresh } = useAuth();

  const navigate = useNavigate();

  const addApi = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);

    const newApi: NewApi = {
      apiName,
      apiUrl,
      connectTimeout,
      readTimeout,
    };

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/proxy/v1/api-info`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(newApi),
        },
      );

      if (response.status === 403 || response.status === 401) {
        await refreshProcess(refresh);
        return await addApi(e);
      }

      if (!response.ok) {
        throw new Error(`HTTP Error, status=${response.status}`);
      }

      alert("API added successfully!");
      setApiName("");
      setApiUrl("");
      setConnectTimeout(undefined);
      setReadTimeout(undefined);

      navigate("/");
    } catch (error) {
      console.error(error);
      alert("Failed to add API");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-[calc(100vh-5rem)]">
      <form
        onSubmit={addApi}
        className="bg-gray-600 px-5 py-5 rounded-lg flex flex-col gap-4 w-80"
      >
        <div className="flex justify-center items-center">
          <div className="text-4xl">Add API</div>
        </div>
        <label htmlFor="apiName">API Name: </label>
        <input
          type="text"
          name="apiName"
          value={apiName}
          className="text-black px-2 py-1 rounded"
          onChange={(e) => setApiName(e.target.value)}
        />
        <label htmlFor="apiUrl">API URL: </label>
        <input
          type="text"
          name="apiUrl"
          value={apiUrl}
          className="text-black px-2 py-1 rounded"
          onChange={(e) => setApiUrl(e.target.value)}
        />
        <label htmlFor="connectTimeout">Connect Timeout: </label>
        <input
          type="number"
          name="connectTimeout"
          className="text-black px-2 py-1 rounded"
          onChange={(e) => {
            const value = e.target.value;
            setConnectTimeout(value === "" ? undefined : Number(value));
          }}
        />
        <label htmlFor="readTimeout">Read Timeout: </label>
        <input
          type="number"
          name="readTimeout"
          className="text-black px-2 py-1 rounded"
          onChange={(e) => {
            const value = e.target.value;
            setReadTimeout(value === "" ? undefined : Number(value));
          }}
        />
        <button
          type="submit"
          disabled={isLoading}
          className="bg-gray-500 py-2 rounded-lg hover:bg-gray-400 disabled:opacity-50"
        >
          Add API
        </button>
      </form>
    </div>
  );
};

export default AddApiComponent;
