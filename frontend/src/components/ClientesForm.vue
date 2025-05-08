<template>
  <div class="q-pa-md column full-height">
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

            <q-input
              v-model="clientForm.titular"
              hint=""
              label="Titular"
              outlined
              dense
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.documento"
              label="DNI/NIE/CIF *"
              outlined
              hint=""
              dense
              lazy-rules
              :rules="[(val) => !!val || 'Documento obligatorio']"
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.email"
              label="Email"
              hint=""
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
              hint=""
              outlined
              dense
              class="q-mb-md"
            />
          </div>

          <div class="col-6">
            <q-input
              hint=""
              v-model="clientForm.direccion"
              label="Dirección *"
              outlined
              dense
              lazy-rules
              :rules="[(val) => !!val || 'Dirección obligatoria']"
              class="q-mb-md"
            />

            <q-input
              hint=""
              v-model="clientForm.cp"
              label="Código Postal"
              outlined
              dense
              mask="#####"
              class="q-mb-md"
            />

            <q-input
              hint=""
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
              hint=""
              outlined
              dense
              class="q-mb-md"
            />

            <q-input
              v-model="clientForm.pais"
              label="País"
              hint=""
              outlined
              dense
              class="q-mb-md"
            />
          </div>
        </div>
      </q-form>
    </q-scroll-area>

    <!-- Botones fijos -->
    <q-card-actions align="right" class="sticky-actions q-px-none">
      <q-btn flat label="Cancelar" color="primary" @click="$emit('cancel')" />
      <q-btn label="Añadir" color="primary" @click="submitForm" class="q-ml-sm" />
    </q-card-actions>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useQuasar } from "quasar";
import tallerApi from "../api/tallerApi";
import type { QForm } from "quasar";

const $q = useQuasar();
const emit = defineEmits(["created", "cancel"]);
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

const onSubmit = async () => {
  try {
    await tallerApi.post("/clientes", clientForm.value);
    $q.notify({ type: "positive", message: "Cliente creado exitosamente" });
    emit("created");
    resetForm();
  } catch {
    $q.notify({
      type: "negative",
      message: "Error al crear cliente",
    });
  }
};

const submitForm = () => {
  if (formRef.value) {
    formRef.value.submit();
  }
};

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
