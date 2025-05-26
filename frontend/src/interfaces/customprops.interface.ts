import type { QTableColumn } from "quasar";

export interface CustomTableProps {
  columns: QTableColumn[];
  title: string;
  route: string;
}

export interface CustomInputProps {
  modelValue: string | null;
  label: string;
  hint?: string;
  obligatorio?: boolean;
}
