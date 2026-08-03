import React from "react";
import { NavLink } from "react-router-dom";

const Navbar = () => {
    return (
        <nav className="flex items-center justify-between bg-black fixed h-20 inset-x-0 z-50 px-8 border-none">
            <div>
                <NavLink to="/" className="text-white">
                    TALI
                </NavLink>
            </div>
            <div>Hi, Somebody</div>
        </nav>
    );
};

export default Navbar;
