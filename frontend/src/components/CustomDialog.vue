<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="(val) => $emit('update:modelValue', val)"
    persistent
    transition-show="scale"
    transition-hide="scale"
    class="enhanced-dialog"
  >
    <q-card class="dialog-card">
      <q-card-section class="dialog-header">
        <div class="header-content">
          <div class="header-main">
            <div class="header-icon">
              <q-icon :name="getHeaderIcon()" />
            </div>
            <div class="header-text">
              <h2 class="dialog-title">{{ dialogTitle }}</h2>
              <p class="dialog-subtitle">{{ getSubtitle() }}</p>
            </div>
          </div>
        </div>
      </q-card-section>

      <q-card-section class="dialog-content">
        <component
          :is="currentForm"
          :edit-data="editData"
          :mode="editData?.id ? 'edit' : 'create'"
          @created="handleFormSubmit"
          @updated="handleFormSubmit"
          @cancel="handleCancel"
          class="full-height"
        />
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { computed } from "vue";
import ClientesForm from "./ClientesForm.vue";
import type { Component } from "vue";
import VehiculosForm from "./VehiculosForm.vue";
import ArticulosForm from "./ArticulosForm.vue";
import OrdenForm from "./OrdenForm.vue";
import EmpleadosForm from "./EmpleadosForm.vue";

type RouteKey = "/clientes" | "/vehiculos" | "/articulos" | "/ordenes" | "/empleados";

const emit = defineEmits(["update:modelValue", "created", "updated"]);

interface BaseEntity {
  id: number;
  [key: string]: unknown;
}

// Interfaz específica para modelos
interface ModeloEditData {
  id: number;
  nombre: string;
  marca: number | null;
}

const props = defineProps<{
  modelValue: boolean;
  route: string;
  editData?: BaseEntity | ModeloEditData | null | undefined;
}>();

type FormComponents = Record<RouteKey, Component>;

const formComponents: FormComponents = {
  "/clientes": ClientesForm,
  "/vehiculos": VehiculosForm,
  "/articulos": ArticulosForm,
  "/ordenes": OrdenForm,
  "/empleados": EmpleadosForm,
} as const;

const currentForm = computed(() => {
  const routeKey = props.route as RouteKey;

  return formComponents[routeKey];
});

type TitleMap = Record<RouteKey, { create: string; edit: string }>;

const dialogTitle = computed(() => {
  const titles: TitleMap = {
    "/clientes": { create: "Nuevo Cliente", edit: "Editar Cliente" },
    "/vehiculos": { create: "Nuevo Vehículo", edit: "Editar Vehículo" },
    "/articulos": { create: "Nuevo Artículo", edit: "Editar Artículo" },
    "/ordenes": { create: "Nueva Orden", edit: "Editar Orden" },
    "/empleados": { create: "Nuevo Empleado", edit: "Editar Empleado" },
  } as const;

  const routeKey = props.route as RouteKey;
  const isEditing = !!props.editData?.id;

  return titles[routeKey]
    ? isEditing
      ? titles[routeKey].edit
      : titles[routeKey].create
    : isEditing
      ? "Editar Entidad"
      : "Crear Entidad";
});

const getSubtitle = () => {
  const isEditing = !!props.editData?.id;
  const baseRoute = props.route.replace("/", "");

  const subtitles = {
    clientes: isEditing
      ? "Modifica la información del cliente seleccionado"
      : "Completa los datos para registrar un nuevo cliente",
    vehiculos: isEditing
      ? "Actualiza los datos del vehículo"
      : "Registra un nuevo vehículo en el sistema",
    articulos: isEditing
      ? "Modifica la información del artículo"
      : "Añade un nuevo artículo al inventario",
    ordenes: isEditing ? "Actualiza los detalles de la orden" : "Crea una nueva orden de trabajo",
    empleados: isEditing
      ? "Modifica la información del empleado seleccionado"
      : "Completa los datos para registrar un nuevo empleado",
  };

  return (
    subtitles[baseRoute as keyof typeof subtitles] ||
    (isEditing ? "Modifica la información" : "Completa los datos requeridos")
  );
};

const getHeaderIcon = () => {
  const baseRoute = props.route.replace("/", "");
  const isEditing = !!props.editData?.id;

  const icons = {
    clientes: isEditing ? "edit" : "person_add",
    vehiculos: isEditing ? "directions_car" : "add_road",
    articulos: isEditing ? "inventory" : "add_box",
    ordenes: isEditing ? "edit_note" : "note_add",
    empleados: isEditing ? "edit" : "person_add",
    marcas: isEditing ? "edit" : "add_business",
    modelos: isEditing ? "edit" : "add_business",
  };

  return icons[baseRoute as keyof typeof icons] || (isEditing ? "edit" : "add");
};

const handleFormSubmit = () => {
  if (props.editData?.id) {
    emit("updated");
  } else {
    emit("created");
  }
  emit("update:modelValue", false);
};

const handleCancel = () => {
  emit("update:modelValue", false);
};
</script>

<style scoped>
.enhanced-dialog {
  backdrop-filter: blur(8px);
}

.dialog-card {
  width: 900px;
  max-width: 95vw;
  height: 75vh;
  max-height: 800px;
  min-height: 600px;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  box-shadow:
    0 25px 50px -12px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  overflow: hidden;
}

.dialog-header {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
  padding: 2rem;
  flex: 0 0 auto;
  position: relative;
  overflow: hidden;
}

.dialog-header::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.header-content {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex: 1;
}

.header-icon {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 1rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.header-icon .q-icon {
  font-size: 2rem;
  color: white;
}

.header-text {
  flex: 1;
}

.dialog-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  color: white;
  line-height: 1.2;
}

.dialog-subtitle {
  font-size: 1rem;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
  font-weight: 400;
}

.close-btn {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  border-radius: 12px;
  width: 44px;
  height: 44px;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

.close-btn .q-icon {
  color: white;
  font-size: 1.25rem;
}

.dialog-content {
  flex: 1 1 auto;
  overflow: hidden;
  padding: 0;
  background: transparent;
}

.full-height {
  height: 100%;
}

/* Animaciones para el dialog */
.enhanced-dialog .q-dialog__inner {
  padding: 2rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .dialog-card {
    width: 100vw;
    height: 100vh;
    max-width: 100vw;
    max-height: 100vh;
    border-radius: 0;
  }

  .enhanced-dialog .q-dialog__inner {
    padding: 0;
  }

  .dialog-header {
    padding: 1.5rem 1rem;
  }

  .header-main {
    gap: 1rem;
  }

  .header-icon {
    padding: 0.75rem;
  }

  .header-icon .q-icon {
    font-size: 1.5rem;
  }

  .dialog-title {
    font-size: 1.5rem;
  }

  .dialog-subtitle {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .header-main {
    justify-content: center;
    text-align: center;
  }

  .close-btn {
    align-self: flex-end;
    position: absolute;
    top: 1rem;
    right: 1rem;
  }
}

.dialog-card {
  animation: dialogEnter 0.4s ease-out;
}

@keyframes dialogEnter {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>
