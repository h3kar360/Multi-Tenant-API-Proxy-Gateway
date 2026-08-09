import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

interface VerifyBody {
  email: string;
  verificationCode: string;
}

export const VerifyComponent = () => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [verificationCode, setVerificationCode] = useState<string>("");

  const location = useLocation();
  const navigate = useNavigate();

  const email = location.state?.email;

  const verify = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);

    const verify: VerifyBody = {
      email,
      verificationCode,
    };

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/auth/verify`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(verify),
        },
      );

      if (!response.ok)
        throw new Error(`HTTP Error, status=${response.status}`);

      navigate("/login");
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-[calc(100vh-5rem)]">
      <form
        onSubmit={verify}
        className="bg-gray-600 px-5 py-5 rounded-lg flex flex-col gap-4"
      >
        <div className="flex justify-center items-center">
          <div className="text-4xl">Verification</div>
        </div>
        <input
          type="text"
          name="verify"
          placeholder="verification code"
          className="text-black px-2 py-1 rounded"
          onChange={(e) => setVerificationCode(e.target.value)}
        />
        <input
          type="submit"
          value="Verify"
          disabled={isLoading}
          className="bg-gray-500 py-2 rounded-lg hover:bg-gray-400"
        />
      </form>
    </div>
  );
};
