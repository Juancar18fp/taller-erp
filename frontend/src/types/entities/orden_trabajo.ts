import type { Empleado } from "./empleado";
import type { Vehiculo } from "./vehiculo";

export interface EstadoOrden {
  id: string;
  nombre: string;
}

export interface OrdenTrabajo {
  id: string;
  codigoOrden: string;
  empleadoAsignado: Empleado | { id: string; nombre: string };
  vehiculo: Vehiculo | { id: string; matricula: string };
  articulosUsados: ArticuloUsado[];
  estadoOrden: EstadoOrden;
  fechaOrden: string;
  fechaInicio?: string;
  fechaFinalizacion?: string;
  fechaPago?: string;
  pagada: boolean;
  observaciones?: string;
  total: number;
}

export interface OrdenTrabajoPayload {
  codigoOrden: string;
  empleadoAsignado: {
    id: string;
  };
  vehiculo: {
    id: string;
  };
  articulosUsados: ArticuloUsado[];
  estadoOrden: {
    id: string;
  };
  fechaOrden: string;
  fechaInicio: string;
  fechaFinalizacion: string;
  fechaPago: string;
  pagada: boolean;
  observaciones: string;
  total: number;
}

export interface OrdenTrabajoEditData extends OrdenTrabajoPayload {
  id: string;
}

export interface ArticuloUsado {
  id?: string;
  articulo: {
    id: string;
    descripcion: string;
    precio: number;
    stock?: number;
  };
  cantidad: number;
  ordenTrabajo?: {
    id: string;
  };
}
