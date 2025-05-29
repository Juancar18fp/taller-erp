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

export interface VehiculoEditData extends VehiculoPayload {
  id?: number;
}

export interface Marca {
  id: string;
  nombre: string;
}

export interface Vehiculo extends VehiculoPayload {
  id?: number;
}
