<template>
  <div class="q-pa-md column full-height">
    <q-scroll-area class="col">
      <q-form ref="formRef" @submit.prevent="onSubmit" class="q-gutter-y-md">
        <div class="row q-col-gutter-lg">
          <div class="col-6">
            <CustomInput
              v-model="vehiculoForm.matricula"
              label="Matrícula *"
              obligatorio
              :rules="[(val: string) => !!val || 'Campo obligatorio']"
            />
            <CustomInput
              v-model="vehiculoForm.cliente.id"
              label="ID Cliente *"
              hint="Enter para comprobar"
              :rules="[(val: string) => !!val || 'Campo obligatorio']"
              @keyup.enter.prevent="buscarCliente"
            />

            <CustomInput
              v-if="vehiculoForm.cliente.nombre"
              v-model="vehiculoForm.cliente.nombre"
              label="Nombre del Cliente"
              readonly
            />
          </div>

          <div class="col-6">
            <q-select
              v-model.number="vehiculoForm.marca.id"
              :options="marcas"
              option-label="nombre"
              option-value="id"
              label="Marca *"
              :rules="[(val: number) => !!val || 'Campo obligatorio']"
              map-options
              emit-value
              outlined
              dense
              clearable
              @update:model-value="cambiarMarca"
            />

            <q-select
              v-model.number="vehiculoForm.modelo.id"
              :options="modelos"
              option-label="nombre"
              option-value="id"
              label="Modelo *"
              :rules="[(val: number) => !!val || 'Campo obligatorio']"
              :disable="!vehiculoForm.marca.id"
              map-options
              emit-value
              outlined
              dense
              clearable
            />

            <CustomInput
              v-model="vehiculoForm.matriculacion"
              label="Año de matriculación *"
              :rules="[(val: string) => !!val || 'Campo obligatorio']"
            />
          </div>
        </div>
      </q-form>
    </q-scroll-area>

    <q-card-actions align="right" class="sticky-actions q-px-none">
      <q-btn flat label="Cancelar" color="primary" @click="$emit('cancel')" />
      <q-btn label="Añadir" color="primary" @click="submitForm" class="q-ml-sm" />
    </q-card-actions>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useQuasar } from "quasar";
import tallerApi from "../api/tallerApi";
import type { QForm } from "quasar";
import CustomInput from "./CustomInput.vue";

interface VehiculoPayload {
  matricula: string;
  marca: {
    id: string;
    nombre?: string;
  };
  modelo: {
    id: string;
    nombre?: string;
    marca_id?: string;
  };
  matriculacion: string;
  cliente: {
    id: string;
    nombre: string;
  };
}

interface Marca {
  id: string;
  nombre: string;
}

interface Modelo {
  id: string;
  nombre: string;
  marca_id: string;
}

const $q = useQuasar();
const emit = defineEmits(["created", "cancel"]);
const formRef = ref<QForm>();

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

const marcas = ref<Marca[]>([]);
const modelos = ref<Modelo[]>([]);

const cargarMarcas = async () => {
  try {
    const { data } = await tallerApi.get<Marca[]>("/marcas");
    marcas.value = data.map((m) => ({
      id: m.id,
      nombre: m.nombre,
    }));
  } catch {
    $q.notify({
      type: "negative",
      message: "Error cargando marcas",
    });
  }
};

const cambiarMarca = (nuevaMarcaId: string) => {
  vehiculoForm.value.modelo.id = "";
  if (nuevaMarcaId) {
    cargarModelosPorMarca(nuevaMarcaId).catch((error) => {
      console.error("Error al cargar modelos:", error);
      $q.notify({
        type: "negative",
        message: "Error al cargar modelos de la marca",
      });
    });
  } else {
    modelos.value = [];
  }
};

const cargarModelosPorMarca = async (marcaId: string) => {
  try {
    const { data } = await tallerApi.get<Modelo[]>(`/modelos/${marcaId}`);
    modelos.value = data.map((m) => ({
      id: m.id,
      nombre: m.nombre,
      marca_id: m.marca_id,
    }));
  } catch (error) {
    console.error("Error cargando modelos:", error);
    $q.notify({
      type: "negative",
      message: "Error cargando modelos de la marca",
    });
  }
};

const buscarCliente = async () => {
  if (!vehiculoForm.value.cliente.id) {
    $q.notify({ type: "warning", message: "Ingrese un ID de cliente" });
    return;
  }

  try {
    const response = await tallerApi.get(`/clientes/${vehiculoForm.value.cliente.id}`);
    vehiculoForm.value.cliente.nombre = response.data.nombre;
  } catch {
    vehiculoForm.value.cliente.nombre = "Cliente no encontrado";
  }
};

const onSubmit = async () => {
  try {
    await tallerApi.post("/vehiculos", vehiculoForm.value);
    $q.notify({ type: "positive", message: "Vehículo creado exitosamente" });
    emit("created");
    resetForm();
  } catch (error) {
    console.error("Error:", error);
    $q.notify({
      type: "negative",
      message: "Error al crear vehículo",
    });
  }
};

const submitForm = async () => {
  try {
    const isValid = await formRef.value?.validate();

    if (isValid) {
      await onSubmit();
    } else {
      $q.notify({
        type: "warning",
        message: "Verifica los campos obligatorios",
      });
    }
  } catch (error) {
    console.error("Error en el envío:", error);
    $q.notify({
      type: "negative",
      message: "Error al procesar la solicitud",
    });
  }
};

onMounted(() => {
  void cargarMarcas();
});

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
</script>
