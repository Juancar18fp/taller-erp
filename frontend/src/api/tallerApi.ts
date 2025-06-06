import axios from "axios";

const tallerApi = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

tallerApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    } else {
      console.warn("⚠️ No hay token disponible para:", config.url);
    }

    return config;
  },
  (error) => {
    console.error("Error en interceptor de request:", error);
    return Promise.reject(new Error(error));
  },
);

tallerApi.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error("Error en la petición:", {
      url: error.config?.url,
      method: error.config?.method,
      data: error.config?.data,
      status: error.response?.status,
      response: error.response?.data,
      baseURL: error.config?.baseURL,
      fullUrl: error.config?.baseURL + error.config?.url,
    });

    if (error.response?.status === 401) {
      console.warn("🚪 Token inválido o expirado, limpiando datos...");
      localStorage.removeItem("token");
      localStorage.removeItem("usuario");

      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }

      return Promise.reject(new Error("Sesión expirada. Inicia sesión nuevamente."));
    }

    if (!error.response) {
      return Promise.reject(new Error("Error de conexión con el servidor"));
    }

    const errorMessage = error.response.data?.message || error.message;
    return Promise.reject(new Error(errorMessage));
  },
);

export default tallerApi;
