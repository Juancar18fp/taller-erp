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
