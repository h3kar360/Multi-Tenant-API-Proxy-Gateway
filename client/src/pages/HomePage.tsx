import React, { useEffect, useState } from "react";
import ProxyKey from "../components/ProxyKey";
import ApiTableList from "../components/ApiTableList";
import { useAuth } from "../context/AuthContext";
import { refreshProcess } from "../util/AuthRefresh";
import { useNavigate } from "react-router-dom";

interface ApiResponse {
    id: number;
    apiName: string;
    apiUrl: string;
    connectTimeout: number;
    readTimeout: number;
}

const HomePage = () => {
    const { isLoggedIn, token, refresh, logout } = useAuth();
    const [apis, setApis] = useState<Array<ApiResponse>>([]);
    const [proxyKey, setProxyKey] = useState<string>("");

    const navigate = useNavigate();

    const makeRequest = async (authToken: string | null): Promise<Response> => {
        return fetch(`${import.meta.env.VITE_API_URL}/proxy/v1/api-info`, {
            method: "GET",
            headers: { Authorization: `Bearer ${authToken}` },
        });
    };

    const getAllApis = async () => {
        if (!isLoggedIn) return;

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

            const apis: Array<ApiResponse> = await response.json();

            setApis(apis);
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        getAllApis();
    }, [isLoggedIn]);

    return isLoggedIn ? (
        <div className="flex flex-col gap-20">
            <ProxyKey />
            <ApiTableList apis={apis} onApiDelete={getAllApis} />
        </div>
    ) : (
        <div className="flex justify-center items-center h-[calc(100vh-4rem)]">
            Please log in to view your proxy key and APIs.
        </div>
    );
};

export default HomePage;
