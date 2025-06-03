import type { Persona } from "./cliente";

export interface EmpleadoPayload extends Persona {
  fechaNacimiento: string | null;
  estadoCivil: {
    id: string;
    nombre?: string;
  };

  numeroSeguridadSocial: string;
  rol: {
    id: string;
    nombre?: string;
  };
  contratos: Contrato[];
}
export interface Contrato {
  id?: string;
  puesto: { id: string };
  fechaContratacion: string | null;
  fechaFinalizacion?: string | null;
  tipoContrato: { id: string };
  jornadaLaboral: { id: string };
  salario: string | number;
  numeroCuenta: string;
  empleado: {
    id: string;
  };
  activo: boolean;
  expanded: boolean;
}
export interface Empleado extends EmpleadoPayload {
  id: number;
}

export interface EmpleadoEditData extends EmpleadoPayload {
  id: number;
}

export interface EstadoCivil {
  id: string;
  nombre: string;
}

export interface Puesto {
  id: string;
  nombre: string;
}

export interface TipoContrato {
  id: string;
  nombre: string;
}

export interface JornadaLaboral {
  id: string;
  nombre: string;
}

export interface Rol {
  id: string;
  nombre: string;
}
