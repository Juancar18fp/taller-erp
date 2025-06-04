<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <!-- Información básica de la orden -->
          <div class="form-section">
            <div class="section-header">
              <q-icon name="work" class="section-icon" />
              <h3 class="section-title">Información de la orden</h3>
            </div>

            <div class="inputs-container">
              <div class="input-row">
                <CustomInput
                  v-model="ordenForm.codigoOrden"
                  label="Código de orden *"
                  placeholder="Ingrese el código"
                  obligatorio
                  :rules="[required]"
                  class="input-large"
                />

                <!-- Fecha de orden automática -->
                <q-input
                  v-model="fechaOrdenDisplay"
                  label="Fecha de orden"
                  readonly
                  outlined
                  dense
                  class="input-large"
                />
              </div>

              <!-- Vehículo siempre requerido -->
              <q-select
                v-model="vehiculoSeleccionado"
                :options="vehiculosOptions"
                option-value="id"
                option-label="matricula"
                use-input
                map-options
                emit-value
                input-debounce="300"
                :placeholder="!vehiculoSeleccionado ? 'Buscar vehiculo...' : ''"
                outlined
                dense
                clearable
                :rules="[required]"
                class="input-large"
                @filter="filtrarVehiculos"
              >
                <template v-slot:no-option>
                  <q-item>
                    <q-item-section class="text-grey"> No se encontraron vehículos </q-item-section>
                  </q-item>
                </template>

                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps">
                    <q-item-section>
                      <q-item-label>{{ scope.opt.matricula }}</q-item-label>
                      <q-item-label caption>{{ scope.opt.cliente.nombre }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
            </div>
          </div>

          <!-- Estado y gestión de la orden -->
          <div class="form-section">
            <div class="section-header">
              <q-icon name="timeline" class="section-icon" />
              <h3 class="section-title">Estado de la orden</h3>
            </div>

            <div class="estado-container">
              <!-- Display del estado actual -->
              <div class="estado-actual">
                <q-chip
                  :color="getEstadoColor(estadoActual)"
                  text-color="white"
                  icon="info"
                  class="estado-chip"
                >
                  {{ getEstadoNombre(estadoActual) }}
                </q-chip>
              </div>

              <!-- Información de fechas -->
              <div class="fechas-info" v-if="!esNuevaOrden">
                <div class="fecha-item" v-if="ordenForm.fechaInicio">
                  <q-icon name="play_arrow" color="green" />
                  <span>Iniciada: {{ formatearFecha(ordenForm.fechaInicio) }}</span>
                </div>
                <div class="fecha-item" v-if="ordenForm.fechaFinalizacion">
                  <q-icon name="check_circle" color="blue" />
                  <span>Finalizada: {{ formatearFecha(ordenForm.fechaFinalizacion) }}</span>
                </div>
                <div class="fecha-item" v-if="ordenForm.fechaPago">
                  <q-icon name="payment" color="purple" />
                  <span>Pagada: {{ formatearFecha(ordenForm.fechaPago) }}</span>
                </div>
              </div>

              <!-- Empleado asignado (solo si no es nueva orden) -->
              <div v-if="!esNuevaOrden" class="empleado-container">
                <q-select
                  v-model="empleadoSeleccionado"
                  :options="empleadosOptions"
                  option-value="id"
                  option-label="nombre"
                  use-input
                  map-options
                  emit-value
                  input-debounce="300"
                  :placeholder="!empleadoSeleccionado ? 'Buscar empleado...' : ''"
                  outlined
                  dense
                  clearable
                  :label="empleadoSeleccionado ? 'Técnico asignado' : 'Asignar técnico'"
                  class="input-large"
                  @filter="filtrarEmpleados"
                />
              </div>

              <!-- Botones de acción según el estado -->
              <div class="acciones-estado" v-if="!esNuevaOrden && estadoActual !== 'CANCELADA'">
                <!-- Asignar técnico (Solo si está pendiente y no tiene técnico) -->
                <q-btn
                  v-if="
                    estadoActual === 'PENDIENTE' && empleadoSeleccionado && !tieneEmpleadoAsignado
                  "
                  label="Asignar Técnico e Iniciar"
                  icon="person_add"
                  color="green"
                  @click="asignarTecnico"
                  class="accion-btn"
                />

                <!-- Iniciar trabajo (Solo si está pendiente y tiene técnico) -->
                <q-btn
                  v-if="estadoActual === 'PENDIENTE' && tieneEmpleadoAsignado"
                  label="Iniciar Trabajo"
                  icon="play_arrow"
                  color="green"
                  @click="iniciarTrabajo"
                  class="accion-btn"
                />

                <!-- Finalizar trabajo (Solo si está iniciada) -->
                <q-btn
                  v-if="estadoActual === 'INICIADA'"
                  label="Finalizar Trabajo"
                  icon="check_circle"
                  color="blue"
                  @click="finalizarTrabajo"
                  class="accion-btn"
                />

                <!-- Marcar como pagada (Solo si no está pagada) -->
                <q-btn
                  v-if="
                    !ordenForm.pagada &&
                    (estadoActual === 'FINALIZADA' || estadoActual === 'PENDIENTE_PAGO')
                  "
                  label="Marcar como Pagada"
                  icon="payment"
                  color="purple"
                  @click="marcarComoPagada"
                  class="accion-btn"
                />
              </div>

              <!-- Botón de cancelar (disponible en cualquier estado excepto completada y cancelada) -->
              <div class="cancelar-container" v-if="!esNuevaOrden && puedeSerCancelada">
                <q-separator class="q-my-md" />
                <q-btn
                  label="Cancelar Orden"
                  icon="cancel"
                  color="negative"
                  outline
                  @click="confirmarCancelacion"
                  class="cancelar-btn"
                />
              </div>
            </div>
          </div>

          <!-- Observaciones -->
          <div class="form-section observaciones-section">
            <div class="section-header">
              <q-icon name="notes" class="section-icon" />
              <h3 class="section-title">Observaciones</h3>
            </div>

            <q-input
              v-model="ordenForm.observaciones"
              label="Observaciones"
              type="textarea"
              outlined
              rows="4"
              class="textarea-full"
            />
          </div>

          <!-- Artículos utilizados -->
          <div class="articulos-section">
            <div class="section-header">
              <q-icon name="inventory" class="section-icon" />
              <h3 class="section-title">Artículos utilizados</h3>
              <q-btn
                icon="add"
                color="primary"
                dense
                round
                @click="agregarArticulo"
                class="add-btn"
                :disable="
                  estadoActual === 'FINALIZADA' ||
                  estadoActual === 'CANCELADA' ||
                  estadoActual === 'COMPLETADA'
                "
              />
            </div>

            <div class="articulos-container">
              <div
                v-for="(articuloUsado, index) in ordenForm.articulosUsados"
                :key="index"
                class="articulo-item"
              >
                <q-select
                  v-model="articuloUsado.articulo"
                  :options="articulosOptions"
                  option-value="id"
                  option-label="descripcion"
                  use-input
                  input-debounce="300"
                  map-options
                  @filter="filtrarArticulos"
                  :placeholder="!articuloUsado.articulo ? 'Buscar artículo...' : ''"
                  outlined
                  dense
                  clearable
                  :disable="
                    estadoActual === 'FINALIZADA' ||
                    estadoActual === 'CANCELADA' ||
                    estadoActual === 'COMPLETADA'
                  "
                  @update:model-value="(val) => actualizarPrecioArticulo(index, val)"
                />

                <q-input
                  v-model.number="articuloUsado.cantidad"
                  label="Cantidad"
                  type="number"
                  outlined
                  dense
                  min="1"
                  :disable="
                    estadoActual === 'FINALIZADA' ||
                    estadoActual === 'CANCELADA' ||
                    estadoActual === 'COMPLETADA'
                  "
                  class="cantidad-input"
                  @update:model-value="calcularTotal"
                />

                <div class="precio-info">
                  <span class="precio-unitario">
                    €{{ (articuloUsado.articulo?.precio || 0).toFixed(2) }}
                  </span>
                  <span class="precio-total">
                    €{{
                      (
                        (articuloUsado.articulo?.precio || 0) * (articuloUsado.cantidad || 0)
                      ).toFixed(2)
                    }}
                  </span>
                </div>

                <q-btn
                  icon="delete"
                  color="negative"
                  dense
                  round
                  :disable="
                    estadoActual === 'FINALIZADA' ||
                    estadoActual === 'CANCELADA' ||
                    estadoActual === 'COMPLETADA'
                  "
                  @click="removerArticulo(index)"
                  class="delete-btn"
                />
              </div>

              <div class="total-container">
                <div class="total-row">
                  <span class="total-label">Total de artículos:</span>
                  <span class="total-value">€{{ totalArticulos.toFixed(2) }}</span>
                </div>
              </div>
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
        :label="esNuevaOrden ? 'Crear Orden' : 'Actualizar'"
        :icon="esNuevaOrden ? 'add' : 'update'"
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
import {computed, onMounted, ref, watch} from "vue";
import type {QNotifyCreateOptions} from "quasar";
import {useQuasar} from "quasar";
import CustomInput from "./CustomInput.vue";
import tallerApi from "src/api/tallerApi";
import type {
  ArticuloUsado,
  EstadoOrden,
  OrdenTrabajoEditData,
  OrdenTrabajoPayload,
} from "src/types/entities/orden_trabajo";

interface Articulo {
  id: string;
  descripcion: string;
  precio: number;
  stock: number;
}

interface Empleado {
  id: string;
  nombre: string;
}

interface Vehiculo {
  id: string;
  matricula: string;
  cliente: {
    nombre: string;
  };
}

const $q = useQuasar();
const emit = defineEmits(["created", "updated", "cancel"]);

const props = defineProps<{
  editData?: OrdenTrabajoEditData;
}>();

const formRef = ref();
const loading = ref(false);

const ordenForm = ref<OrdenTrabajoPayload>({
  codigoOrden: "",
  empleadoAsignado: {
    id: "",
  },
  vehiculo: {
    id: "",
  },
  estadoOrden: {
    id: "",
  },
  fechaOrden: "",
  fechaInicio: "",
  fechaFinalizacion: "",
  fechaPago: "",
  pagada: false,
  articulosUsados: [],
  observaciones: "",
  total: 0,
});

const required = (val: string | null) => !!val || "Campo obligatorio";

// Estados
const empleadoSeleccionado = ref<Empleado | null>(null);
const vehiculoSeleccionado = ref<Vehiculo | null>(null);
const estadoActual = ref<string>("PENDIENTE");
const empleadosOptions = ref<Empleado[]>([]);
const vehiculosOptions = ref<Vehiculo[]>([]);
const articulosOptions = ref<Articulo[]>([]);
const estadosOrden = ref<EstadoOrden[]>([]);

// Computeds
const esNuevaOrden = computed(() => !props.editData?.id);
const tieneEmpleadoAsignado = computed(() => !!ordenForm.value.empleadoAsignado?.id);
const puedeSerCancelada = computed(() => {
  return !["COMPLETADA", "CANCELADA"].includes(estadoActual.value);
});

const fechaOrdenDisplay = computed(() => {
  return new Date().toISOString().split("T")[0];
});

const totalArticulos = computed(() => {
  return ordenForm.value.articulosUsados.reduce((total, item) => {
    const precio = item.articulo?.precio || 0;
    const cantidad = item.cantidad || 0;
    return total + precio * cantidad;
  }, 0);
});

// Funciones de estado
const getEstadoColor = (estado: string) => {
  const colores = {
    PENDIENTE: "orange",
    INICIADA: "blue",
    FINALIZADA: "green",
    PENDIENTE_PAGO: "red",
    COMPLETADA: "purple",
    CANCELADA: "grey",
  };
  return colores[estado as keyof typeof colores] || "grey";
};

const getEstadoNombre = (estado: string) => {
  const nombres = {
    PENDIENTE: "Pendiente",
    INICIADA: "En Progreso",
    FINALIZADA: "Finalizada",
    PENDIENTE_PAGO: "Pendiente de Pago",
    COMPLETADA: "Completada",
    CANCELADA: "Cancelada",
  };
  return nombres[estado as keyof typeof nombres] || estado;
};

const formatearFecha = (fecha: string) => {
  return new Date(fecha).toLocaleDateString("es-ES");
};

// Funciones de gestión de estado
const asignarTecnico = async () => {
  if (!empleadoSeleccionado.value) return;

  await cambiarEstado("INICIADA", {
    empleadoAsignado: { id: empleadoSeleccionado.value.id || empleadoSeleccionado.value },
    fechaInicio: new Date().toISOString().split("T")[0],
  });
};

const iniciarTrabajo = async () => {
  await cambiarEstado("INICIADA", {
    fechaInicio: new Date().toISOString().split("T")[0],
  });
};

const finalizarTrabajo = async () => {
  const nuevoEstado = ordenForm.value.pagada ? "COMPLETADA" : "PENDIENTE_PAGO";
  await cambiarEstado(nuevoEstado, {
    fechaFinalizacion: new Date().toISOString().split("T")[0],
  });
};

const marcarComoPagada = async () => {
  const datosActualizacion = {
    pagada: true,
    fechaPago: new Date().toISOString().split("T")[0],
  };

  // Si está finalizada o pendiente de pago, pasa a completada
  const nuevoEstado =
    estadoActual.value === "FINALIZADA" || estadoActual.value === "PENDIENTE_PAGO"
      ? "COMPLETADA"
      : estadoActual.value;

  await cambiarEstado(nuevoEstado, datosActualizacion);
};

const confirmarCancelacion = () => {
  $q.dialog({
    title: "Cancelar Orden",
    message: "¿Está seguro de que desea cancelar esta orden? Esta acción no se puede deshacer.",
    cancel: true,
    persistent: true,
    color: "negative",
  }).onOk(() => {
    void cancelarOrden();
  });
};

const cancelarOrden = async () => {
  await cambiarEstado("CANCELADA", {
    fechaCancelacion: new Date().toISOString().split("T")[0],
  });
};

const cambiarEstado = async (
  nuevoEstado: string,
  datosAdicionales: Record<string, unknown> = {},
) => {
  loading.value = true;
  try {
    const estadoObj = estadosOrden.value.find((e) => e.nombre === nuevoEstado);
    if (!estadoObj) {
      throw new Error(`Estado ${nuevoEstado} no encontrado`);
    }

    // IMPORTANTE: Excluir articulosUsados del payload
    const { articulosUsados: _, ...ordenSinArticulos } = ordenForm.value;
    void _; // Silenciar ESLint

    const payload = {
      id: props.editData!.id,
      ...ordenSinArticulos, // Sin articulosUsados
      estadoOrden: { id: estadoObj.id },
      empleadoAsignado: empleadoSeleccionado.value
        ? {
            id: empleadoSeleccionado.value.id || empleadoSeleccionado.value,
          }
        : null,
      vehiculo: {
        id: vehiculoSeleccionado.value?.id || vehiculoSeleccionado.value,
      },
      ...datosAdicionales,
    };

    await tallerApi.put(`/ordenes/${props.editData!.id}`, payload);

    // Recargar los datos completos del backend
    const { data: ordenActualizada } = await tallerApi.get(`/ordenes/${props.editData!.id}`);

    // Actualizar completamente los datos locales
    if (ordenActualizada.estadoOrden) {
      const estadoEncontrado = estadosOrden.value.find(
        (e) => e.id == ordenActualizada.estadoOrden.id,
      );
      estadoActual.value = estadoEncontrado?.nombre || "PENDIENTE";
    }

    // Actualizar el form con los datos frescos del backend
    Object.assign(ordenForm.value, ordenActualizada);

    // Actualizar empleado y vehículo seleccionados si es necesario
    if (ordenActualizada.empleadoAsignado?.id && !empleadoSeleccionado.value) {
      try {
        const { data: empleado } = await tallerApi.get(
          `/empleados/${ordenActualizada.empleadoAsignado.id}`,
        );
        empleadoSeleccionado.value = empleado;
      } catch (error) {
        console.error("Error cargando empleado:", error);
      }
    }

    $q.notify({
      type: "positive",
      message: `Estado cambiado a ${getEstadoNombre(estadoActual.value)}`,
    });

    emit("updated");
  } catch (error) {
    console.error("Error al cambiar estado:", error);
    $q.notify({ type: "negative", message: "Error al cambiar el estado" });
  } finally {
    loading.value = false;
  }
};

// Funciones de búsqueda
const filtrarEmpleados = async (val: string, update: (fn: () => void) => void) => {
  if (val.length < 2) {
    update(() => {
      empleadosOptions.value = [];
    });
    return;
  }

  try {
    const { data } = await tallerApi.get("/empleados", {
      params: { search: val },
    });

    update(() => {
      empleadosOptions.value = Array.isArray(data) ? data : [];
    });
  } catch (error) {
    console.error("Error buscando empleados:", error);
    update(() => {
      empleadosOptions.value = [];
    });
  }
};

const filtrarVehiculos = async (val: string, update: (fn: () => void) => void) => {
  if (val.length < 2) {
    update(() => {
      vehiculosOptions.value = [];
    });
    return;
  }

  try {
    const { data } = await tallerApi.get("/vehiculos", {
      params: { search: val },
    });

    update(() => {
      vehiculosOptions.value = Array.isArray(data) ? data : [];
    });
  } catch (error) {
    console.error("Error buscando vehículos:", error);
    update(() => {
      vehiculosOptions.value = [];
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
    const { data } = await tallerApi.get("/articulos", {
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

// Gestión de artículos
const agregarArticulo = () => {
  const nuevoArticulo: ArticuloUsado = {
    id: "",
    articulo: {
      id: "",
      descripcion: "",
      precio: 0,
    },
    cantidad: 1,
  };
  ordenForm.value.articulosUsados.push(nuevoArticulo);
};

const removerArticulo = (index: number) => {
  ordenForm.value.articulosUsados.splice(index, 1);
  calcularTotal();
};

const actualizarPrecioArticulo = (index: number, articulo: Articulo | null) => {
  if (ordenForm.value.articulosUsados[index]) {
    ordenForm.value.articulosUsados[index].articulo = articulo ?? {
      id: "",
      descripcion: "",
      precio: 0,
    };
    calcularTotal();
  }
};

const calcularTotal = () => {
  ordenForm.value.total = totalArticulos.value;
};

// Cargar datos iniciales
const cargarEstadosOrden = async () => {
  try {
    const { data } = await tallerApi.get<EstadoOrden[]>("/estados-ordenes");
    estadosOrden.value = data.map((e) => ({
      id: e.id.toString(),
      nombre: e.nombre,
    }));
  } catch (error) {
    console.error("Error cargando estados:", error);
    $q.notify({
      type: "negative",
      message: "Error cargando estados de orden",
    } as QNotifyCreateOptions);
  }
};

// Submit principal
const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  loading.value = true;

  try {
    if (esNuevaOrden.value) {
      // === CREAR NUEVA ORDEN ===
      const estadoPendiente = estadosOrden.value.find((e) => e.nombre === "PENDIENTE");

      const payload = {
        codigoOrden: ordenForm.value.codigoOrden,
        vehiculo: {
          id: vehiculoSeleccionado.value?.id || vehiculoSeleccionado.value,
        },
        estadoOrden: {
          id: estadoPendiente?.id || "1",
        },
        fechaOrden: new Date().toISOString().split("T")[0],
        observaciones: ordenForm.value.observaciones,
        total: totalArticulos.value,
        pagada: false,
      };

      const response = await tallerApi.post("/ordenes", payload);

      // Guardar artículos si los hay
      if (ordenForm.value.articulosUsados.length > 0) {
        await guardarArticulosUsadosEnOrden(response.data.id);
      }

      $q.notify({ type: "positive", message: "Orden creada correctamente" });
      emit("created");
      resetForm();
    } else {
      // === EDITAR ORDEN EXISTENTE ===
      const { articulosUsados: _, ...payloadSinArticulos } = ordenForm.value;
      void _;

      // Verificar si hay cambios en la orden principal
      const hayChangesEnOrden =
        ordenForm.value.codigoOrden !== props.editData?.codigoOrden ||
        ordenForm.value.observaciones !== props.editData?.observaciones ||
        totalArticulos.value !== props.editData?.total;

      // Solo actualizar la orden si hay cambios
      if (hayChangesEnOrden) {
        const payload = {
          id: props.editData!.id, // ← AÑADIR EL ID
          ...payloadSinArticulos,
          empleadoAsignado: empleadoSeleccionado.value
            ? { id: empleadoSeleccionado.value.id || empleadoSeleccionado.value }
            : null,
          vehiculo: {
            id: vehiculoSeleccionado.value?.id || vehiculoSeleccionado.value,
          },
          total: totalArticulos.value,
        };

        await tallerApi.put(`/ordenes/${props.editData!.id}`, payload);
      }

      // Siempre guardar artículos (se controla internamente si hay cambios)
      if (ordenForm.value.articulosUsados.length > 0) {
        await guardarArticulosUsadosEnOrden(props.editData!.id);
      }

      $q.notify({
        type: "positive",
        message: hayChangesEnOrden ? "Orden actualizada" : "Artículos actualizados",
      });
      emit("updated");
    }
  } catch (error) {
    console.error("Error al guardar:", error);
    $q.notify({ type: "negative", message: "Error al guardar la orden" });
  } finally {
    loading.value = false;
  }
};

const guardarArticulosUsadosEnOrden = async (ordenId: string) => {
  try {
    for (const articulo of ordenForm.value.articulosUsados) {
      // Validar que tenemos los datos necesarios
      if (!articulo.articulo?.id || !articulo.cantidad) {
        console.warn("Artículo sin datos completos, saltando:", articulo);
        continue;
      }

      const articuloPayload = {
        articuloId: articulo.articulo.id,
        cantidad: articulo.cantidad,
        ordenTrabajoId: parseInt(ordenId),
      };

      console.log("Enviando payload:", articuloPayload);

      if (articulo.id && props.editData?.id) {
        // ACTUALIZAR artículo existente
        console.log(`Actualizando artículo usado ID: ${articulo.id}`);
        await tallerApi.put(`/articulos-usados/guardar/${articulo.id}`, articuloPayload);
      } else {
        // CREAR nuevo artículo
        console.log("Creando nuevo artículo usado");
        await tallerApi.post(`/articulos-usados/guardar`, articuloPayload);
      }
    }
  } catch (error) {
    console.error("Error al guardar artículos:", error);
    $q.notify({
      type: "warning",
      message: "Orden guardada, pero hubo errores con los artículos",
    });
  }
};

const resetForm = () => {
  ordenForm.value = {
    codigoOrden: "",
    empleadoAsignado: { id: "" },
    vehiculo: { id: "" },
    estadoOrden: { id: "" },
    fechaOrden: "",
    fechaInicio: "",
    fechaFinalizacion: "",
    fechaPago: "",
    pagada: false,
    articulosUsados: [],
    observaciones: "",
    total: 0,
  };
  empleadoSeleccionado.value = null;
  vehiculoSeleccionado.value = null;
  estadoActual.value = "PENDIENTE";
  formRef.value?.resetValidation();
};

// Watch para datos de edición
watch(
  () => props.editData,
  async (data) => {
    if (data) {
      ordenForm.value = {
        codigoOrden: data.codigoOrden || "",
        fechaOrden: data.fechaOrden || "",
        fechaInicio: data.fechaInicio || "",
        fechaFinalizacion: data.fechaFinalizacion || "",
        fechaPago: data.fechaPago || "",
        empleadoAsignado: data.empleadoAsignado || { id: "" },
        vehiculo: data.vehiculo || { id: "" },
        observaciones: data.observaciones || "",
        estadoOrden: data.estadoOrden || { id: "" },
        pagada: data.pagada || false,
        total: data.total || 0,
        articulosUsados: data.articulosUsados
          ? data.articulosUsados.map((articulo) => ({
              ...articulo,
              cantidad: Number(articulo.cantidad) || 0,
            }))
          : [],
      };

      // Cargar estado actual
      if (data.estadoOrden && estadosOrden.value.length > 0) {
        const estadoEncontrado = estadosOrden.value.find((e) => e.id == data.estadoOrden.id);
        estadoActual.value = estadoEncontrado?.nombre || "PENDIENTE";
        console.log(
          "Estado encontrado en watcher:",
          estadoEncontrado,
          "estadoActual:",
          estadoActual.value,
        );
      }

      // Cargar empleado y vehículo
      if (data.empleadoAsignado?.id) {
        try {
          const { data: empleado } = await tallerApi.get<Empleado>(
            `/empleados/${data.empleadoAsignado.id}`,
          );
          empleadoSeleccionado.value = empleado;
        } catch (error) {
          console.error("Error cargando empleado:", error);
        }
      }

      if (data.vehiculo?.id) {
        try {
          const { data: vehiculo } = await tallerApi.get<Vehiculo>(
            `/vehiculos/${data.vehiculo.id}`,
          );
          vehiculoSeleccionado.value = vehiculo;
        } catch (error) {
          console.error("Error cargando vehículo:", error);
        }
      }
    } else {
      resetForm();
    }
  },
  { immediate: true },
);
watch(
  () => estadosOrden.value,
  () => {
    const editData = props.editData;
    if (editData?.estadoOrden && estadosOrden.value.length > 0) {
      const estadoEncontrado = estadosOrden.value.find((e) => e.id == editData.estadoOrden.id);
      estadoActual.value = estadoEncontrado?.nombre || "PENDIENTE";
      console.log("Estado actualizado tras cargar estados:", estadoActual.value);
    }
  },
);

onMounted(async () => {
  await cargarEstadosOrden();
});
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto auto;
  gap: 1.5rem;
  grid-template-areas:
    "info estado"
    "observaciones observaciones"
    "articulos articulos";
}

.form-section:nth-child(1) {
  grid-area: info;
}

.form-section:nth-child(2) {
  grid-area: estado;
}

.observaciones-section {
  grid-area: observaciones;
}

.articulos-section {
  grid-area: articulos;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  max-height: 500px;
  display: flex;
  flex-direction: column;
}

@media (max-width: 900px) {
  .form-grid {
    grid-template-columns: 1fr;
    grid-template-areas:
      "info"
      "estado"
      "observaciones"
      "articulos";
    gap: 1rem;
  }
}

.estado-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.estado-actual {
  display: flex;
  justify-content: center;
}

.estado-chip {
  font-size: 1rem;
  padding: 0.5rem 1rem;
}

.fechas-info {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 1rem;
}

.fecha-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.fecha-item:last-child {
  margin-bottom: 0;
}

.empleado-container {
  margin-top: 0.5rem;
}

.acciones-estado {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 1rem;
}

.accion-btn {
  width: 100%;
}

.articulos-container {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  background-color: #fafafa;
  flex: 1;
  overflow-y: auto;
  max-height: 350px;
}

.articulos-container::-webkit-scrollbar {
  width: 6px;
}

.articulos-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.articulos-container::-webkit-scrollbar-thumb {
  background: rgba(59, 130, 246, 0.3);
  border-radius: 3px;
}

.articulos-container::-webkit-scrollbar-thumb:hover {
  background: rgba(59, 130, 246, 0.5);
}

.articulo-item {
  display: grid;
  grid-template-columns: 2fr 100px 120px 40px;
  gap: 12px;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  margin-bottom: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.2s ease;
}

.articulo-item:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.precio-info {
  display: flex;
  flex-direction: column;
  text-align: right;
}

.precio-unitario {
  font-size: 0.85em;
  color: #666;
}

.precio-total {
  font-weight: 600;
  color: #1976d2;
}

.total-container {
  margin-top: 16px;
  border-top: 2px solid #e0e0e0;
  background: white;
  border-radius: 6px;
  padding: 16px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.1em;
  font-weight: 600;
}

.total-label {
  color: #333;
}

.total-value {
  color: #1976d2;
  font-size: 1.2em;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #e2e8f0;
}

.section-icon {
  font-size: 1.25rem;
  color: #3b82f6;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.add-btn {
  margin-left: auto;
}

.delete-btn {
  justify-self: center;
}

.input-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.input-large {
  width: 100%;
}

.textarea-full {
  width: 100%;
}

.inputs-container {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1rem;
  border-top: 1px solid #e0e0e0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
}

.cancel-btn {
  min-width: 120px;
}

.submit-btn {
  min-width: 120px;
}

.cancelar-container {
  margin-top: 1rem;
  text-align: center;
}

.cancelar-btn {
  width: 100%;
  border: 2px dashed #f44336;
}

.cancelar-btn:hover {
  background-color: rgba(244, 67, 54, 0.1);
}

@media (max-width: 768px) {
  .articulo-item {
    grid-template-columns: 1fr;
    gap: 8px;
    text-align: center;
  }

  .precio-info {
    text-align: center;
  }

  .input-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .acciones-estado {
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column;
  }

  .cancel-btn,
  .submit-btn {
    width: 100%;
  }
}
</style>
