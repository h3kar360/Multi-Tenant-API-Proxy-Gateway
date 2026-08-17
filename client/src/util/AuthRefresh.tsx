export const refreshProcess = async (refresh: (newToken: string) => void) => {
    try {
        console.log("Refreshing token...");
        const response = await fetch(
            `${import.meta.env.VITE_API_URL}/auth/refresh`,
            {
                method: "POST",
                credentials: "include",
            },
        );

        if (!response.ok) {
            console.error(`Refresh failed: ${response.status}`);
            return null;
        }

        const { newAccessToken }: { newAccessToken: string } =
            await response.json();

        refresh(newAccessToken);
        return newAccessToken;
    } catch (error) {
        console.error("Refresh error:", error);
        return false;
    }
};
