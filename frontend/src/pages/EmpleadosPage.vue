<template>
  <CustomTable :columns="columns" title="Empleados" route="/empleados" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { Empleado } from "../types/entities/empleado";
import CustomTable from "src/components/CustomTable.vue";
import { onMounted, ref } from "vue";
import tallerApi from "src/api/tallerApi";

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
    sortable: true,
    align: "left",
  },
  {
    name: "documento",
    label: "DNI",
    field: "documento",
    sortable: true,
    align: "left",
  },
  {
    name: "documento",
    label: "DNI",
    field: "documento",
    sortable: true,
    align: "left",
  },
  {
    name: "puesto.name",
    label: "Puesto",
    field: (row) => {
      return puestosActuales.value[row.id] || "Contrato no activo";
    },
    sortable: true,
    align: "left",
  },
];

const puestosActuales = ref<{ [empleadoId: number]: string }>({});
onMounted(async () => {
  try {
    const response = await tallerApi.get("/contratos/activos");
    if (Array.isArray(response.data)) {
      response.data.forEach((item: { empleadoId: number; puesto: string }) => {
        puestosActuales.value[item.empleadoId] = item.puesto;
      });
    } else {
      console.warn("Respuesta inesperada:", response.data);
    }
  } catch (error) {
    console.error("Error al obtener los puestos actuales:", error);
  }
});
</script>
