<template>
  <CustomTable :columns="columns" title="Vehiculos" route="/vehiculos" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { Vehiculo } from "../interfaces/index";
import CustomTable from "src/components/CustomTable.vue";

const columns: QTableColumn[] = [
  {
    name: "id",
    label: "Código",
    field: (row) => (row as Vehiculo).id,
    sortable: true,
    required: true,
    align: "left",
  },
  {
    name: "matricula",
    label: "Matrícula",
    field: "matricula",
    sortable: true,
    align: "left",
  },
  {
    name: "marca",
    label: "Marca",
    field: "marca",
    sortable: true,
    align: "left",
  },
  {
    name: "modelo",
    label: "Modelo",
    field: "modelo",
    sortable: true,
    align: "left",
  },
  {
    name: "cliente.id",
    label: "Cliente",
    field: (row) => row.cliente.nombre,
    sortable: true,
    align: "left",
  },
];
</script>
