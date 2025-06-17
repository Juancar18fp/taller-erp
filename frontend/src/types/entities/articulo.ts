export interface ArticuloPayload {
  descripcion: string;
  precio: string;
  stock: string;
  proveedor: string;
}

export interface ArticuloEditData extends ArticuloPayload {
  id?: string;
}

export interface Articulo extends ArticuloPayload {
  id?: string;
}
