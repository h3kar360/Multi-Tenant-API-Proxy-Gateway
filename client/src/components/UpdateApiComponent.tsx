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

export const UpdateApiComponent = ({ id }: { id: string | undefined }) => {
    const [apiName, setApiName] = useState<string>("");
    const [apiUrl, setApiUrl] = useState<string>("");
    const [connectTimeout, setConnectTimeout] = useState<number | undefined>();
    const [readTimeout, setReadTimeout] = useState<number | undefined>();
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { token, refresh, logout } = useAuth();

    const navigate = useNavigate();

    const makeRequest = async (authToken: string | null): Promise<Response> => {
        const newApi: NewApi = {
            apiName,
            apiUrl,
            connectTimeout,
            readTimeout,
        };

        return fetch(
            `${import.meta.env.VITE_API_URL}/proxy/v1/api-info/${id}`,
            {
                method: "PUT",
                headers: { Authorization: `Bearer ${authToken}` },
                body: JSON.stringify(newApi),
            },
        );
    };

    const updateApi = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
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
                onSubmit={updateApi}
                className="bg-gray-600 px-5 py-5 rounded-lg flex flex-col gap-4 w-80"
            >
                <div className="flex justify-center items-center">
                    <div className="text-4xl">Update API</div>
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
                        setConnectTimeout(
                            value === "" ? undefined : Number(value),
                        );
                    }}
                />
                <label htmlFor="readTimeout">Read Timeout: </label>
                <input
                    type="number"
                    name="readTimeout"
                    className="text-black px-2 py-1 rounded"
                    onChange={(e) => {
                        const value = e.target.value;
                        setReadTimeout(
                            value === "" ? undefined : Number(value),
                        );
                    }}
                />
                <button
                    type="submit"
                    disabled={isLoading}
                    className="bg-gray-500 py-2 rounded-lg hover:bg-gray-400 disabled:opacity-50"
                >
                    Update API
                </button>
            </form>
        </div>
    );
};
