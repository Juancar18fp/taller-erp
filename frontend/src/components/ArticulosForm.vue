<template>
  <div class="q-pa-md column full-height">
    <q-scroll-area class="col">
      <q-form ref="formRef" @submit="handleFormSubmit" class="q-gutter-y-md">
        <div class="row q-col-gutter-lg">
          <div class="col-6">
            <CustomInput
              v-model="articuloForm.descripcion"
              label="Descripción *"
              obligatorio
              :rules="[(val: string | null) => !!val || 'Campo obligatorio']"
            />
            <CustomInput
              v-model="precioStr"
              label="Precio *"
              type="number"
              obligatorio
              :rules="[(val: string | null) => !!val || 'Campo obligatorio']"
            />
          </div>

          <div class="col-6">
            <CustomInput v-model="stockStr" label="Stock*" type="number" />
            <CustomInput
              v-model="articuloForm.proveedor"
              label="Proveedor"
              :rules="[(val: string | null) => !!val || 'Campo obligatorio']"
            />
          </div>
        </div>
      </q-form>
    </q-scroll-area>

    <q-card-actions align="right" class="sticky-actions q-px-none">
      <q-btn flat label="Cancelar" color="primary" @click="$emit('cancel')" />
      <q-btn
        :label="isEditing ? 'Actualizar' : 'Añadir'"
        color="primary"
        :loading="isLoading"
        @click="handleFormSubmit"
        class="q-ml-sm"
      />
    </q-card-actions>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import CustomInput from "./CustomInput.vue";
import { useEntityForm } from "../composables/useEntityForm";
import type { ArticuloPayload } from "../types/entities";

interface ArticuloEditData extends ArticuloPayload {
  id: number;
}

const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: ArticuloEditData | undefined;
  mode?: "create" | "edit";
}>();

const articuloForm = ref<ArticuloPayload>({
  descripcion: "",
  precio: 0,
  stock: 0,
  proveedor: "",
});

const precioStr = ref<string>("");
const stockStr = ref<string>("");

const isEditing = computed(() => props.mode === "edit" || !!props.editData?.id);

const { formRef, isLoading, submitForm } = useEntityForm<ArticuloPayload>({
  endpoint: "/articulos",
  successMessage: isEditing.value
    ? "Artículo actualizado exitosamente"
    : "Artículo creado exitosamente",
  errorMessage: isEditing.value ? "Error al actualizar artículo" : "Error al crear artículo",
  onSuccess: () => {
    if (!isEditing.value) {
      resetForm();
    }
    emit(isEditing.value ? "updated" : "created");
  },
});

const resetForm = () => {
  articuloForm.value = {
    descripcion: "",
    precio: 0,
    stock: 0,
    proveedor: "",
  };
  precioStr.value = "";
  stockStr.value = "";
  formRef.value?.resetValidation();
};

const handleFormSubmit = async (evt: Event) => {
  evt.preventDefault();

  if (!formRef.value) {
    console.error("Error: Referencia del formulario no encontrada");
    return;
  }

  const success = await formRef.value.validate();
  if (!success) {
    console.error("Error: Validación del formulario fallida");
    return;
  }

  const formData = {
    ...articuloForm.value,
    precio: Number(precioStr.value) || 0,
    stock: Number(stockStr.value) || 0,
    ...(isEditing.value && props.editData?.id ? { id: props.editData.id } : {}),
  };

  await submitForm(formData, props.editData?.id);
};

watch(
  () => props.editData,
  (newData) => {
    if (newData) {
      articuloForm.value = {
        descripcion: newData.descripcion || "",
        precio: newData.precio || 0,
        stock: newData.stock || 0,
        proveedor: newData.proveedor || "",
      };
      precioStr.value = newData.precio?.toString() || "";
      stockStr.value = newData.stock?.toString() || "";
    } else {
      resetForm();
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.sticky-actions {
  position: sticky;
  bottom: 0;
  background: white;
  z-index: 1000;
  padding: 16px 0;
  border-top: 1px solid rgba(0, 0, 0, 0.12);
  margin-top: auto;
}

.full-height {
  height: 100%;
}
</style>
