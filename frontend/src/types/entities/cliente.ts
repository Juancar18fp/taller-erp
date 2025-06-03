export interface Persona {
  nombre: string;
  documento: string;
  direccion: string | null;
  cp: string | null;
  poblacion: string | null;
  provincia: string | null;
  pais: string | null;
  email: string | null;
  telefono: string | null;
}

export interface ClientePayload extends Persona {
  titular: string | null;
}

export interface ClienteEditData extends ClientePayload {
  id?: number;
}

export interface Cliente extends ClientePayload {
  id?: number;
}
