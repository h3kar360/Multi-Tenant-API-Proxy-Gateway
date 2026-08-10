import React, { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";

interface SignUpBody {
    username: String;
    email: string;
    password: string;
}

export const SignupComponent = () => {
    const [username, setUsername] = useState<string>("");
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const navigate = useNavigate();

    const submitLogin = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);

        const signupBody: SignUpBody = {
            username,
            email,
            password,
        };

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/auth/signup`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(signupBody),
                },
            );

            if (!response.ok)
                throw new Error(`HTTP Error, status=${response.status}`);

            alert("Check verification code in your email inbox");
            navigate("/verify", { state: { email } });
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex justify-center items-center h-[calc(100vh-5rem)]">
            <form
                onSubmit={submitLogin}
                className="bg-gray-600 px-5 py-5 rounded-lg flex flex-col gap-4"
            >
                <div className="flex justify-center items-center">
                    <div className="text-4xl">Signup</div>
                </div>
                <label htmlFor="username">Username: </label>
                <input
                    type="text"
                    name="username"
                    placeholder="username"
                    className="text-black px-2 py-1 rounded"
                    onChange={(e) => setUsername(e.target.value)}
                />
                <label htmlFor="email">Email: </label>
                <input
                    type="email"
                    name="email"
                    placeholder="email"
                    className="text-black px-2 py-1 rounded"
                    onChange={(e) => setEmail(e.target.value)}
                />
                <label htmlFor="password">Password: </label>
                <input
                    type="password"
                    name="password"
                    placeholder="password"
                    className="text-black px-2 py-1 rounded"
                    onChange={(e) => setPassword(e.target.value)}
                />
                <input
                    type="submit"
                    value="Create account"
                    disabled={isLoading}
                    className="bg-gray-500 py-2 rounded-lg hover:bg-gray-400"
                />
                <p>
                    Already have an account?
                    <NavLink to={"/login"} className="text-blue-700 ml-2">
                        Login
                    </NavLink>
                </p>
            </form>
        </div>
    );
};
