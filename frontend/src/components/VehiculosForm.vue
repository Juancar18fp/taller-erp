<template>
  <div class="q-pa-md column full-height">
    <div class="text-h6 q-mb-md">
      {{ isEditing ? "Editar Vehículo" : "Nuevo Vehículo" }}
    </div>
    <q-scroll-area class="col">
      <q-form ref="formRef" @submit="handleFormSubmit" class="q-gutter-y-md">
        <div class="row q-col-gutter-lg">
          <div class="col-6">
            <CustomInput
              v-model="vehiculoForm.matricula"
              label="Matrícula *"
              obligatorio
              :rules="[(val: string | null) => !!val || 'Campo obligatorio']"
            />
            <CustomInput
              v-model="clienteInfo.id"
              label="ID Cliente *"
              hint="Enter para comprobar"
              :rules="[(val: string | null) => !!val || 'Campo obligatorio']"
              @keyup.enter.prevent="() => buscarCliente(clienteInfo.id)"
            />

            <CustomInput
              v-if="clienteInfo.nombre"
              v-model="clienteInfo.nombre"
              label="Nombre del Cliente"
              readonly
            />
          </div>

          <div class="col-6">
            <q-select
              v-model="marcaSeleccionada"
              :options="marcas"
              option-label="nombre"
              option-value="id"
              label="Marca *"
              :rules="[(val: string) => !!val || 'Campo obligatorio']"
              map-options
              emit-value
              outlined
              dense
              clearable
              @update:model-value="cambiarMarca"
            />

            <q-select
              v-model="modeloSeleccionado"
              :options="modelos"
              option-label="nombre"
              option-value="id"
              label="Modelo *"
              :rules="[(val: string) => !!val || 'Campo obligatorio']"
              :disable="!marcaSeleccionada"
              map-options
              emit-value
              outlined
              dense
              clearable
            />

            <CustomInput
              v-model="vehiculoForm.matriculacion"
              label="Año de matriculación *"
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
import { ref, computed, watch, onMounted } from "vue";
import CustomInput from "./CustomInput.vue";
import { useEntityForm } from "../composables/useEntityForm";
import { useClienteSearch } from "../composables/useClienteSearch";
import { useMarcaModelo } from "../composables/useMarcaModelo";
import type { VehiculoPayload, VehiculoApiPayload, VehiculoEditData } from "../types/entities";

const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: VehiculoEditData | undefined;
  mode?: "create" | "edit";
}>();

const vehiculoForm = ref<VehiculoPayload>({
  matricula: "",
  marca: { id: "" },
  modelo: { id: "" },
  matriculacion: "",
  cliente: {
    id: "",
    nombre: "",
  },
});

const isEditing = computed(() => {
  console.log("Calculando isEditing:", {
    mode: props.mode,
    hasEditData: !!props.editData?.id,
    editData: props.editData,
  });
  return props.mode === "edit" || !!props.editData?.id;
});

const successMessage = computed(() =>
  isEditing.value ? "Vehículo actualizado exitosamente" : "Vehículo creado exitosamente",
);

const errorMessage = computed(() =>
  isEditing.value ? "Error al actualizar vehículo" : "Error al crear vehículo",
);

const { formRef, isLoading, submitForm, resetForm } = useEntityForm<
  VehiculoPayload,
  VehiculoApiPayload
>({
  endpoint: "/vehiculos",
  transformPayload: (data) => ({
    ...data,
    marca: data.marca.id,
    modelo: data.modelo.id,
  }),
  successMessage: successMessage.value,
  errorMessage: errorMessage.value,
  onSuccess: () => {
    console.log("Operación completada con éxito");
    if (!isEditing.value) {
      resetForm();
    }
    emit(isEditing.value ? "updated" : "created");
  },
  onError: (error) => {
    console.error("Error en submitForm:", error);
  },
});

const { clienteInfo, buscarCliente } = useClienteSearch();
const {
  marcas,
  modelos,
  marcaSeleccionada,
  modeloSeleccionado,
  cargarMarcas,
  cargarModelosPorMarca,
  cambiarMarca,
} = useMarcaModelo();

const handleFormSubmit = async (evt: Event) => {
  evt.preventDefault();
  const formData = {
    ...vehiculoForm.value,
    marca: { id: marcaSeleccionada.value },
    modelo: { id: modeloSeleccionado.value },
    cliente: {
      id: clienteInfo.value.id,
      nombre: clienteInfo.value.nombre,
    },
  };

  if (isEditing.value && !props.editData?.id) {
    console.error("Error: No se proporcionó ID para actualización");
    return;
  }

  await submitForm(formData, props.editData?.id);
};

watch(
  () => props.editData,
  async (newData) => {
    console.log("editData cambiado:", {
      newData,
      hasId: !!newData?.id,
      mode: props.mode,
      isEditing: isEditing.value,
    });
    if (newData) {
      const marcaId = typeof newData.marca === "object" ? newData.marca.id : newData.marca;
      const modeloId = typeof newData.modelo === "object" ? newData.modelo.id : newData.modelo;

      console.log("IDs extraídos:", { marcaId, modeloId });

      if (marcaId) {
        await cargarModelosPorMarca(marcaId.toString());
        marcaSeleccionada.value = marcaId.toString();
        if (modeloId) {
          modeloSeleccionado.value = modeloId.toString();
        }
      }

      vehiculoForm.value = {
        matricula: newData.matricula || "",
        marca: { id: marcaId?.toString() || "" },
        modelo: { id: modeloId?.toString() || "" },
        matriculacion: newData.matriculacion?.toString() || "",
        cliente: {
          id: newData.cliente.id?.toString() || "",
          nombre: newData.cliente.nombre || "",
        },
      };

      clienteInfo.value = {
        id: newData.cliente.id?.toString() || "",
        nombre: newData.cliente.nombre || "",
      };
    } else {
      resetForm();
    }
  },
  { immediate: true },
);

watch(
  () => props.mode,
  (newMode) => {
    console.log("Modo cambiado:", {
      newMode,
      editData: props.editData,
      hasId: !!props.editData?.id,
      isEditing: isEditing.value,
    });
  },
  { immediate: true },
);

onMounted(() => {
  void cargarMarcas();
});
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
