import axios from "axios";

const tallerApi = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para manejar errores
tallerApi.interceptors.response.use(
  (response) => response,
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

    // Si es un error de red o el servidor no responde
    if (!error.response) {
      return Promise.reject(new Error("Error de conexión con el servidor"));
    }

    // Si es un error del servidor
    const errorMessage = error.response.data?.message || error.message;
    return Promise.reject(new Error(errorMessage));
  },
);

export default tallerApi;
