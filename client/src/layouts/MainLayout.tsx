import React from "react";
import Navbar from "../components/Navbar";
import { Outlet } from "react-router-dom";

const MainLayout = () => {
    return (
        <>
            <Navbar />
            <div className="pt-20"></div>
            <Outlet />
        </>
    );
};

export default MainLayout;
