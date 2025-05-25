<template>
  <div class="q-pa-md column full-height">
    <div class="text-h6 q-mb-md">
      {{ isEditing ? "Editar Cliente" : "Nuevo Cliente" }}
    </div>
    <q-scroll-area class="col">
      <q-form ref="formRef" @submit="handleFormSubmit" class="q-gutter-y-md">
        <div class="row q-col-gutter-lg">
          <div class="col-6">
            <CustomInput
              v-model="clienteForm.nombre"
              label="Nombre *"
              obligatorio
              :rules="[(val: string | null) => !!val || 'Campo obligatorio']"
            />
            <CustomInput v-model="clienteForm.titular" label="Titular" :rules="[]" />
            <CustomInput
              v-model="clienteForm.documento"
              label="DNI/NIE/CIF *"
              obligatorio
              :rules="[
                (val: string | null) => !!val || 'Campo obligatorio',
                (val: string | null) => !val || isValidDocument(val) || 'Formato inválido',
              ]"
            />
          </div>

          <div class="col-6">
            <CustomInput
              v-model="clienteForm.telefono"
              label="Teléfono"
              :rules="[(val: string | null) => !val || isValidPhone(val) || 'Teléfono inválido']"
            />
            <CustomInput
              v-model="clienteForm.email"
              label="Email"
              :rules="[(val: string | null) => !val || isValidEmail(val) || 'Email inválido']"
            />
            <CustomInput v-model="clienteForm.direccion" label="Dirección" />
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
import { useFormHandler } from "../composables/useFormHandler";
import type { ClientePayload } from "../types/entities";
import type { AxiosError } from "axios";

interface ClienteEditData extends ClientePayload {
  id: number;
}

const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: ClienteEditData | undefined;
  mode?: "create" | "edit";
}>();

const clienteForm = ref<ClientePayload>({
  nombre: "",
  titular: null,
  documento: "",
  telefono: null,
  email: null,
  direccion: null,
});

const isEditing = computed(() => props.mode === "edit" || !!props.editData?.id);

const { formRef, isLoading, handleSubmit } = useFormHandler<ClientePayload>({
  endpoint: "/clientes",
  successMessage: isEditing.value
    ? "Cliente actualizado exitosamente"
    : "Cliente creado exitosamente",
  errorMessage: isEditing.value ? "Error al actualizar cliente" : "Error al crear cliente",
});

const resetForm = () => {
  clienteForm.value = {
    nombre: "",
    titular: null,
    documento: "",
    telefono: null,
    email: null,
    direccion: null,
  };
  formRef.value?.resetValidation();
};

const isValidEmail = (email: string) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

const isValidDocument = (document: string) => {
  // Validación para DNI/NIE/CIF
  const dniRegex = /^[XYZ]?\d{7,8}[A-Z]$/i;
  const cifRegex = /^[A-HJNPQRSUVW]\d{7}[0-9A-J]$/i;
  return dniRegex.test(document) || cifRegex.test(document);
};

const isValidPhone = (phone: string) => {
  const phoneRegex = /^[6-9]\d{8}$/;
  return phoneRegex.test(phone);
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
    ...clienteForm.value,
    ...(isEditing.value && props.editData?.id ? { id: props.editData.id } : {}),
    titular: clienteForm.value.titular || null,
    telefono: clienteForm.value.telefono || null,
    email: clienteForm.value.email || null,
    direccion: clienteForm.value.direccion || null,
  };

  console.log("Enviando datos del cliente:", {
    isEditing: isEditing.value,
    id: props.editData?.id,
    formData,
  });

  try {
    await handleSubmit(formData, isEditing.value, props.editData?.id, () => {
      if (!isEditing.value) {
        resetForm();
      }
      emit(isEditing.value ? "updated" : "created");
    });
  } catch (error: unknown) {
    const axiosError = error as AxiosError;
    console.error("Error al enviar formulario:", axiosError.response?.data || axiosError);
  }
};

watch(
  () => props.editData,
  (newData) => {
    console.log("editData cambiado:", {
      newData,
      hasId: !!newData?.id,
      mode: props.mode,
    });
    if (newData) {
      clienteForm.value = {
        nombre: newData.nombre || "",
        titular: newData.titular || null,
        documento: newData.documento || "",
        telefono: newData.telefono || null,
        email: newData.email || null,
        direccion: newData.direccion || null,
      };
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
