import React from "react";
import ProxyKey from "../components/ProxyKey";
import ApiTableList from "../components/ApiTableList";

const HomePage = () => {
    const stuff = [
        {
            id: 1,
            apiName: "hello",
            apiUrl: "uehiowho",
            connectTimeout: 1000,
            readTimeout: 2000,
        },
        {
            id: 2,
            apiName: "hello",
            apiUrl: "uehiowho",
            connectTimeout: 1000,
            readTimeout: 2000,
        },
    ];

    return (
        <div className="flex flex-col gap-20">
            <ProxyKey proxyKey="wehwefhewohewoihefnnonowehjoewhfiowfwoij" />
            <ApiTableList apis={stuff} />
        </div>
    );
};

export default HomePage;
