import React from "react";
import { UpdateApiComponent } from "../components/UpdateApiComponent";
import { useParams } from "react-router-dom";

export const UpdateApiPage = () => {
  const { id } = useParams<{ id: string }>();

  return <UpdateApiComponent id={id} />;
};
