export interface BaseEntity {
  id: number;
}

export interface ClientePayload {
  nombre: string;
  documento: string;
  titular: string | null;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
}

export interface VehiculoPayload {
  matricula: string;
  marca: {
    id: string;
    nombre?: string;
  };
  modelo: {
    id: string;
    nombre?: string;
    marca_id?: string;
  };
  matriculacion: string;
  cliente: {
    id: string;
    nombre: string;
  };
}

export interface VehiculoApiPayload {
  matricula: string;
  marca: string;
  modelo: string;
  matriculacion: string;
  cliente: {
    id: string;
    nombre: string;
  };
}

export interface VehiculoEditData extends Omit<VehiculoPayload, "marca" | "modelo"> {
  id: number;
  marca: string | { id: string | number };
  modelo: string | { id: string | number };
}

export interface ArticuloPayload {
  descripcion: string;
  precio: number;
  stock: number;
  proveedor: string;
}

export interface OrdenTrabajoPayload {
  vehiculo: {
    id: string;
    matricula: string;
  };
  cliente: {
    id: string;
    nombre: string;
  };
  fechaEntrada: string;
  descripcion: string;
  estado: string;
  articulos?: Array<{
    id: string;
    cantidad: number;
  }>;
}
