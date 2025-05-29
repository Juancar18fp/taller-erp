export interface TecnicoPayLoad {
  nombre: string;
}

export interface TecnicoEditData extends TecnicoPayLoad {
  id?: number;
}
export interface Tecnico extends TecnicoPayLoad {
  id?: number;
}
