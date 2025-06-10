export interface LoginRequest {
  dni: string;
  password: string;
}

export interface RolDto {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  empleadoId: number;
  dni: string;
  nombre: string;
  email: string;
  rol: RolDto;
  permisos: string[];
}

export interface Usuario {
  id: number;
  dni: string;
  nombre: string;
  email: string;
  rol: RolDto;
  permisos: string[];
}
