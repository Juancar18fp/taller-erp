<template>
  <CustomTable :columns="columns" title="Ordenes" route="/ordenes" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { OrdenTrabajo } from "src/interfaces";
import CustomTable from "src/components/CustomTable.vue";

const columns: QTableColumn[] = [
  {
    name: "id",
    label: "Código",
    field: (row) => (row as OrdenTrabajo).id,
    sortable: true,
    required: true,
    align: "left",
  },
  {
    name: "estado",
    label: "Estado",
    field: "estado",
    sortable: true,
    align: "left",
  },
  {
    name: "fechaCreacion",
    label: "Fecha de creación",
    field: "fechaCreacion",
    align: "left",
  },
  {
    name: "tecnico",
    label: "Técnico",
    field: "tecnico",
    sortable: true,
    align: "left",
  },
];
</script>
