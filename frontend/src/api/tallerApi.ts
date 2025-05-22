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
    });
    return Promise.reject(new Error(error.response?.data?.message || error.message));
  },
);

export default tallerApi;
