<template>
  <CustomTable :columns="columns" title="Vehiculos" route="/vehiculos" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { Vehiculo } from "../types/entities/vehiculo";
import CustomTable from "src/components/CustomTable.vue";
import { ref, onMounted } from "vue";
import tallerApi from "../api/tallerApi";

interface Marca {
  id: string;
  nombre: string;
}

interface Modelo {
  id: string;
  nombre: string;
}

const marcas = ref<Record<string, string>>({});
const modelos = ref<Record<string, string>>({});

const cargarMarcas = async () => {
  try {
    const { data } = await tallerApi.get<Marca[]>("/marcas/all");
    marcas.value = data.reduce(
      (acc, marca) => {
        acc[marca.id] = marca.nombre;
        return acc;
      },
      {} as Record<string, string>,
    );
  } catch (error) {
    console.error("Error cargando marcas:", error);
  }
};

const cargarModelos = async () => {
  try {
    const { data } = await tallerApi.get<Modelo[]>("/modelos/all");
    modelos.value = data.reduce(
      (acc, modelo) => {
        acc[modelo.id] = modelo.nombre;
        return acc;
      },
      {} as Record<string, string>,
    );
  } catch (error) {
    console.error("Error cargando modelos:", error);
  }
};

onMounted(async () => {
  await Promise.all([cargarMarcas(), cargarModelos()]);
});

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
    field: (row) => row.marca?.nombre || "Sin marca",
    sortable: true,
    align: "left",
  },
  {
    name: "modelo",
    label: "Modelo",
    field: (row) => row.modelo?.nombre || "Sin modelo",
    sortable: true,
    align: "left",
  },
  {
    name: "cliente",
    label: "Cliente",
    field: (row) => row.cliente?.nombre || "Sin cliente",
    sortable: true,
    align: "left",
  },
];
</script>
