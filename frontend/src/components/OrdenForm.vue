<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <div class="form-section">
            <div class="section-header">
              <q-icon name="account_circle" class="section-icon" />
              <h3 class="section-title">Información Personal</h3>
            </div>

            <div class="inputs-container">
              <CustomInput
                v-model="form.nombre"
                label="Nombre *"
                placeholder="Ingrese el nombre completo"
                obligatorio
                :rules="[required]"
              />

              <CustomInput
                v-model="form.titular"
                label="Titular (Empresas)"
                placeholder="Nombre del titular (opcional)"
              />

              <CustomInput
                v-model="form.documento"
                label="Documento de identidad *"
                placeholder="DNI, NIE o CIF"
                obligatorio
                :rules="[required, validDocument]"
              />
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <q-icon name="contact_mail" class="section-icon" />
              <h3 class="section-title">Información de Contacto</h3>
            </div>

            <div class="inputs-container">
              <CustomInput
                v-model="form.telefono"
                label="Número de teléfono"
                placeholder="Ej: 612345678"
                :rules="[validPhone]"
              />

              <CustomInput
                v-model="form.email"
                label="Correo electrónico"
                placeholder="ejemplo@correo.com"
                :rules="[validEmail]"
              />

              <div class="input-row">
                <CustomInput v-model="form.pais" label="País" placeholder="Ej: España" />
                <CustomInput v-model="form.provincia" label="Provincia" placeholder="Ej: Madrid" />
              </div>

              <div class="input-row">
                <CustomInput
                  v-model="form.poblacion"
                  label="Localidad"
                  placeholder="Ej: Madrid"
                  class="input-large"
                />
                <CustomInput
                  v-model="form.cp"
                  label="Código Postal"
                  placeholder="Ej: 28080"
                  class="input-small"
                />
              </div>

              <CustomInput
                v-model="form.direccion"
                label="Dirección"
                placeholder="Calle, número, puerta"
              />
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
import type { ClientePayload, ClienteEditData } from "../types/entities/cliente";

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: ClienteEditData;
}>();

const formRef = ref();
const loading = ref(false);

const form = ref<ClientePayload>({
  nombre: "",
  titular: null,
  documento: "",
  direccion: null,
  cp: null,
  poblacion: null,
  provincia: null,
  pais: null,
  email: null,
  telefono: null,
});

const required = (val: string | null) => !!val || "Campo obligatorio";

const validEmail = (val: string | null) => {
  if (!val) return true;
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || "Email inválido";
};

const validDocument = (val: string | null) => {
  if (!val) return true;
  const dniRegex = /^[XYZ]?\d{7,8}[A-Z]$/i;
  const cifRegex = /^[A-HJNPQRSUVW]\d{7}[0-9A-J]$/i;
  return dniRegex.test(val) || cifRegex.test(val) || "Formato inválido";
};

const validPhone = (val: string | null) => {
  if (!val) return true;
  return /^[6-9]\d{8}$/.test(val) || "Teléfono inválido";
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  loading.value = true;
  try {
    if (props.editData?.id) {
      await tallerApi.put(`/clientes/${props.editData.id}`, form.value);
      $q.notify({ type: "positive", message: "Cliente actualizado" });
      emit("updated");
    } else {
      await tallerApi.post("/clientes", form.value);
      $q.notify({ type: "positive", message: "Cliente creado" });
      emit("created");
      resetForm();
    }
  } catch {
    $q.notify({ type: "negative", message: "Error al guardar" });
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = {
    nombre: "",
    titular: null,
    documento: "",
    direccion: null,
    cp: null,
    poblacion: null,
    provincia: null,
    pais: null,
    email: null,
    telefono: null,
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
