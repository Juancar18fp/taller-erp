<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <div class="form-section">
            <div class="section-header">
              <q-icon name="person" class="section-icon" />
              <h3 class="section-title">Información Personal</h3>
            </div>

            <div class="inputs-container">
              <CustomInput
                v-model="form.nombre"
                label="Nombre completo *"
                placeholder="Ingrese el nombre completo del empleado"
                obligatorio
              />

              <div class="input-row">
                <CustomInput
                  class="input-large"
                  v-model="form.documento"
                  label="Documento de identidad *"
                  placeholder="DNI o NIE"
                  obligatorio
                  :rules="[validDocument]"
                />
                <CustomInput
                  class="input-large"
                  v-model="form.numeroSeguridadSocial"
                  label="Número de SS"
                  placeholder="Ej: 281234567890"
                  :rules="[validSSN]"
                />
              </div>

              <div class="input-row">
                <q-input
                  v-model="form.fechaNacimiento"
                  label="Fecha de nacimiento"
                  type="date"
                  outlined
                  dense
                  class="date-input input-large"
                />
                <q-select
                  class="input-large"
                  v-model="estadoCivilSeleccionado"
                  :options="estadosCiviles"
                  label="Estado civil"
                  option-label="nombre"
                  option-value="id"
                  outlined
                  dense
                  emit-value
                  map-options
                />
              </div>
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

          <div class="form-section contracts-section">
            <div class="section-header">
              <q-icon name="description" class="section-icon" />
              <h3 class="section-title">Contratos</h3>
              <q-space />
              <q-select
                class="input-large"
                v-model="rolSeleccionado"
                :options="roles"
                label="Rol del empleado *"
                option-label="nombre"
                option-value="id"
                outlined
                dense
                emit-value
                map-options
              />
              <q-btn
                icon="add"
                size="sm"
                color="primary"
                round
                flat
                @click="agregarContrato"
                :tooltip="'Agregar nuevo contrato'"
              >
                <q-tooltip>Agregar nuevo contrato</q-tooltip>
              </q-btn>
            </div>

            <div class="contracts-container" v-if="form.contratos.length > 0">
              <div v-for="(contrato, index) in form.contratos" :key="index" class="contract-card">
                <div class="contract-header">
                  <div class="contract-title">
                    <q-icon name="work" class="contract-icon" />
                    <span>Contrato {{ index + 1 }}</span>
                    <q-chip
                      :color="contrato.activo ? 'green' : 'grey'"
                      text-color="white"
                      size="sm"
                    >
                      {{ contrato.activo ? "Activo" : "Inactivo" }}
                    </q-chip>
                  </div>
                  <div class="contract-actions">
                    <q-btn
                      icon="visibility"
                      size="sm"
                      flat
                      round
                      color="primary"
                      @click="toggleContractVisibility(index)"
                    >
                      <q-tooltip>{{ contrato.expanded ? "Contraer" : "Expandir" }}</q-tooltip>
                    </q-btn>
                    <q-btn
                      icon="delete"
                      size="sm"
                      flat
                      round
                      color="negative"
                      @click="eliminarContrato(index)"
                    >
                      <q-tooltip>Eliminar contrato</q-tooltip>
                    </q-btn>
                  </div>
                </div>

                <q-slide-transition>
                  <div v-show="contrato.expanded" class="contract-content">
                    <div class="contract-inputs">
                      <div class="input-row">
                        <q-select
                          v-model="contrato.puesto.id"
                          :options="puestos"
                          label="Puesto de trabajo *"
                          option-label="nombre"
                          option-value="id"
                          outlined
                          dense
                          emit-value
                          map-options
                          class="input-large"
                        />
                        <q-input
                          v-model="contrato.fechaContratacion"
                          label="Fecha de contratación *"
                          type="date"
                          outlined
                          dense
                          class="date-input input-large"
                        />
                      </div>

                      <div class="input-row">
                        <q-select
                          v-model="contrato.tipoContrato.id"
                          :options="tiposContrato"
                          label="Tipo de contrato *"
                          option-label="nombre"
                          option-value="id"
                          outlined
                          dense
                          emit-value
                          map-options
                          class="input-large"
                        />
                        <q-select
                          v-model="contrato.jornadaLaboral.id"
                          :options="jornadasLaborales"
                          label="Jornada laboral *"
                          option-label="nombre"
                          option-value="id"
                          outlined
                          dense
                          emit-value
                          map-options
                          class="input-large"
                        />
                      </div>

                      <div class="input-row">
                        <q-input
                          v-model.number="contrato.salario"
                          label="Salario (€) *"
                          type="number"
                          outlined
                          dense
                          :rules="[validSalary]"
                          prefix="€"
                          class="input-large"
                        />
                        <CustomInput
                          v-model="contrato.numeroCuenta"
                          label="Número de cuenta bancaria"
                          placeholder="ES00 0000 0000 0000 0000 0000"
                          :rules="[validIBAN]"
                          class="input-large"
                        />
                      </div>

                      <div class="input-row">
                        <q-input
                          v-model="contrato.fechaFinalizacion"
                          label="Fecha de finalización"
                          type="date"
                          outlined
                          dense
                          class="date-input input-large"
                        />
                        <div class="contract-toggle-container">
                          <q-toggle
                            :model-value="contrato.activo"
                            @update:model-value="setContratoActivo(index)"
                            label="Contrato activo"
                            color="green"
                            class="contract-toggle"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </q-slide-transition>
              </div>
            </div>

            <div v-else class="no-contracts">
              <q-icon name="work_off" size="48px" color="grey-5" />
              <p class="text-grey-6">No hay contratos registrados</p>
              <q-btn
                label="Agregar contrato"
                icon="add"
                color="primary"
                outline
                @click="agregarContrato"
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
        :label="editData ? 'Actualizar Empleado' : 'Crear Empleado'"
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
import { onMounted, ref, watch } from "vue";
import { useQuasar } from "quasar";
import CustomInput from "./CustomInput.vue";
import tallerApi from "../api/tallerApi";
import type { Contrato, EmpleadoEditData, EmpleadoPayload } from "src/types/entities/empleado";
import { useEmpleadosForm } from "src/composables/useEmpleadosForm";

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: EmpleadoEditData;
}>();

const formRef = ref();
const loading = ref(false);

const form = ref<EmpleadoPayload>({
  nombre: "",
  fechaNacimiento: "",
  documento: "",
  direccion: "",
  cp: "",
  poblacion: "",
  provincia: "",
  pais: "",
  email: "",
  telefono: "",
  numeroSeguridadSocial: "",
  estadoCivil: {
    id: "",
  },
  rol: {
    id: "",
  },
  contratos: [],
});

const {
  estadoCivilSeleccionado,
  rolSeleccionado,
  estadosCiviles,
  jornadasLaborales,
  tiposContrato,
  puestos,
  roles,
  cargarEstadoCivil,
  cargarJornadasLaborales,
  cargarTiposContrato,
  cargarPuestos,
  cargarRoles,
} = useEmpleadosForm();

const validEmail = (val: string | null) => {
  if (!val) return true;
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || "Email inválido";
};

const validDocument = (val: string | null) => {
  if (!val) return "Documento requerido";
  const dniRegex = /^[XYZ]?\d{7,8}[A-Z]$/i;
  const nieRegex = /^[XYZ]\d{7}[A-Z]$/i;
  return dniRegex.test(val) || nieRegex.test(val) || "Formato de documento inválido";
};

const validPhone = (val: string | null) => {
  if (!val) return true;
  return /^[6-9]\d{8}$/.test(val) || "Teléfono inválido";
};

const validSSN = (val: string | null) => {
  if (!val) return true;
  return /^\d{12}$/.test(val) || "Número de Seguridad Social inválido";
};

const validSalary = (val: number | null | undefined) => {
  if (val === null || val === undefined || val === 0) return "Salario requerido";
  return val > 0 || "El salario debe ser mayor a 0";
};

const validIBAN = (val: string | null) => {
  if (!val) return true;
  const cleanIban = val.replace(/\s/g, "");
  const ibanRegex = /^ES\d{22}$/;
  return ibanRegex.test(cleanIban) || "IBAN inválido";
};

const agregarContrato = () => {
  const nuevoContrato: Contrato = {
    empleado: {
      id: "",
    },
    puesto: { id: "" },
    fechaContratacion: "",
    fechaFinalizacion: "",
    tipoContrato: { id: "" },
    jornadaLaboral: { id: "" },
    salario: 0,
    numeroCuenta: "",
    activo: form.value.contratos.length === 0,
    expanded: true,
  };

  form.value.contratos.push(nuevoContrato);
};

const eliminarContrato = async (index: number) => {
  const contratoEliminado = form.value.contratos[index];
  if (contratoEliminado && contratoEliminado.id) {
    await tallerApi.delete(`/contratos/${contratoEliminado.id}`);
    $q.notify({ type: "negative", message: "Contrato eliminado correctamente" });
  }

  form.value.contratos.splice(index, 1);
};

const setContratoActivo = (index: number) => {
  const contratoActual = form.value.contratos[index];
  if (!contratoActual) return;

  if (contratoActual.activo) {
    contratoActual.activo = false;
  } else {
    form.value.contratos.forEach((contrato, i) => {
      contrato.activo = i === index;
    });
  }
};

const handleSubmit = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    loading.value = true;

    const payload = {
      id: props.editData?.id || undefined,
      nombre: form.value.nombre,
      fechaNacimiento: form.value.fechaNacimiento || null,
      documento: form.value.documento,
      direccion: form.value.direccion,
      cp: form.value.cp,
      poblacion: form.value.poblacion,
      provincia: form.value.provincia,
      pais: form.value.pais,
      email: form.value.email,
      telefono: form.value.telefono,
      numeroSeguridadSocial: form.value.numeroSeguridadSocial,
      rol: { id: rolSeleccionado.value },
      estadoCivil: { id: estadoCivilSeleccionado.value },
    };

    let empleadoId;

    if (props.editData?.id) {
      await tallerApi.put(`/empleados/${props.editData.id}`, payload);
      empleadoId = props.editData.id;
      $q.notify({ type: "positive", message: "Empleado actualizado correctamente" });
      emit("updated");
    } else {
      const response = await tallerApi.post("/empleados", payload);
      empleadoId = response.data.id;
      $q.notify({ type: "positive", message: "Empleado creado correctamente" });
      emit("created");
    }

    if (form.value.contratos.length > 0 && empleadoId) {
      await guardarContratosDelEmpleado(empleadoId);
    }

    if (!props.editData?.id) {
      resetForm();
    }
  } catch (error) {
    console.error("Error al guardar empleado:", error);
    $q.notify({ type: "negative", message: "Error al guardar el empleado" });
  } finally {
    loading.value = false;
  }
};

interface ContratoPayLoad {
  id?: string;
  puesto: { id: string };
  fechaContratacion: string;
  tipoContrato: { id: string };
  jornadaLaboral: { id: string };
  salario: number;
  numeroCuenta: string;
  fechaFinalizacion: string;
  activo: boolean;
  empleado: { id: string };
}

const guardarContratosDelEmpleado = async (empleadoId: string) => {
  try {
    for (const contrato of form.value.contratos) {
      if (contrato.id && props.editData?.id) {
        const contratoPayload: ContratoPayLoad = {
          id: contrato.id,
          puesto: { id: contrato.puesto.id },
          fechaContratacion: contrato.fechaContratacion || "",
          tipoContrato: { id: contrato.tipoContrato.id },
          jornadaLaboral: { id: contrato.jornadaLaboral.id },
          salario: Number(contrato.salario),
          numeroCuenta: contrato.numeroCuenta || "",
          activo: contrato.activo,
          fechaFinalizacion: contrato.fechaFinalizacion || "",
          empleado: { id: empleadoId },
        };
        await tallerApi.put(`/contratos/${contrato.id}`, contratoPayload);
      } else {
        const contratoPayload: ContratoPayLoad = {
          puesto: { id: contrato.puesto.id },
          fechaContratacion: contrato.fechaContratacion || "",
          tipoContrato: { id: contrato.tipoContrato.id },
          jornadaLaboral: { id: contrato.jornadaLaboral.id },
          salario: Number(contrato.salario),
          numeroCuenta: contrato.numeroCuenta || "",
          activo: contrato.activo,
          empleado: { id: empleadoId },
          fechaFinalizacion: contrato.fechaFinalizacion || "",
        };
        await tallerApi.post("/contratos", contratoPayload);
      }
    }
    $q.notify({ type: "positive", message: "Contratos guardados correctamente" });
  } catch (error) {
    console.error("Error al guardar contratos:", error);
    $q.notify({
      type: "warning",
      message: "Empleado guardado, pero hubo errores con los contratos",
    });
  }
};

const toggleContractVisibility = (index: number) => {
  const contrato = form.value.contratos[index];
  if (contrato) {
    contrato.expanded = !contrato.expanded;
  }
};

const resetForm = () => {
  form.value = {
    nombre: "",
    fechaNacimiento: "",
    documento: "",
    direccion: "",
    cp: "",
    poblacion: "",
    provincia: "",
    pais: "",
    email: "",
    telefono: "",
    numeroSeguridadSocial: "",
    estadoCivil: {
      id: "",
    },
    rol: {
      id: "",
    },
    contratos: [],
  };

  formRef.value?.resetValidation();
  estadoCivilSeleccionado.value = "";
  rolSeleccionado.value = "";
};

watch(
  () => props.editData,
  (data) => {
    if (data) {
      form.value = {
        nombre: data.nombre || "",
        fechaNacimiento: data.fechaNacimiento || "",
        documento: data.documento || "",
        direccion: data.direccion || "",
        cp: data.cp || "",
        poblacion: data.poblacion || "",
        provincia: data.provincia || "",
        pais: data.pais || "",
        estadoCivil: data.estadoCivil || { id: "" },
        telefono: data.telefono || "",
        email: data.email || "",
        numeroSeguridadSocial: data.numeroSeguridadSocial || "",
        rol: data.rol || { id: "" },
        contratos: data.contratos
          ? data.contratos.map((contrato) => ({
              ...contrato,
              salario: Number(contrato.salario) || 0,
              expanded: false,
            }))
          : [],
      };

      if (data.contratos && data.contratos.length > 0) {
        data.contratos.forEach((contrato, index) => {
          const puestoId =
            typeof contrato.puesto === "object" ? contrato.puesto.id : contrato.puesto;
          if (puestoId && form.value.contratos[index]) {
            form.value.contratos[index].puesto.id = puestoId.toString();
          }

          const tipoContratoId =
            typeof contrato.tipoContrato === "object"
              ? contrato.tipoContrato.id
              : contrato.tipoContrato;
          if (tipoContratoId && form.value.contratos[index]) {
            form.value.contratos[index].tipoContrato.id = tipoContratoId.toString();
          }

          const jornadaLaboralId =
            typeof contrato.jornadaLaboral === "object"
              ? contrato.jornadaLaboral.id
              : contrato.jornadaLaboral;
          if (jornadaLaboralId && form.value.contratos[index]) {
            form.value.contratos[index].jornadaLaboral.id = jornadaLaboralId.toString();
          }
        });
      }

      const estadoCivilId =
        typeof data.estadoCivil === "object" ? data.estadoCivil.id : data.estadoCivil;
      estadoCivilSeleccionado.value = estadoCivilId?.toString() || "";

      const rolId = typeof data.rol === "object" ? data.rol.id : data.rol;
      rolSeleccionado.value = rolId?.toString() || "";
    } else {
      resetForm();
    }
  },
  { immediate: true },
);

onMounted(async () => {
  await cargarEstadoCivil();
  await cargarJornadasLaborales();
  await cargarTiposContrato();
  await cargarPuestos();
  await cargarRoles();
});
</script>

<style scoped>
.contract-actions-bottom {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e0e0e0;
}

.save-contract-btn {
  min-width: 140px;
}

.contract-toggle-container {
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 12px;
}

.contract-actions {
  display: flex;
  gap: 8px;
}

.q-chip {
  font-weight: 500;
}

.contract-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 16px;
  background: #fafafa;
}

.contract-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 8px 8px 0 0;
}

.contract-content {
  padding: 16px;
  background: white;
}

.no-contracts {
  text-align: center;
  padding: 40px;
  color: #666;
}
</style>
