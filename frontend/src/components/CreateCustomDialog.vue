<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="(val) => $emit('update:modelValue', val)"
    persistent
  >
    <q-card class="dialog-card">
      <q-card-section class="dialog-header">
        <div class="text-h4">{{ dialogTitle }}</div>
      </q-card-section>

      <q-card-section class="dialog-content q-pt-none">
        <component
          :is="currentForm"
          @created="handleFormSubmit"
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

type RouteKey = "/clientes" | "/vehiculos" | "/articulos" | "/ordenes";

const emit = defineEmits(["update:modelValue", "created"]);

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  route: {
    type: String,
    required: true,
  },
});

type FormComponents = Record<RouteKey, Component>;

const formComponents: FormComponents = {
  "/clientes": ClientesForm,
  "/vehiculos": VehiculosForm,
  "/articulos": ClientesForm,
  "/ordenes": ClientesForm,
} as const;

const currentForm = computed(() => {
  const baseRoute = props.route.split("/")[1] as RouteKey extends `/${infer T}` ? T : never;
  const routeKey = `/${baseRoute}` as RouteKey;
  return formComponents[routeKey] || ClientesForm;
});

type TitleMap = Record<RouteKey, string>;

const dialogTitle = computed(() => {
  const titles: TitleMap = {
    "/clientes": "Cliente",
    "/vehiculos": "Vehículo",
    "/articulos": "Artículo",
    "/ordenes": "Orden",
  } as const;

  const baseRoute = props.route.split("/")[1];
  const routeKey = `/${baseRoute}` as RouteKey;
  return `Crear ${titles[routeKey] || "Entidad"}`;
});

const handleFormSubmit = () => {
  emit("created");
  emit("update:modelValue", false);
};

const handleCancel = () => {
  emit("update:modelValue", false);
};
</script>

<style scoped>
.dialog-card {
  width: 800px;
  max-width: 90vw;
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  flex: 0 0 auto;
  padding-bottom: 0;
}

.dialog-content {
  flex: 1 1 auto;
  overflow: hidden;
  padding-top: 0;
}

.full-height {
  height: 100%;
}
</style>
