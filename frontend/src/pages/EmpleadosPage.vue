<template>
  <CustomTable :columns="columns" title="Empleados" route="/empleados" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { Empleado } from "../types/entities/empleado";
import CustomTable from "src/components/CustomTable.vue";

const columns: QTableColumn[] = [
  {
    name: "id",
    label: "Código",
    field: (row) => (row as Empleado).id,
    sortable: true,
    required: true,
    align: "left",
  },
  {
    name: "nombre",
    label: "Nombre",
    field: "nombre",
    align: "left",
  },
  {
    name: "puesto",
    label: "Puesto Actual",
    field: (row) => {
      const empleado = row as Empleado;
      const contratoActivo = empleado.contratos?.find((c) => c.activo);
      return contratoActivo?.puesto?.nombre || "Empleado inactivo";
    },
    sortable: true,
    align: "left",
  },
  {
    name: "telefono",
    label: "Teléfono",
    field: "telefono",
    sortable: false,
    align: "left",
  },
  {
    name: "email",
    label: "Email",
    field: "email",
    sortable: false,
    align: "left",
  },
];
</script>
