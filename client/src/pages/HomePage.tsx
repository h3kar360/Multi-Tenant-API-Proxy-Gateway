import React, { useEffect, useState } from "react";
import ProxyKey from "../components/ProxyKey";
import ApiTableList from "../components/ApiTableList";
import { useAuth } from "../context/AuthContext";
import { refreshProcess } from "../util/AuthRefresh";

interface ApiResponse {
  id: number;
  apiName: string;
  apiUrl: string;
  connectTimeout: number;
  readTimeout: number;
}

const HomePage = () => {
  const { isLoggedIn, token, refresh } = useAuth();
  const [apis, setApis] = useState<Array<ApiResponse>>([]);

  useEffect(() => {
    const getAllApis = async () => {
      try {
        const response = await fetch(
          `${import.meta.env.VITE_API_URL}/proxy/v1/api-info`,
          {
            method: "GET",
            headers: { Authorization: `Bearer ${token}` },
          },
        );

        if (response.status === 403 || response.status === 401) {
          await refreshProcess(refresh);
          return await getAllApis();
        }

        if (!response.ok)
          throw new Error(`HTTP Error, status=${response.status}`);

        const apis: Array<ApiResponse> = await response.json();

        setApis(apis);
      } catch (error) {
        console.error(error);
      }
    };

    getAllApis();
  }, [isLoggedIn]);

  return isLoggedIn ? (
    <div className="flex flex-col gap-20">
      <ProxyKey />
      <ApiTableList apis={apis} />
    </div>
  ) : (
    <div className="flex justify-center items-center h-[calc(100vh-4rem)]">
      Please log in to view your proxy key and APIs.
    </div>
  );
};

export default HomePage;
