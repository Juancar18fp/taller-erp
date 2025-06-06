<template>
  <CustomTable :columns="columns" title="Ordenes" route="/ordenes" />
</template>

<script setup lang="ts">
import type { QTableColumn } from "quasar";
import type { OrdenTrabajo } from "../types/entities/orden_trabajo";
import CustomTable from "src/components/CustomTable.vue";

const formatearFecha = (fecha: string | null | undefined): string => {
  if (!fecha) return "-";
  try {
    const date = new Date(fecha);
    const dia = date.getDate().toString().padStart(2, "0");
    const mes = (date.getMonth() + 1).toString().padStart(2, "0");
    const anio = date.getFullYear();
    return `${dia}/${mes}/${anio}`;
  } catch {
    return "-";
  }
};

const getEstadoDisplay = (estado: string | undefined): string => {
  const nombres = {
    PENDIENTE: "Pendiente",
    INICIADA: "En Progreso",
    FINALIZADA: "Finalizada",
    PENDIENTE_PAGO: "Pendiente de Pago",
    COMPLETADA: "Completada",
    CANCELADA: "Cancelada",
  };
  return nombres[estado as keyof typeof nombres] || estado || "-";
};

const columns: QTableColumn[] = [
  {
    name: "codigoOrden",
    label: "Código de Orden",
    field: (row) => (row as OrdenTrabajo).codigoOrden || "-",
    sortable: true,
    required: true,
    align: "left",
  },
  {
    name: "vehiculo",
    label: "Vehículo",
    field: (row) => {
      const orden = row as OrdenTrabajo;
      return orden.vehiculo?.matricula || "-";
    },
    sortable: true,
    align: "left",
  },
  {
    name: "empleado",
    label: "Empleado",
    field: (row) => {
      const orden = row as OrdenTrabajo;
      return orden.empleadoAsignado?.nombre || "Sin asignar";
    },
    sortable: true,
    align: "left",
  },
  {
    name: "fechaOrden",
    label: "Fecha Orden",
    field: (row) => {
      const orden = row as OrdenTrabajo;
      return formatearFecha(orden.fechaOrden);
    },
    sortable: true,
    align: "center",
    style: "font-family: monospace;",
  },
  {
    name: "estado",
    label: "Estado",
    field: (row) => {
      const orden = row as OrdenTrabajo;
      return getEstadoDisplay(orden.estadoOrden?.nombre);
    },
    sortable: true,
    align: "center",
    format: (val: string) => val,
  },
  {
    name: "total",
    label: "Total",
    field: (row) => {
      const orden = row as OrdenTrabajo;
      return orden.total || 0;
    },
    sortable: true,
    align: "center",
    format: (val: number) => val.toFixed(2),
  },
];
</script>
