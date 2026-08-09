export const refreshProcess = async (refresh: (newToken: string) => void) => {
  try {
    const response = await fetch(
      `${import.meta.env.VITE_API_URL}/auth/refresh`,
      {
        method: "POST",
      },
    );

    if (!response.ok) throw new Error(`HTTP Error, status=${response.status}`);

    const { newAccessToken }: { newAccessToken: string } =
      await response.json();

    refresh(newAccessToken);
  } catch (error) {
    console.error(error);
  }
};
