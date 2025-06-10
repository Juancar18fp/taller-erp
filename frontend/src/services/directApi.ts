export interface DirectApi {
  get: <T = unknown>(url: string, params?: Record<string, string | number | boolean>) => Promise<T>;
}

export const directApi: DirectApi = {
  get: async <T = unknown>(
    url: string,
    params: Record<string, string | number | boolean> = {},
  ): Promise<T> => {
    const token = localStorage.getItem("token");
    const queryString = new URLSearchParams(params as Record<string, string>).toString();
    const fullUrl = `http://localhost:8080${url}${queryString ? "?" + queryString : ""}`;

    const response = await fetch(fullUrl, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${await response.text()}`);
    }

    return response.json();
  },
};
