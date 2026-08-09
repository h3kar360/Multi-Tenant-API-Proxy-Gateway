import React from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const { isLoggedIn, clientName, logout } = useAuth();

  const logoutLogic = async () => {
    logout();
  };

  return (
    <nav className="flex items-center justify-between bg-black fixed h-20 inset-x-0 z-50 px-8 border-none">
      <div>
        <NavLink to="/" className="text-white">
          TALI
        </NavLink>
      </div>
      <div>
        {isLoggedIn ? (
          <div>
            <div>Hi, {clientName}</div>
            <button onClick={logoutLogic}>Logout</button>
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
