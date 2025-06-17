import type { Persona } from "./cliente";

export interface EmpleadoPayload extends Persona {
  password: string;
  fechaNacimiento: string | null;
  estadoCivil: {
    id: string;
    nombre?: string;
  };
  numeroSeguridadSocial: string;
  contratos: Contrato[];
}

export interface Contrato {
  id?: string;
  puesto: {
    id: string;
    nombre?: string;
    rol?: Rol;
  };
  fechaContratacion: string | null;
  fechaFinalizacion?: string | null;
  tipoContrato: {
    id: string;
    nombre?: string;
  };
  jornadaLaboral: {
    id: string;
    nombre?: string;
  };
  salario: string | number;
  numeroCuenta: string;
  empleado: {
    id: string;
  };
  activo: boolean;
  expanded: boolean;
}

export interface Empleado extends Omit<EmpleadoPayload, "rol"> {
  id: number;
  activo?: boolean;
  rol?: Rol;
}

export interface EmpleadoEditData extends Omit<EmpleadoPayload, "rol"> {
  id: number;
  activo?: boolean;
  rol?: Rol;
}

export interface EstadoCivil {
  id: string;
  nombre: string;
}

export interface Puesto {
  id: string;
  nombre: string;
  rol: Rol;
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
  descripcion?: string;
}

export interface EmpleadoResponse extends Empleado {
  contratos: ContratoResponse[];
}

export interface ContratoResponse
  extends Omit<Contrato, "puesto" | "tipoContrato" | "jornadaLaboral"> {
  puesto: Puesto;
  tipoContrato: TipoContrato;
  jornadaLaboral: JornadaLaboral;
}
