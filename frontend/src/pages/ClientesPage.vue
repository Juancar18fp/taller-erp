<template>
  <CustomTable :columns="columns" title="Clientes" route="/clientes" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { Cliente } from "../types/entities/cliente";
import CustomTable from "src/components/CustomTable.vue";

const columns: QTableColumn[] = [
  {
    name: "id",
    label: "Código",
    field: (row) => (row as Cliente).id,
    sortable: true,
    required: true,
    align: "left",
  },
  {
    name: "nombre",
    label: "Nombre",
    field: "nombre",
    sortable: true,
    align: "left",
  },
  {
    name: "documento",
    label: "DNI o NIF",
    field: "documento",
    align: "left",
  },
  {
    name: "poblacion",
    label: "Población",
    field: "poblacion",
    sortable: true,
    align: "left",
  },
  {
    name: "email",
    label: "Email",
    field: "email",
    align: "left",
  },
  {
    name: "telefono",
    label: "Teléfono",
    field: "telefono",
    align: "left",
  },
];
</script>
