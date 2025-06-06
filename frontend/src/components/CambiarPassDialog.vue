<template>
  <q-dialog v-model="isOpen" persistent>
    <q-card style="min-width: 400px">
      <q-card-section>
        <div class="text-h6">Cambiar Contraseña</div>
        <div class="text-subtitle2 text-grey-6">{{ empleadoNombre }}</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <q-form ref="passwordFormRef" @submit="cambiarPassword">
          <q-input
            v-model="passwordForm.passwordActual"
            label="Contraseña actual *"
            type="password"
            outlined
            dense
            :rules="[(val) => !!val || 'Campo requerido']"
            class="q-mb-md"
          />

          <q-input
            v-model="passwordForm.passwordNueva"
            label="Nueva contraseña *"
            type="password"
            outlined
            dense
            :rules="[validarPassword]"
            class="q-mb-md"
          />

          <q-input
            v-model="passwordForm.confirmarPassword"
            label="Confirmar nueva contraseña *"
            type="password"
            outlined
            dense
            :rules="[validarConfirmacion]"
            class="q-mb-md"
          />

          <div class="text-caption text-grey-6 q-mb-md">
            La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas y
            números.
          </div>
        </q-form>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="Cancelar" color="grey-7" @click="cerrar" />
        <q-btn
          label="Cambiar Contraseña"
          color="primary"
          :loading="loading"
          @click="cambiarPassword"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import { useQuasar } from "quasar";
import tallerApi from "../api/tallerApi";

const $q = useQuasar();
const emit = defineEmits(["close"]);

const props = defineProps<{
  modelValue: boolean;
  empleadoId: string | number;
  empleadoNombre: string;
}>();

const isOpen = ref(props.modelValue);
const loading = ref(false);
const passwordFormRef = ref();

const passwordForm = reactive({
  passwordActual: "",
  passwordNueva: "",
  confirmarPassword: "",
});

const validarPassword = (val: string) => {
  if (!val) return "Campo requerido";
  if (val.length < 8) return "Mínimo 8 caracteres";
  if (!/[A-Z]/.test(val)) return "Debe contener al menos una mayúscula";
  if (!/[a-z]/.test(val)) return "Debe contener al menos una minúscula";
  if (!/\d/.test(val)) return "Debe contener al menos un número";
  return true;
};

const validarConfirmacion = (val: string) => {
  if (!val) return "Campo requerido";
  if (val !== passwordForm.passwordNueva) return "Las contraseñas no coinciden";
  return true;
};

const cambiarPassword = async () => {
  try {
    const valid = await passwordFormRef.value?.validate();
    if (!valid) return;

    loading.value = true;

    const request = {
      passwordActual: passwordForm.passwordActual,
      passwordNueva: passwordForm.passwordNueva,
      confirmarPassword: passwordForm.confirmarPassword,
    };

    await tallerApi.put(`/empleados/${props.empleadoId}/cambiar-password`, request);

    $q.notify({
      type: "positive",
      message: "Contraseña actualizada correctamente",
    });

    cerrar();
  } catch (error: any) {
    $q.notify({
      type: "negative",
      message: error.response?.data?.error || "Error al cambiar la contraseña",
    });
  } finally {
    loading.value = false;
  }
};

const cerrar = () => {
  // Limpiar formulario
  passwordForm.passwordActual = "";
  passwordForm.passwordNueva = "";
  passwordForm.confirmarPassword = "";

  passwordFormRef.value?.resetValidation();
  isOpen.value = false;
  emit("close");
};

watch(
  () => props.modelValue,
  (val) => {
    isOpen.value = val;
  },
);
</script>
