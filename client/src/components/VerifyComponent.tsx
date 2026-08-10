import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

interface VerifyBody {
    email: string;
    verificationCode: string;
}

interface ResendBody {
    email: string;
}

export const VerifyComponent = () => {
    const [isVerifyLoading, setIsVerifyLoading] = useState<boolean>(false);
    const [isResendLoading, setIsResendLoading] = useState<boolean>(false);
    const [verificationCode, setVerificationCode] = useState<string>("");

    const location = useLocation();
    const navigate = useNavigate();

    const email = location.state?.email;

    const verify = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsVerifyLoading(true);

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

            if (!response.ok) {
                alert("Incorrect verification code");
                throw new Error(`HTTP Error, status=${response.status}`);
            }

            navigate("/login");
        } catch (error) {
            console.error(error);
        } finally {
            setIsVerifyLoading(false);
        }
    };

    const resend = async () => {
        setIsResendLoading(true);

        const resend: ResendBody = {
            email,
        };

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/auth/verify`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(resend),
                },
            );

            if (!response.ok) {
                throw new Error(`HTTP Error, status=${response.status}`);
            }

            alert("Verification code is resent to your email");
        } catch (error) {
            console.error(error);
        } finally {
            setIsVerifyLoading(false);
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
                <div className="flex gap-0 items-center">
                    <input
                        type="text"
                        name="verify"
                        placeholder="verification code"
                        className="text-black px-2 py-1 rounded"
                        onChange={(e) => setVerificationCode(e.target.value)}
                    />
                    <button
                        className="bg-gray-500 px-2 py-1 rounded hover:bg-gray-400"
                        onClick={resend}
                        disabled={isResendLoading}
                    >
                        Resend
                    </button>
                </div>
                <input
                    type="submit"
                    value="Verify"
                    disabled={isVerifyLoading}
                    className="bg-gray-500 py-2 rounded-lg hover:bg-gray-400"
                />
            </form>
        </div>
    );
};
