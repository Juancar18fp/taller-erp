import type { BaseEntity } from "./entities.interface";
export interface ApiResponse<T = BaseEntity> {
  items: T[];
  total: number;
}
