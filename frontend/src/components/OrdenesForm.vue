<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <div class="form-section">
            <div class="section-header">
              <q-icon name="work" class="section-icon" />
              <h3 class="section-title">Información de la Orden</h3>
            </div>
            <div class="inputs-container">
              <div class="input-row">
                <CustomInput
                  v-model="form.nombre"
                  label="Nombre de la orden *"
                  placeholder="Ingrese el nombre de la orden"
                  obligatorio
                  :rules="[required]"
                  class="input-large"
                />
                <q-select
                  v-model="form.estado"
                  :options="estadosOrden"
                  label="Estado *"
                  :rules="[required]"
                  outlined
                  dense
                  class="input-large"
                />
              </div>
              <div class="input-row">
                <q-input
                  v-model="form.fechaInicioTrabajo"
                  label="Fecha inicio trabajo"
                  type="date"
                  outlined
                  dense
                  class="input-large"
                />
                <q-input
                  v-model="form.fechaFinalizacion"
                  label="Fecha finalización"
                  type="date"
                  outlined
                  dense
                  class="input-large"
                />
              </div>
              <div class="input-row">
                <q-input
                  v-model="form.fechaEntrega"
                  label="Fecha entrega"
                  type="date"
                  outlined
                  dense
                  class="input-large"
                />
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <q-icon name="directions_car" class="section-icon" />
              <h3 class="section-title">Vehículo y Cliente</h3>
            </div>
            <div class="inputs-container">
              <div class="input-row">
                <q-select
                  v-model="vehiculoSeleccionado"
                  :options="vehiculosOptions"
                  option-value="id"
                  option-label="displayText"
                  use-input
                  input-debounce="300"
                  @filter="filtrarVehiculos"
                  :placeholder="!vehiculoSeleccionado ? 'Buscar vehículo...' : ''"
                  outlined
                  dense
                  clearable
                  :rules="[required]"
                  class="input-large"
                  @update:model-value="actualizarClienteDesdeVehiculo"
                >
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">No se encontraron vehículos</q-item-section>
                    </q-item>
                  </template>
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps">
                      <q-item-section>
                        <q-item-label
                          >{{ scope.opt.matricula }} - {{ scope.opt.marca?.nombre }}
                          {{ scope.opt.modelo?.nombre }}</q-item-label
                        >
                        <q-item-label caption>{{ scope.opt.cliente?.nombre }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>

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
                  class="input-large"
                >
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">No se encontraron clientes</q-item-section>
                    </q-item>
                  </template>
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps">
                      <q-item-section>
                        <q-item-label>{{ scope.opt.nombre }}</q-item-label>
                        <q-item-label caption>{{
                          scope.opt.documento || "Sin documento"
                        }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
              </div>
            </div>
          </div>

          <div class="form-section single-column">
            <div class="section-header">
              <q-icon name="engineering" class="section-icon" />
              <h3 class="section-title">Técnico Asignado</h3>
            </div>
            <div class="inputs-container">
              <div class="input-row">
                <q-select
                  v-model="tecnicoSeleccionado"
                  :options="tecnicosOptions"
                  option-value="id"
                  option-label="nombre"
                  use-input
                  input-debounce="300"
                  @filter="filtrarTecnicos"
                  :placeholder="!tecnicoSeleccionado ? 'Buscar técnico...' : ''"
                  outlined
                  dense
                  clearable
                  class="input-large"
                >
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">No se encontraron técnicos</q-item-section>
                    </q-item>
                  </template>
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps">
                      <q-item-section>
                        <q-item-label>{{ scope.opt.nombre }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <q-icon name="inventory_2" class="section-icon" />
              <h3 class="section-title">Materiales Utilizados</h3>
              <q-btn
                flat
                round
                icon="add"
                color="primary"
                size="sm"
                @click="agregarMaterial"
                class="add-button"
              />
            </div>
            <div class="materiales-container">
              <div v-for="(material, index) in form.materiales" :key="index" class="material-row">
                <q-select
                  v-model="material.articulo"
                  :options="articulosOptions"
                  option-value="id"
                  option-label="descripcion"
                  use-input
                  input-debounce="300"
                  @filter="filtrarArticulos"
                  placeholder="Seleccionar artículo..."
                  outlined
                  dense
                  class="articulo-select"
                  @update:model-value="(articulo) => actualizarPrecioArticulo(index, articulo)"
                >
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">No se encontraron artículos</q-item-section>
                    </q-item>
                  </template>
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps">
                      <q-item-section>
                        <q-item-label>{{ scope.opt.descripcion }}</q-item-label>
                        <q-item-label caption>Precio: €{{ scope.opt.precio }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>

                <q-input
                  v-model="material.cantidad"
                  type="number"
                  label="Cantidad"
                  outlined
                  dense
                  min="1"
                  class="cantidad-input"
                  @keypress="validarTeclaStock"
                  @update:model-value="() => calcularSubtotal(index)"
                />

                <q-input
                  v-model="material.precioUnitario"
                  type="number"
                  label="Precio Unit."
                  outlined
                  dense
                  min="0"
                  step="0.01"
                  class="precio-input"
                  @keypress="validarTeclaPrecio"
                  @update:model-value="() => calcularSubtotal(index)"
                />

                <q-input
                  v-model="material.subtotal"
                  label="Subtotal"
                  outlined
                  dense
                  readonly
                  class="subtotal-input"
                />

                <q-btn
                  flat
                  round
                  icon="delete"
                  color="negative"
                  size="sm"
                  @click="eliminarMaterial(index)"
                  class="delete-button"
                />
              </div>

              <div v-if="form.materiales.length === 0" class="no-materiales">
                <q-icon name="inventory_2" size="48px" color="grey-5" />
                <p class="text-grey-6">No hay materiales agregados</p>
              </div>

              <div v-if="form.materiales.length > 0" class="total-container">
                <div class="total-row">
                  <strong>Total: €{{ totalMateriales.toFixed(2) }}</strong>
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <q-icon name="payment" class="section-icon" />
              <h3 class="section-title">Información de Pago</h3>
            </div>
            <div class="inputs-container">
              <div class="input-row">
                <q-checkbox v-model="form.pagado" label="Pagado" class="checkbox-field" />
                <q-select
                  v-model="form.metodoPago"
                  :options="metodosPago"
                  label="Método de pago"
                  outlined
                  dense
                  class="input-large"
                  :disable="!form.pagado"
                />
              </div>
              <div class="input-row" v-if="form.pagado">
                <q-input
                  v-model="form.fechaPago"
                  label="Fecha de pago"
                  type="date"
                  outlined
                  dense
                  class="input-large"
                />
              </div>
            </div>
          </div>

          <div class="form-section single-column">
            <div class="section-header">
              <q-icon name="notes" class="section-icon" />
              <h3 class="section-title">Observaciones</h3>
            </div>
            <div class="inputs-container">
              <q-input
                v-model="form.observaciones"
                label="Observaciones"
                type="textarea"
                outlined
                rows="4"
                placeholder="Ingrese observaciones adicionales..."
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
import { ref, watch, onMounted, computed } from "vue";
import { useQuasar } from "quasar";
import CustomInput from "./CustomInput.vue";
import tallerApi from "../api/tallerApi";
import type { OrdenTrabajoEditData } from "../types/entities/ordenTrabajo";
import type { Vehiculo } from "../types/entities/vehiculo";
import type { Cliente } from "../types/entities/cliente";
import type { Tecnico } from "../types/entities/tecnico";
import type { Articulo } from "../types/entities/articulo";

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: OrdenTrabajoEditData;
}>();

const formRef = ref();
const loading = ref(false);

const form = ref({
  nombre: "",
  estado: "",
  fechaInicioTrabajo: "",
  fechaFinalizacion: "",
  fechaEntrega: "",
  observaciones: "",
  pagado: false,
  metodoPago: "",
  fechaPago: "",
  materiales: [] as Array<{
    articulo: Articulo | null;
    cantidad: string;
    precioUnitario: string;
    subtotal: string;
  }>,
});

const vehiculoSeleccionado = ref<Vehiculo | null>(null);
const clienteSeleccionado = ref<Cliente | null>(null);
const tecnicoSeleccionado = ref<Tecnico | null>(null);

const vehiculosOptions = ref<Vehiculo[]>([]);
const clientesOptions = ref<Cliente[]>([]);
const tecnicosOptions = ref<Tecnico[]>([]);
const articulosOptions = ref<Articulo[]>([]);

const estadosOrden = [
  { label: "Pendiente", value: "PENDIENTE" },
  { label: "En Progreso", value: "EN_PROGRESO" },
  { label: "Pausada", value: "PAUSADA" },
  { label: "Completada", value: "COMPLETADA" },
  { label: "Entregada", value: "ENTREGADA" },
  { label: "Cancelada", value: "CANCELADA" },
];

const metodosPago = ["EFECTIVO", "TARJETA", "TRANSFERENCIA", "CHEQUE"];

const required = (val: string | null) => !!val || "Campo obligatorio";

const validarTeclaPrecio = (event: KeyboardEvent) => {
  const char = event.key;
  const currentValue = (event.target as HTMLInputElement).value;

  if (/[0-9]/.test(char)) return true;
  if (char === "." && !currentValue.includes(".")) return true;
  if ([8, 9, 27, 13, 46].includes(event.keyCode)) return true;

  event.preventDefault();
  return false;
};

const validarTeclaStock = (event: KeyboardEvent) => {
  const char = event.key;

  if (/[0-9]/.test(char)) return true;
  if (["Backspace", "Tab", "Escape", "Enter", "Delete"].includes(event.key)) return true;

  event.preventDefault();
  return false;
};

const totalMateriales = computed(() => {
  return form.value.materiales.reduce((total, material) => {
    return total + parseFloat(material.subtotal || "0");
  }, 0);
});

const filtrarVehiculos = async (val: string, update: (fn: () => void) => void) => {
  if (val.length < 2) {
    update(() => {
      vehiculosOptions.value = [];
    });
    return;
  }

  try {
    const { data } = await tallerApi.get<Vehiculo[]>("/vehiculos", {
      params: { search: val },
    });

    update(() => {
      vehiculosOptions.value = Array.isArray(data)
        ? data.map((v: Vehiculo) => ({
            ...v,
            displayText: `${v.matricula} - ${v.marca?.nombre || ""} ${v.modelo?.nombre || ""}`,
          }))
        : [];
    });
  } catch (error) {
    console.error("Error buscando vehículos:", error);
    update(() => {
      vehiculosOptions.value = [];
    });
  }
};

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

const filtrarTecnicos = async (val: string, update: (fn: () => void) => void) => {
  if (val.length < 2) {
    update(() => {
      tecnicosOptions.value = [];
    });
    return;
  }

  try {
    const { data } = await tallerApi.get<Tecnico[]>("/tecnicos", {
      params: { search: val },
    });

    update(() => {
      tecnicosOptions.value = Array.isArray(data) ? data : [];
    });
  } catch (error) {
    console.error("Error buscando técnicos:", error);
    update(() => {
      tecnicosOptions.value = [];
    });
  }
};

const filtrarArticulos = async (val: string, update: (fn: () => void) => void) => {
  if (val.length < 2) {
    update(() => {
      articulosOptions.value = [];
    });
    return;
  }

  try {
    const { data } = await tallerApi.get<Articulo[]>("/articulos", {
      params: { search: val },
    });

    update(() => {
      articulosOptions.value = Array.isArray(data) ? data : [];
    });
  } catch (error) {
    console.error("Error buscando artículos:", error);
    update(() => {
      articulosOptions.value = [];
    });
  }
};

const agregarMaterial = () => {
  form.value.materiales.push({
    articulo: null,
    cantidad: "1",
    precioUnitario: "0",
    subtotal: "0",
  });
};

const eliminarMaterial = (index: number) => {
  form.value.materiales.splice(index, 1);
};

const actualizarPrecioArticulo = (index: number, articulo: Articulo | null) => {
  if (articulo && form.value.materiales[index]) {
    form.value.materiales[index].precioUnitario = articulo.precio?.toString() || "0";
    calcularSubtotal(index);
  }
};

const calcularSubtotal = (index: number) => {
  const material = form.value.materiales[index];
  if (material) {
    const cantidad = parseFloat(material.cantidad || "0");
    const precioUnitario = parseFloat(material.precioUnitario || "0");
    material.subtotal = (cantidad * precioUnitario).toFixed(2);
  }
};

const actualizarClienteDesdeVehiculo = async (vehiculo: Vehiculo | null) => {
  if (vehiculo?.cliente?.id) {
    try {
      const { data: clienteData } = await tallerApi.get<Cliente>(
        `/clientes/${vehiculo.cliente.id}`,
      );
      clienteSeleccionado.value = clienteData;
    } catch (error) {
      console.error("Error cargando cliente desde vehículo:", error);
      clienteSeleccionado.value = null;
    }
  }
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  loading.value = true;

  try {
    const payload = {
      nombre: form.value.nombre,
      estado: form.value.estado,
      fechaInicioTrabajo: form.value.fechaInicioTrabajo || null,
      fechaFinalizacion: form.value.fechaFinalizacion || null,
      fechaEntrega: form.value.fechaEntrega || null,
      observaciones: form.value.observaciones,
      pagado: form.value.pagado,
      metodoPago: form.value.pagado ? form.value.metodoPago : null,
      fechaPago: form.value.pagado ? form.value.fechaPago : null,
      tecnicoPrincipal: tecnicoSeleccionado.value
        ? {
            id: tecnicoSeleccionado.value.id,
          }
        : null,
      vehiculo: {
        id: vehiculoSeleccionado.value?.id,
      },
      cliente: {
        id: clienteSeleccionado.value?.id,
      },
      materiales: form.value.materiales.map((material) => ({
        articulo: { id: material.articulo?.id },
        cantidad: parseInt(material.cantidad),
        precioUnitario: parseFloat(material.precioUnitario),
        subtotal: parseFloat(material.subtotal),
      })),
    };

    if (props.editData?.id) {
      await tallerApi.put(`/ordenes-trabajo/${props.editData.id}`, payload);
      $q.notify({ type: "positive", message: "Orden de trabajo actualizada" });
      emit("updated");
    } else {
      await tallerApi.post("/ordenes-trabajo", payload);
      $q.notify({ type: "positive", message: "Orden de trabajo creada" });
      emit("created");
      resetForm();
    }
  } catch (error) {
    console.error("Error al guardar orden:", error);
    $q.notify({ type: "negative", message: "Error al guardar" });
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = {
    nombre: "",
    estado: "",
    fechaInicioTrabajo: "",
    fechaFinalizacion: "",
    fechaEntrega: "",
    observaciones: "",
    pagado: false,
    metodoPago: "",
    fechaPago: "",
    materiales: [],
  };
  vehiculoSeleccionado.value = null;
  clienteSeleccionado.value = null;
  tecnicoSeleccionado.value = null;
  formRef.value?.resetValidation();
};

watch(
  () => props.editData,
  async (data) => {
    if (data) {
      form.value = {
        nombre: data.nombre || "",
        estado: data.estado || "",
        fechaInicioTrabajo: data.fechaInicioTrabajo || "",
        fechaFinalizacion: data.fechaFinalizacion || "",
        fechaEntrega: data.fechaEntrega || "",
        observaciones: data.observaciones || "",
        pagado: data.pagado || false,
        metodoPago: data.metodoPago || "",
        fechaPago: data.fechaPago || "",
        materiales:
          data.materiales?.map((m) => ({
            articulo: m.articulo,
            cantidad: m.cantidad?.toString() || "1",
            precioUnitario: m.precioUnitario?.toString() || "0",
            subtotal: m.subtotal?.toString() || "0",
          })) || [],
      };

      if (data.vehiculo?.id) {
        try {
          const { data: vehiculoData } = await tallerApi.get<Vehiculo>(
            `/vehiculos/${data.vehiculo.id}`,
          );
          vehiculoSeleccionado.value = vehiculoData;
        } catch (error) {
          console.error("Error cargando vehículo:", error);
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

      if (data.tecnicoPrincipal?.id) {
        try {
          const { data: tecnicoData } = await tallerApi.get<Tecnico>(
            `/tecnicos/${data.tecnicoPrincipal.id}`,
          );
          tecnicoSeleccionado.value = tecnicoData;
        } catch (error) {
          console.error("Error cargando técnico:", error);
        }
      }
    } else {
      resetForm();
    }
  },
  { immediate: true },
);

onMounted(async () => {
  // Inicialización si es necesaria
});
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  position: relative;
}

.add-button {
  margin-left: auto;
}

.materiales-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.material-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr auto;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fafafa;
}

.no-materiales {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  text-align: center;
}

.total-container {
  border-top: 1px solid #e0e0e0;
  padding-top: 1rem;
}

.total-row {
  text-align: right;
  font-size: 1.1rem;
}

.checkbox-field {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .material-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
}
</style>
