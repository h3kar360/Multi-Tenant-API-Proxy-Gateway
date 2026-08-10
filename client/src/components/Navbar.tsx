import React, { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { isLoggedIn, clientName, logout } = useAuth();

    const navigate = useNavigate();

    const logoutLogic = async () => {
        setIsLoading(true);

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/auth/logout`,
                {
                    method: "POST",
                    credentials: "include",
                },
            );

            if (!response.ok)
                throw new Error(`HTTP Error, status=${response.status}`);

            logout();
            navigate("/login");
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <nav className="flex items-center justify-between bg-black fixed h-20 inset-x-0 z-50 px-8 border-none">
            <div>
                <NavLink to="/" className="text-white">
                    PROXIER
                </NavLink>
            </div>
            <div>
                {isLoggedIn ? (
                    <div className="flex gap-4 items-center">
                        <div>Hi, {clientName}</div>
                        <button
                            onClick={logoutLogic}
                            disabled={isLoading}
                            className="px-3 py-2 bg-gray-600 rounded-md hover:bg-gray-500"
                        >
                            Logout
                        </button>
                    </div>
                ) : (
                    <button className="px-3 py-2 bg-gray-600 rounded-md hover:bg-gray-500">
                        <NavLink to="/login">Login</NavLink>
                    </button>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
