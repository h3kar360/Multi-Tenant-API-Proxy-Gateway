import React, { useState } from "react";
import AddNewApi from "./AddNewApi";
import { SquarePen, Trash2 } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { refreshProcess } from "../util/AuthRefresh";

interface ApiResponse {
    id: number;
    apiName: string;
    apiUrl: string;
    connectTimeout: number;
    readTimeout: number;
}

interface ApiInfo {
    apis: Array<ApiResponse>;
    onApiDelete: () => void;
}

const ApiTableList = ({ apis, onApiDelete }: ApiInfo) => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { token, refresh, logout } = useAuth();

    const navigate = useNavigate();

    const handleEdit = (id: number) => {
        navigate(`update-api/${id}`);
    };

    const makeDeleteRequest = async (
        authToken: string | null,
        id: number,
    ): Promise<Response> => {
        return fetch(
            `${import.meta.env.VITE_API_URL}/proxy/v1/api-info/${id}`,
            {
                method: "POST",
                headers: { Authorization: `Bearer ${authToken}` },
            },
        );
    };

    const handleDelete = async (id: number) => {
        setIsLoading(true);

        try {
            let response = await makeDeleteRequest(token, id);

            if (response.status === 403 || response.status === 401) {
                const newToken = await refreshProcess(refresh);
                if (!newToken) {
                    logout();
                    navigate("/login");
                    return;
                }

                response = await makeDeleteRequest(newToken, id);

                if (response.status === 403 || response.status === 401) {
                    logout();
                    navigate("/login");
                    return;
                }
            }

            if (!response.ok) {
                throw new Error(`HTTP Error, status=${response.status}`);
            }

            alert("API deleted successfully");
            onApiDelete();
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="w-full max-w-5xl mx-auto px-4">
            <div className="bg-gray-800/50 rounded-xl overflow-hidden shadow-lg border border-gray-700">
                <div className="grid grid-cols-12 gap-3 px-6 py-4 bg-gray-700/50 border-b border-gray-600">
                    <div className="col-span-1 text-xs font-medium text-gray-400 uppercase tracking-wider">
                        ID
                    </div>
                    <div className="col-span-3 text-xs font-medium text-gray-400 uppercase tracking-wider">
                        API Name
                    </div>
                    <div className="col-span-3 text-xs font-medium text-gray-400 uppercase tracking-wider">
                        API URL
                    </div>
                    <div className="col-span-2 text-xs font-medium text-gray-400 uppercase tracking-wider">
                        Connect Timeout
                    </div>
                    <div className="col-span-2 text-xs font-medium text-gray-400 uppercase tracking-wider">
                        Read Timeout
                    </div>
                    <div className="col-span-1 text-xs font-medium text-gray-400 uppercase tracking-wider text-right">
                        Actions
                    </div>
                </div>

                {apis.map((api) => (
                    <div
                        key={api.id}
                        className="grid grid-cols-12 gap-3 px-6 py-4 border-b border-gray-700/50 hover:bg-gray-700/30 transition-colors"
                    >
                        <div className="col-span-1 text-sm text-gray-300 font-mono">
                            {api.id}
                        </div>
                        <div className="col-span-3 text-sm text-gray-200 truncate">
                            {api.apiName}
                        </div>
                        <div className="col-span-3 text-sm text-gray-200 truncate">
                            {api.apiUrl}
                        </div>
                        <div className="col-span-2 text-sm text-gray-300">
                            {api.connectTimeout}ms
                        </div>
                        <div className="col-span-2 text-sm text-gray-300">
                            {api.readTimeout}ms
                        </div>
                        <div className="col-span-1 flex items-center justify-end gap-2">
                            <button
                                onClick={() => handleEdit(api.id)}
                                className="p-1.5 rounded-md hover:bg-blue-500/20 text-blue-400 transition-colors"
                                aria-label="Edit"
                            >
                                <SquarePen className="w-4 h-4" />
                            </button>
                            <button
                                onClick={() => handleDelete(api.id)}
                                disabled={isLoading}
                                className="p-1.5 rounded-md hover:bg-red-500/20 text-red-400 transition-colors"
                                aria-label="Delete"
                            >
                                <Trash2 className="w-4 h-4" />
                            </button>
                        </div>
                    </div>
                ))}

                {apis.length === 0 && (
                    <div className="text-center py-12 text-gray-400">
                        No APIs configured yet
                    </div>
                )}
            </div>

            <div className="mt-6 flex justify-end">
                <AddNewApi />
            </div>
        </div>
    );
};

export default ApiTableList;
