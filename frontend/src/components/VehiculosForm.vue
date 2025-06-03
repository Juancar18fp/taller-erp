export interface EmpleadoPayload { nombre: string; fechaNacimiento: string | null;
documentoIdentidad: string; nacionalidad: string | null; estadoCivil: { id: string; nombre?: string;
}; direccion: string; telefono: string; email: string; numeroSeguridadSocial: string; rol: { id:
string; nombre?: string; }; contrato: { puesto: { id: string; nombre?: string; }; fechaContratacion:
string | null; tipoContrato: { id: string; nombre?: string; }; jornadaLaboral: { id: string;
nombre?: string; }; salario: string | null; numeroCuenta: string | null; }; } export interface
Empleado extends EmpleadoPayload { id: number; } export interface EmpleadoEditData extends
EmpleadoPayload { id: number;
<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <div class="form-section">
            <div class="section-header">
              <q-icon name="account_circle" class="section-icon" />
              <h3 class="section-title">Información del vehículo</h3>
            </div>

            <div class="inputs-container">
              <div class="input-row">
                <CustomInput
                  v-model="vehiculoForm.matricula"
                  label="Matrícula *"
                  placeholder="Ingrese la matrícula"
                  obligatorio
                  :rules="[required]"
                />

                <CustomInput
                  v-model="vehiculoForm.matriculacion"
                  label="Año matriculación"
                  placeholder="Año matriculación"
                />
              </div>

              <div class="input-row">
                <q-select
                  class="input-large"
                  v-model="marcaSeleccionada"
                  :options="marcas"
                  option-label="nombre"
                  option-value="id"
                  label="Marca *"
                  :rules="[required]"
                  map-options
                  emit-value
                  outlined
                  dense
                  @update:model-value="cambiarMarca"
                />

                <q-select
                  class="input-large"
                  v-model="modeloSeleccionado"
                  :options="modelos"
                  option-label="nombre"
                  option-value="id"
                  label="Modelo *"
                  :rules="[required]"
                  :disable="!marcaSeleccionada"
                  map-options
                  emit-value
                  outlined
                  dense
                />
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <q-icon name="contact_mail" class="section-icon" />
              <h3 class="section-title">Información del cliente</h3>
            </div>

            <div class="inputs-container">
              <q-select
                v-model="clienteSeleccionado"
                :options="clientesOptions"
                option-value="id"
                option-label="nombre"
                use-input
                input-debounce="300"
                @filter="filtrarClientes"
                :placeholder="!clienteSeleccionado ? 'Buscar cliente...' : ''"
                outlined
                dense
                clearable
                :rules="[required]"
                class="custom-select"
              >
                <template v-slot:no-option>
                  <q-item>
                    <q-item-section class="text-grey"> No se encontraron clientes </q-item-section>
                  </q-item>
                </template>

                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps">
                    <q-item-section>
                      <q-item-label>{{ scope.opt.nombre }}</q-item-label>
                      <q-item-label caption>{{ scope.opt.email || "Sin email" }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
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
import { ref, watch, onMounted } from "vue";
import { useQuasar } from "quasar";
import CustomInput from "./CustomInput.vue";
import tallerApi from "src/api/tallerApi";
import type { VehiculoEditData, VehiculoPayload } from "../types/entities/vehiculo";
import type { Cliente } from "src/types/entities/cliente";
import { useMarcaModelo } from "src/composables/useMarcaModelo";

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const {
  marcas,
  modelos,
  marcaSeleccionada,
  modeloSeleccionado,
  cargarMarcas,
  cargarModelosPorMarca,
  cambiarMarca,
} = useMarcaModelo();

const props = defineProps<{
  editData?: VehiculoEditData;
}>();

const formRef = ref();
const loading = ref(false);

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

const clienteSeleccionado = ref<Cliente | null>(null);

const clientesOptions = ref<Cliente[]>([]);

const required = (val: string | null) => !!val || "Campo obligatorio";

const filtrarClientes = async (val: string, update: (fn: () => void) => void) => {
  if (val.length < 2) {
    update(() => {
      clientesOptions.value = [];
    });
    return;
  }

  try {
    const { data } = await tallerApi.get<Cliente[]>("/clientes", {
      params: { search: val },
    });

    update(() => {
      clientesOptions.value = Array.isArray(data) ? data : [];
    });
  } catch (error) {
    console.error("Error buscando clientes:", error);
    update(() => {
      clientesOptions.value = [];
    });
  }
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  loading.value = true;

  try {
    const payload = {
      id: props.editData?.id || undefined,
      matricula: vehiculoForm.value.matricula,
      matriculacion: vehiculoForm.value.matriculacion,
      marca: { id: marcaSeleccionada.value },
      modelo: { id: modeloSeleccionado.value },
      cliente: {
        id: clienteSeleccionado.value?.id?.toString() || "",
        nombre: clienteSeleccionado.value?.nombre || "",
      },
    };

    if (props.editData?.id) {
      await tallerApi.put(`/vehiculos/${props.editData.id}`, payload);
      $q.notify({ type: "positive", message: "Vehículo actualizado" });
      emit("updated");
    } else {
      await tallerApi.post("/vehiculos", payload);
      $q.notify({ type: "positive", message: "Vehículo creado" });
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
  vehiculoForm.value = {
    matricula: "",
    marca: { id: "" },
    modelo: { id: "" },
    matriculacion: "",
    cliente: {
      id: "",
      nombre: "",
    },
  };
  formRef.value?.resetValidation();
};

watch(
  () => props.editData,
  async (data) => {
    if (data) {
      vehiculoForm.value = {
        matricula: data.matricula || "",
        marca: data.marca || null,
        modelo: data.modelo || null,
        matriculacion: data.matriculacion?.toString() || "",
        cliente: {
          id: data.cliente.id?.toString() || "",
          nombre: data.cliente.nombre || "",
        },
      };

      const marcaId = typeof data.marca === "object" ? data.marca.id : data.marca;

      if (marcaId) {
        await cargarModelosPorMarca(marcaId.toString());
        marcaSeleccionada.value = marcaId.toString();
        const modeloId = typeof data.modelo === "object" ? data.modelo.id : data.modelo;
        if (modeloId) {
          modeloSeleccionado.value = modeloId.toString();
        }
      }

      if (data.cliente?.id) {
        try {
          const { data: clienteData } = await tallerApi.get<Cliente>(
            `/clientes/${data.cliente.id}`,
          );
          clienteSeleccionado.value = clienteData;
        } catch (error) {
          console.error("Error cargando cliente:", error);
        }
      }
    } else {
      resetForm();
    }
  },
  { immediate: true },
);

onMounted(async () => {
  await cargarMarcas();
});
</script>
