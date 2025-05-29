<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <div class="form-section">
            <div class="section-header">
              <q-icon name="inventory" class="section-icon" />
              <h3 class="section-title">Información del Artículo</h3>
            </div>
            <div class="inputs-container">
              <div class="input-row">
                <CustomInput
                  v-model="form.descripcion"
                  label="Descripción *"
                  placeholder="Ingrese la descripción del artículo"
                  obligatorio
                  :rules="[required]"
                  class="input-large"
                />
                <CustomInput
                  v-model="form.proveedor"
                  label="Proveedor"
                  placeholder="Nombre del proveedor (opcional)"
                  class="input-large"
                />
              </div>
              <div class="input-row">
                <CustomInput
                  v-model="form.precio"
                  label="Precio *"
                  placeholder="Ingrese el precio"
                  obligatorio
                  :rules="[required, validarPrecio]"
                  class="input-large"
                />
                <CustomInput
                  v-model="form.stock"
                  label="Stock *"
                  placeholder="Ingrese el stock"
                  obligatorio
                  :rules="[required, validarStock]"
                  class="input-large"
                />
              </div>
            </div>
          </div>
        </div>
      </q-form>
    </q-scroll-area>
    <div class="form-actions">
      <q-btn
        flat
        label="Cancelar"
        color="grey-7"
        icon="close"
        @click="$emit('cancel')"
        class="cancel-btn"
      />
      <q-btn
        :label="editData ? 'Actualizar' : 'Crear'"
        :icon="editData ? 'update' : 'add'"
        color="primary"
        unelevated
        :loading="loading"
        @click="handleSubmit"
        class="submit-btn"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useQuasar } from "quasar";
import CustomInput from "./CustomInput.vue";
import tallerApi from "../api/tallerApi";
import type { ArticuloEditData } from "../types/entities/articulo";

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: ArticuloEditData;
}>();

const formRef = ref();
const loading = ref(false);

const form = ref({
  descripcion: "",
  precio: "0",
  stock: "0",
  proveedor: "",
});

const required = (val: string | null) => !!val || "Campo obligatorio";

const validarPrecio = (val: string | null) => {
  if (!val || val === "") return "Campo obligatorio";
  const precio = parseFloat(val);
  if (isNaN(precio) || precio < 0) return "Debe ser un número válido mayor o igual a 0";
  return true;
};

const validarStock = (val: string | null) => {
  if (!val || val === "") return "Campo obligatorio";
  const stock = parseInt(val);
  if (isNaN(stock) || stock < 0 || !Number.isInteger(parseFloat(val))) {
    return "Debe ser un número entero mayor o igual a 0";
  }
  return true;
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  loading.value = true;

  try {
    if (props.editData?.id) {
      await tallerApi.put(`/articulos/${props.editData.id}`, form.value);
      $q.notify({ type: "positive", message: "Artículo actualizado" });
      emit("updated");
    } else {
      await tallerApi.post("/articulos", form.value);
      $q.notify({ type: "positive", message: "Artículo creado" });
      emit("created");
      resetForm();
    }
  } catch (error) {
    console.error("Error al guardar artículo:", error);
    $q.notify({ type: "negative", message: "Error al guardar" });
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = {
    descripcion: "",
    precio: "0",
    stock: "0",
    proveedor: "",
  };
  formRef.value?.resetValidation();
};

watch(
  () => props.editData,
  (data) => {
    if (data) {
      form.value = { ...data };
    } else {
      resetForm();
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  height: 100%;
}
</style>
