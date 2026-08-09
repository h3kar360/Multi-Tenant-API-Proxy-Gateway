import React from "react";
import { NavLink } from "react-router-dom";

const AddNewApi = () => {
  return (
    <button className="bg-gray-500 px-2 py-1 rounded hover:bg-gray-400">
      <NavLink to={"/add-api"}>+ New API</NavLink>
    </button>
  );
};

export default AddNewApi;
