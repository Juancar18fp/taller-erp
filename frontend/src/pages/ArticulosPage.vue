<template>
  <CustomTable :columns="columns" title="Artículos" route="/articulos" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { Articulo } from "../types/entities/articulo";
import CustomTable from "src/components/CustomTable.vue";

const columns: QTableColumn[] = [
  {
    name: "id",
    label: "Código",
    field: (row) => (row as Articulo).id,
    sortable: true,
    required: true,
    align: "left",
  },
  {
    name: "descripcion",
    label: "Descripción",
    field: "descripcion",
    sortable: true,
    align: "left",
  },
  {
    name: "precio",
    label: "Precio",
    field: "precio",
    sortable: true,
    align: "left",
  },
  {
    name: "stock",
    label: "Stock",
    field: "stock",
    sortable: true,
    align: "left",
  },
  {
    name: "proveedor",
    label: "Proveedor",
    sortable: true,

    field: "proveedor",
    align: "left",
  },
];
</script>
