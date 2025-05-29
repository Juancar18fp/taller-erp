// types/entities/ordenTrabajo.ts

import type { Cliente } from "./cliente";
import type { Vehiculo } from "./vehiculo";
import type { Tecnico } from "./tecnico";
import type { Articulo } from "./articulo";

export enum EstadoOrden {
  PENDIENTE = "PENDIENTE",
  EN_PROCESO = "EN_PROCESO",
  PAUSADA = "PAUSADA",
  COMPLETADA = "COMPLETADA",
  ENTREGADA = "ENTREGADA",
  CANCELADA = "CANCELADA",
}

// Interfaz para TareaChapaPintura (si la necesitas)
export interface TareaChapaPintura {
  id: number;
  nombre?: string;
  descripcion?: string;
  completada?: boolean;
  fechaInicio?: string;
  fechaFin?: string;
  ordenTrabajo?: OrdenTrabajo;
}

// Interfaz para MaterialUtilizado
export interface MaterialUtilizado {
  id: number;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  articulo: Articulo;
  ordenTrabajo?: OrdenTrabajo;
}

// Interfaz principal para OrdenTrabajo
export interface OrdenTrabajo {
  id: number;
  nombre: string;
  estado: EstadoOrden;
  fechaCreacion: string; // ISO string format
  fechaInicioTrabajo?: string; // ISO string format
  fechaFinalizacion?: string; // ISO string format
  fechaEntrega?: string; // ISO string format
  tareas: TareaChapaPintura[];
  materiales: MaterialUtilizado[];
  observaciones?: string;
  pagado: boolean;
  metodoPago?: string;
  fechaPago?: string; // ISO string format
  tecnicoPrincipal?: Tecnico;
  vehiculo: Vehiculo;
  cliente: Cliente;
}

// Interfaz para crear/actualizar orden de trabajo (payload)
export interface OrdenTrabajoPayload {
  nombre: string;
  estado: EstadoOrden | string;
  fechaInicioTrabajo?: string;
  fechaFinalizacion?: string;
  fechaEntrega?: string;
  observaciones?: string;
  pagado: boolean;
  metodoPago?: string;
  fechaPago?: string;
  tecnicoPrincipal?: {
    id: number | string;
  };
  vehiculo: {
    id: number | string;
  };
  cliente: {
    id: number | string;
  };
  materiales: MaterialUtilizadoPayload[];
}

// Interfaz para el payload de materiales utilizados
export interface MaterialUtilizadoPayload {
  articulo: {
    id: number | string;
  };
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

// Interfaz para editar orden de trabajo
export interface OrdenTrabajoEditData {
  id: number;
  nombre: string;
  estado: EstadoOrden;
  fechaCreacion: string;
  fechaInicioTrabajo?: string;
  fechaFinalizacion?: string;
  fechaEntrega?: string;
  observaciones?: string;
  pagado: boolean;
  metodoPago?: string;
  fechaPago?: string;
  tecnicoPrincipal?: Tecnico;
  vehiculo: Vehiculo;
  cliente: Cliente;
  materiales: MaterialUtilizado[];
  tareas: TareaChapaPintura[];
}

// Interfaz para crear orden de trabajo (formulario)
export interface OrdenTrabajoCreate {
  nombre: string;
  estado: EstadoOrden;
  fechaInicioTrabajo?: string;
  fechaFinalizacion?: string;
  fechaEntrega?: string;
  observaciones?: string;
  pagado: boolean;
  metodoPago?: string;
  fechaPago?: string;
  tecnicoPrincipalId?: number;
  vehiculoId: number;
  clienteId: number;
  materiales: MaterialUtilizadoCreate[];
}

// Interfaz para crear material utilizado
export interface MaterialUtilizadoCreate {
  articuloId: number;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

// Interfaz para la respuesta de la API al listar órdenes
export interface OrdenTrabajoListItem {
  id: number;
  nombre: string;
  estado: EstadoOrden;
  fechaCreacion: string;
  fechaEntrega?: string;
  pagado: boolean;
  cliente: {
    id: number;
    nombre: string;
  };
  vehiculo: {
    id: number;
    matricula: string;
    marca?: {
      nombre: string;
    };
    modelo?: {
      nombre: string;
    };
  };
  tecnicoPrincipal?: {
    id: number;
    nombre: string;
  };
  totalMateriales?: number;
}

// Interfaz para filtros de búsqueda
export interface OrdenTrabajoFilters {
  nombre?: string;
  estado?: EstadoOrden;
  clienteId?: number;
  vehiculoId?: number;
  tecnicoId?: number;
  pagado?: boolean;
  fechaDesde?: string;
  fechaHasta?: string;
}

// Interfaz para estadísticas de órdenes (si la necesitas)
export interface OrdenTrabajoStats {
  total: number;
  pendientes: number;
  enProgreso: number;
  completadas: number;
  entregadas: number;
  canceladas: number;
  totalFacturado: number;
  totalPendientePago: number;
}

export const ESTADOS_ORDEN_LABELS: Record<EstadoOrden, string> = {
  [EstadoOrden.PENDIENTE]: "Pendiente",
  [EstadoOrden.EN_PROCESO]: "En Proceso",
  [EstadoOrden.PAUSADA]: "Pausada",
  [EstadoOrden.COMPLETADA]: "Completada",
  [EstadoOrden.ENTREGADA]: "Entregada",
  [EstadoOrden.CANCELADA]: "Cancelada",
};

export const METODOS_PAGO = ["EFECTIVO", "TARJETA", "TRANSFERENCIA", "CHEQUE"] as const;

export type MetodoPago = (typeof METODOS_PAGO)[number];
