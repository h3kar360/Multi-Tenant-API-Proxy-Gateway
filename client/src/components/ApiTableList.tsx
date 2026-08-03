import React from "react";
import AddNewApi from "./AddNewApi";

interface Api {
    id: number;
    apiName: string;
    apiUrl: string;
    connectTimeout: number;
    readTimeout: number;
}

interface ApiInfo {
    apis: Array<Api>;
}

const ApiTableList = ({ apis }: ApiInfo) => {
    return (
        <div className="flex flex-col items-center p-4">
            <div className="w-full max-w-4xl mb-10">
                <div className="grid grid-cols-5 gap-2 p-2 font-semibold border-b">
                    <div>Id</div>
                    <div>API Name</div>
                    <div>API Url</div>
                    <div>Connect Timeout</div>
                    <div>Read Timeout</div>
                </div>
                {apis.map((api) => (
                    <div key={api.id} className="grid grid-cols-5 gap-2 p-2">
                        <div>{api.id}</div>
                        <div className="truncate">{api.apiName}</div>
                        <div className="truncate">{api.apiUrl}</div>
                        <div>{api.connectTimeout}</div>
                        <div>{api.readTimeout}</div>
                    </div>
                ))}
            </div>
            <AddNewApi />
        </div>
    );
};

export default ApiTableList;
