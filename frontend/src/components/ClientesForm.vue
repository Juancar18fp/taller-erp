<template>
  <div class="q-pa-md column full-height">
    <div class="text-h6 q-mb-md">
      {{ isEditing ? "Editar Cliente" : "Nuevo Cliente" }}
    </div>
    <q-scroll-area class="col">
      <q-form ref="formRef" @submit="onSubmit" class="q-gutter-y-md">
        <div class="row q-col-gutter-lg">
          <div class="col-6">
            <q-input
              v-model="clientForm.nombre"
              label="Nombre *"
              outlined
              dense
              lazy-rules
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="q-mb-md"
            />

            <q-input v-model="clientForm.titular" label="Titular" outlined dense class="q-mb-md" />

            <q-input
              v-model="clientForm.documento"
              label="DNI/NIE/CIF *"
              outlined
              dense
              lazy-rules
              :rules="[
                (val) => !!val || 'Documento obligatorio',
                (val) => /^[XYZ]?\d{7,8}[A-Z]$/i.test(val) || 'Formato inválido',
              ]"
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.email"
              label="Email"
              outlined
              type="email"
              dense
              lazy-rules
              :rules="[(val) => !val || /.+@.+\..+/.test(val) || 'Email inválido']"
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.telefono"
              label="Teléfono"
              outlined
              dense
              mask="##########"
              class="q-mb-md"
            />
          </div>

          <div class="col-6">
            <q-input
              v-model="clientForm.direccion"
              label="Dirección *"
              outlined
              dense
              lazy-rules
              :rules="[(val) => !!val || 'Dirección obligatoria']"
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.cp"
              label="Código Postal"
              outlined
              dense
              mask="#####"
              fill-mask
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.poblacion"
              label="Población *"
              outlined
              dense
              lazy-rules
              :rules="[(val) => !!val || 'Población obligatoria']"
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.provincia"
              label="Provincia"
              outlined
              dense
              class="q-mb-md"
            />

            <q-input v-model="clientForm.pais" label="País" outlined dense class="q-mb-md" />
          </div>
        </div>
      </q-form>
    </q-scroll-area>

    <q-card-actions align="right" class="sticky-actions q-px-none">
      <q-btn flat label="Cancelar" color="primary" @click="$emit('cancel')" />
      <q-btn
        :label="isEditing ? 'Actualizar' : 'Añadir'"
        color="primary"
        @click="submitForm"
        class="q-ml-sm"
      />
    </q-card-actions>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from "vue";
import { useQuasar } from "quasar";
import tallerApi from "../api/tallerApi";
import type { QForm, QNotifyCreateOptions } from "quasar";

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: (ClientPayload & { id: number }) | undefined;
  mode?: "create" | "edit";
}>();

const formRef = ref<QForm>();

interface ClientPayload {
  nombre: string;
  titular: string;
  documento: string;
  direccion: string;
  cp: string;
  poblacion: string;
  provincia: string;
  pais: string;
  email: string;
  telefono: string;
}

interface ApiError {
  response?: {
    data?: {
      message?: string;
      details?: string;
    };
    status?: number;
  };
  message?: string;
  config?: {
    url?: string;
    method?: string;
    data?: ClientPayload;
  };
}

const clientForm = ref<ClientPayload>({
  nombre: "",
  titular: "",
  documento: "",
  direccion: "",
  cp: "",
  poblacion: "",
  provincia: "",
  pais: "",
  email: "",
  telefono: "",
});

const isEditing = computed(() => props.mode === "edit" || !!props.editData?.id);

const resetForm = () => {
  clientForm.value = {
    nombre: "",
    titular: "",
    documento: "",
    direccion: "",
    cp: "",
    poblacion: "",
    provincia: "",
    pais: "",
    email: "",
    telefono: "",
  };
  formRef.value?.resetValidation();
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
      clientForm.value = {
        nombre: newData.nombre || "",
        titular: newData.titular || "",
        documento: newData.documento || "",
        direccion: newData.direccion || "",
        cp: newData.cp || "",
        poblacion: newData.poblacion || "",
        provincia: newData.provincia || "",
        pais: newData.pais || "",
        email: newData.email || "",
        telefono: newData.telefono || "",
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
    });
  },
  { immediate: true },
);

const onSubmit = async () => {
  try {
    const payload = { ...clientForm.value };

    // Limpiar campos opcionales vacíos
    (Object.keys(payload) as Array<keyof ClientPayload>).forEach((key) => {
      if (!payload[key]) payload[key] = "";
    });

    console.log("Estado de edición:", {
      isEditing: isEditing.value,
      mode: props.mode,
      editData: props.editData,
      hasId: !!props.editData?.id,
    });

    if (isEditing.value && props.editData?.id) {
      console.log("Actualizando cliente:", {
        id: props.editData.id,
        payload,
        url: `/clientes/${props.editData.id}`,
      });

      // Asegurarnos de que el ID está en el payload
      const updatePayload = {
        ...payload,
        id: props.editData.id,
      };

      const response = await tallerApi.put(`/clientes/${props.editData.id}`, updatePayload);
      console.log("Respuesta de actualización:", response);

      $q.notify({
        type: "positive",
        message: "Cliente actualizado exitosamente",
        position: "top",
      } as QNotifyCreateOptions);
      emit("updated");
    } else {
      console.log("Creando nuevo cliente:", {
        payload,
        url: "/clientes",
      });

      const response = await tallerApi.post("/clientes", payload);
      console.log("Respuesta de creación:", response);

      $q.notify({
        type: "positive",
        message: "Cliente creado exitosamente",
        position: "top",
      } as QNotifyCreateOptions);
      emit("created");
      resetForm();
    }
  } catch (error: unknown) {
    const apiError = error as ApiError;
    console.error("Error completo:", error);
    console.error("Error detallado:", {
      response: apiError.response?.data,
      message: apiError.message,
      status: apiError.response?.status,
      isEditing: isEditing.value,
      mode: props.mode,
      editData: props.editData,
      url: apiError.config?.url,
      method: apiError.config?.method,
      data: apiError.config?.data,
    });

    const errorMessage =
      apiError.response?.data?.message ||
      (isEditing.value ? "Error al actualizar cliente" : "Error al crear cliente");

    const notifyOptions: QNotifyCreateOptions = {
      type: "negative",
      message: errorMessage,
      position: "top",
    };

    if (apiError.response?.data?.details) {
      notifyOptions.caption = apiError.response.data.details;
    }

    $q.notify(notifyOptions);
  }
};

const submitForm = async () => {
  if (!formRef.value) {
    $q.notify({
      type: "negative",
      message: "Error al validar el formulario",
      position: "top",
    } as QNotifyCreateOptions);
    return;
  }

  try {
    const success = await formRef.value.validate();
    if (success) {
      formRef.value.submit();
    } else {
      $q.notify({
        type: "negative",
        message: "Por favor completa los campos requeridos correctamente",
        position: "top",
      } as QNotifyCreateOptions);
    }
  } catch (error) {
    console.error("Error al validar el formulario:", error);
    $q.notify({
      type: "negative",
      message: "Error al validar el formulario",
      position: "top",
    } as QNotifyCreateOptions);
  }
};
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
