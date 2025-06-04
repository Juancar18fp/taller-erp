<template>
  <CustomTable :columns="columns" title="Ordenes" route="/ordenes" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { OrdenTrabajo } from "../types/entities/orden_trabajo";
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
    name: "codigoOrden",
    label: "Código de Orden",
    field: (row) => (row as OrdenTrabajo).codigoOrden,
    sortable: true,
    required: true,
    align: "left",
  },
];
</script>
