export interface ClientePayload {
  nombre: string;
  titular: string | null;
  documento: string;
  direccion: string | null;
  cp: string | null;
  poblacion: string | null;
  provincia: string | null;
  pais: string | null;
  email: string | null;
  telefono: string | null;
}

export interface ClienteEditData extends ClientePayload {
  id?: number;
}

export interface Cliente extends ClientePayload {
  id?: number;
}
