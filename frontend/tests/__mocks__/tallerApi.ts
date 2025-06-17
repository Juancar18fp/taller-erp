import { vi } from "vitest";
import type { Marca, Modelo } from "src/types/entities/vehiculo";
import type { Cliente } from "src/types/entities/cliente";

interface ApiConfig {
  params?: Record<string, string | number>;
}

interface ApiResponse<T> {
  data: T;
}

export const mockMarcas: Marca[] = [
  { id: "1", nombre: "Toyota" },
  { id: "2", nombre: "Ford" },
  { id: "3", nombre: "Volkswagen" },
];

export const mockModelos: Modelo[] = [
  { id: "1", nombre: "Corolla", marca_id: "1" },
  { id: "2", nombre: "Focus", marca_id: "2" },
  { id: "3", nombre: "Golf", marca_id: "3" },
];

export const mockClientes: Cliente[] = [
  {
    id: 1,
    nombre: "Juan Pérez",
    documento: "12345678A",
    direccion: "Calle Falsa 123",
    cp: "28001",
    poblacion: "Madrid",
    provincia: "Madrid",
    pais: "España",
    email: "juan@email.com",
    telefono: "600123456",
    titular: null,
  },
  {
    id: 2,
    nombre: "María García",
    documento: "87654321B",
    direccion: "Avenida Principal 456",
    cp: "28002",
    poblacion: "Madrid",
    provincia: "Madrid",
    pais: "España",
    email: "maria@email.com",
    telefono: "600654321",
    titular: null,
  },
];

// Mock de la API
const tallerApi = {
  get: vi
    .fn()
    .mockImplementation((url: string, config?: ApiConfig): Promise<ApiResponse<unknown>> => {
      const params = config?.params || {};

      if (url === "/marcas") {
        return Promise.resolve({
          data: mockMarcas.filter((marca) =>
            marca.nombre.toLowerCase().includes(((params.search as string) || "").toLowerCase()),
          ),
        });
      }

      if (url === "/modelos/buscar") {
        let modelos = mockModelos;
        if (params.marca) {
          modelos = modelos.filter((modelo) => modelo.marca_id === params.marca);
        }
        if (params.modelo) {
          modelos = modelos.filter((modelo) =>
            modelo.nombre.toLowerCase().includes((params.modelo as string).toLowerCase()),
          );
        }
        return Promise.resolve({ data: modelos });
      }

      if (url === "/clientes") {
        return Promise.resolve({
          data: mockClientes.filter(
            (cliente) =>
              cliente.nombre
                .toLowerCase()
                .includes(((params.search as string) || "").toLowerCase()) ||
              cliente.documento
                .toLowerCase()
                .includes(((params.search as string) || "").toLowerCase()),
          ),
        });
      }

      if (url.startsWith("/marcas/")) {
        const id = url.split("/")[2];
        return Promise.resolve({ data: mockMarcas.find((m) => m.id === id) });
      }

      if (url.startsWith("/modelos/")) {
        const id = url.split("/")[2];
        return Promise.resolve({ data: mockModelos.find((m) => m.id === id) });
      }

      if (url.startsWith("/clientes/")) {
        const id = url.split("/")[2];
        return Promise.resolve({ data: mockClientes.find((c) => c.id === id) });
      }

      return Promise.resolve({ data: {} });
    }),

  post: vi.fn().mockResolvedValue({ data: { id: "new-id" } }),
  put: vi.fn().mockResolvedValue({ data: { id: "updated-id" } }),
  delete: vi.fn().mockResolvedValue({ data: {} }),
};

export default tallerApi;
