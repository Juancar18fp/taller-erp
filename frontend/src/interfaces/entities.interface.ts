export interface BaseEntity {
  id: number;
  [key: string]: unknown;
}

export interface Cliente extends BaseEntity {
  razonSocial: string;
  titular: string;
  nif: string;
  direccion: string;
  cp: string;
  poblacion: string;
  provincia: string;
  pais: string;
  email: string;
  telefono: string;
}

export interface Vehiculo extends BaseEntity {
  matricula: string;
}

export interface Articulo extends BaseEntity {
  descripcion: string;
  precio: number;
  stock: number;
  proveedor?: string;
}

export interface OrdenTrabajo extends BaseEntity {
  vehiculo: Vehiculo;
  cliente: Cliente;
  fechaEntrada: Date;
  fechaSalida: Date;
  estado: string;
}
