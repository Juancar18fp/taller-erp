<template>
  <div class="dialog-form-container">
    <q-scroll-area class="form-scroll-area">
      <q-form ref="formRef" @submit="handleSubmit" class="form-content">
        <div class="form-grid">
          <div class="form-section">
            <div class="section-header">
              <q-icon name="work" class="section-icon" />
              <h3 class="section-title">Información de la orden</h3>
            </div>

            <div class="inputs-container">
              <div class="input-row">
                <div class="readonly-field">
                  <label class="readonly-label">Código de orden</label>
                  <div class="readonly-value">
                    {{ ordenForm.codigoOrden || "Se generará automáticamente" }}
                  </div>
                </div>

                <div class="readonly-field">
                  <label class="readonly-label">Fecha de orden</label>
                  <div class="readonly-value">{{ fechaOrdenDisplay }}</div>
                </div>
              </div>

              <q-select
                v-model="vehiculoSeleccionado"
                :options="vehiculosOptions"
                option-value="id"
                option-label="matricula"
                use-input
                map-options
                emit-value
                input-debounce="300"
                label="Vehículo"
                :placeholder="!vehiculoSeleccionado ? 'Buscar vehiculo...' : ''"
                outlined
                dense
                clearable
                :rules="[required]"
                class="input-large"
                @filter="filtrarVehiculos"
                @update:model-value="actualizarCliente"
                :disable="!puedeEditarOrden"
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

              <div class="readonly-field">
                <label class="readonly-label">Cliente</label>
                <div class="readonly-value">
                  {{ clienteNombre || "Se rellenará automáticamente" }}
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <q-icon name="timeline" class="section-icon" />
              <h3 class="section-title">Estado de la orden</h3>
            </div>

            <div class="estado-container">
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
                  :disable="!puedeEditarOrden"
                  @filter="filtrarEmpleados"
                />
              </div>

              <div class="acciones-estado" v-if="!esNuevaOrden && estadoActual !== 'CANCELADA'">
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

                <q-btn
                  v-if="estadoActual === 'PENDIENTE' && tieneEmpleadoAsignado"
                  label="Iniciar Trabajo"
                  icon="play_arrow"
                  color="green"
                  @click="iniciarTrabajo"
                  class="accion-btn"
                />

                <q-btn
                  v-if="estadoActual === 'INICIADA'"
                  label="Finalizar Trabajo"
                  icon="check_circle"
                  color="blue"
                  @click="finalizarTrabajo"
                  class="accion-btn"
                />

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
              :readonly="!puedeEditarOrden"
            />
          </div>
          <div v-if="!esNuevaOrden && !puedeEditarOrden" class="readonly-notice">
            <q-banner class="bg-blue-1 text-blue-8" rounded>
              <template v-slot:avatar>
                <q-icon name="info" color="blue" />
              </template>
              Esta orden no se puede modificar porque está
              {{ getEstadoNombre(estadoActual).toLowerCase() }}.
            </q-banner>
          </div>

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
                :disable="!puedeEditarArticulos"
              />
            </div>

            <div class="articulos-container">
              <div
                v-for="(articuloUsado, index) in ordenForm.articulosUsados"
                :key="index"
                class="articulo-item"
              >
                <div class="articulo-select-container">
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
                    :disable="!puedeEditarArticulos"
                    @update:model-value="(val) => actualizarPrecioArticulo(index, val)"
                    class="articulo-select"
                  >
                    <template v-slot:option="scope">
                      <q-item v-bind="scope.itemProps">
                        <q-item-section>
                          <q-item-label>{{ scope.opt.descripcion }}</q-item-label>
                          <q-item-label caption>
                            Stock: {{ scope.opt.stock }} | €{{ scope.opt.precio.toFixed(2) }}
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                </div>

                <div class="cantidad-container">
                  <div class="cantidad-label">Cantidad</div>
                  <q-input
                    v-model.number="articuloUsado.cantidad"
                    type="number"
                    outlined
                    dense
                    min="1"
                    :max="articuloUsado.articulo?.stock || 999"
                    :disable="!puedeEditarArticulos"
                    class="cantidad-input"
                    @update:model-value="(val) => validarCantidadStock(index, Number(val) || 0)"
                    :rules="[(val) => validarStock(index, Number(val) || 0)]"
                  />
                </div>

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
                  <span class="stock-info" v-if="articuloUsado.articulo?.stock !== undefined">
                    Stock: {{ articuloUsado.articulo.stock }}
                  </span>
                </div>

                <q-btn
                  icon="delete"
                  color="negative"
                  dense
                  round
                  :disable="!puedeEditarArticulos"
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
        :disable="!esNuevaOrden && !puedeEditarOrden"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import type { QNotifyCreateOptions } from "quasar";
import { useQuasar } from "quasar";
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

const empleadoSeleccionado = ref<Empleado | null>(null);
const vehiculoSeleccionado = ref<Vehiculo | null>(null);
const estadoActual = ref<string>("PENDIENTE");
const empleadosOptions = ref<Empleado[]>([]);
const vehiculosOptions = ref<Vehiculo[]>([]);
const articulosOptions = ref<Articulo[]>([]);
const estadosOrden = ref<EstadoOrden[]>([]);

const esNuevaOrden = computed(() => !props.editData?.id);
const tieneEmpleadoAsignado = computed(() => !!ordenForm.value.empleadoAsignado?.id);
const puedeSerCancelada = computed(() => {
  return !["COMPLETADA", "CANCELADA"].includes(estadoActual.value);
});

const puedeEditarArticulos = computed(() => {
  return ["PENDIENTE", "INICIADA"].includes(estadoActual.value);
});
const puedeEditarOrden = computed(() => {
  return !["FINALIZADA", "COMPLETADA", "CANCELADA"].includes(estadoActual.value);
});

const fechaOrdenDisplay = computed(() => {
  const hoy = new Date();
  const dia = hoy.getDate().toString().padStart(2, "0");
  const mes = (hoy.getMonth() + 1).toString().padStart(2, "0");
  const anio = hoy.getFullYear();
  return `${dia}/${mes}/${anio}`;
});

const totalArticulos = computed(() => {
  return ordenForm.value.articulosUsados.reduce((total, item) => {
    const precio = item.articulo?.precio || 0;
    const cantidad = item.cantidad || 0;
    return total + precio * cantidad;
  }, 0);
});

const generarCodigoOrden = async () => {
  if (!esNuevaOrden.value) return;

  const hoy = new Date();
  const dia = hoy.getDate().toString().padStart(2, "0");
  const mes = (hoy.getMonth() + 1).toString().padStart(2, "0");
  const anio = hoy.getFullYear().toString();

  try {
    const { data } = await tallerApi.get(
      `/ordenes/siguiente-numero?fecha=${hoy.toISOString().split("T")[0]}`,
    );
    const numeroOrden = data.siguienteNumero.toString().padStart(3, "0");
    ordenForm.value.codigoOrden = `OT-${anio}${mes}${dia}-${numeroOrden}`;
  } catch (error) {
    console.error("Error generando código:", error);
    ordenForm.value.codigoOrden = `OT-${anio}${mes}${dia}-001`;
  }
};
const validarStock = (index: number, cantidad: number) => {
  const articulo = ordenForm.value.articulosUsados[index]?.articulo;
  if (!articulo || !cantidad || typeof articulo.stock !== "number") return true;

  if (cantidad > articulo.stock) {
    return `Stock insuficiente. Disponible: ${articulo.stock}`;
  }
  return true;
};

const validarCantidadStock = (index: number, cantidad: number) => {
  const articuloUsado = ordenForm.value.articulosUsados[index];
  const articulo = articuloUsado?.articulo;

  if (articulo && typeof articulo.stock === "number" && cantidad > articulo.stock) {
    $q.notify({
      type: "warning",
      message: `Stock insuficiente. Disponible: ${articulo.stock}`,
      timeout: 2000,
    });
    if (articuloUsado) {
      articuloUsado.cantidad = articulo.stock;
    }
  }
  calcularTotal();
};

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
  await descontarStockArticulos();

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

const descontarStockArticulos = async () => {
  try {
    for (const articuloUsado of ordenForm.value.articulosUsados) {
      if (articuloUsado.articulo?.id && articuloUsado.cantidad) {
        await tallerApi.put(`/articulos/${articuloUsado.articulo.id}/descontar-stock`, {
          cantidad: articuloUsado.cantidad,
        });
      }
    }
  } catch (error) {
    console.error("Error descontando stock:", error);
    $q.notify({
      type: "warning",
      message: "Error al descontar stock de algunos artículos",
    });
  }
};

const clienteNombre = ref<string>("");

const actualizarCliente = (vehiculoId: string | null) => {
  if (vehiculoId) {
    let vehiculo = vehiculosOptions.value.find((v) => v.id === vehiculoId);

    if (!vehiculo && vehiculoSeleccionado.value) {
      if (
        typeof vehiculoSeleccionado.value === "object" &&
        vehiculoSeleccionado.value.id === vehiculoId
      ) {
        vehiculo = vehiculoSeleccionado.value;
      }
    }

    if (vehiculo && vehiculo.cliente) {
      clienteNombre.value = vehiculo.cliente.nombre;
    } else {
      console.warn("No se encontró vehículo o cliente para ID:", vehiculoId);
    }
  } else {
    clienteNombre.value = "";
  }
};

const cargarClienteDesdeVehiculo = async (vehiculoId: string) => {
  try {
    const { data: vehiculo } = await tallerApi.get<Vehiculo>(`/vehiculos/${vehiculoId}`);
    if (vehiculo && vehiculo.cliente) {
      clienteNombre.value = vehiculo.cliente.nombre;
    }
  } catch (error) {
    console.error("Error cargando cliente desde vehículo:", error);
  }
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

    const { articulosUsados: _, ...ordenSinArticulos } = ordenForm.value;
    void _;

    const payload = {
      id: props.editData!.id,
      ...ordenSinArticulos,
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

    const { data: ordenActualizada } = await tallerApi.get(`/ordenes/${props.editData!.id}`);

    if (ordenActualizada.estadoOrden) {
      const estadoEncontrado = estadosOrden.value.find(
        (e) => e.id == ordenActualizada.estadoOrden.id,
      );
      estadoActual.value = estadoEncontrado?.nombre || "PENDIENTE";
    }

    Object.assign(ordenForm.value, ordenActualizada);

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
      articulosOptions.value = Array.isArray(data)
        ? data.filter((articulo) => articulo.stock > 0)
        : [];
    });
  } catch (error) {
    console.error("Error buscando artículos:", error);
    update(() => {
      articulosOptions.value = [];
    });
  }
};

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
  const articuloUsado = ordenForm.value.articulosUsados[index];
  if (articuloUsado) {
    articuloUsado.articulo = articulo ?? {
      id: "",
      descripcion: "",
      precio: 0,
    };

    if (articulo && typeof articulo.stock === "number" && articuloUsado.cantidad > articulo.stock) {
      articuloUsado.cantidad = Math.min(articulo.stock, 1);
    }

    calcularTotal();
  }
};

const calcularTotal = () => {
  ordenForm.value.total = totalArticulos.value;
};

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

const handleSubmit = async () => {
  if (!esNuevaOrden.value && !puedeEditarOrden.value) {
    $q.notify({
      type: "warning",
      message:
        "No se puede modificar una orden que está " +
        getEstadoNombre(estadoActual.value).toLowerCase(),
    });
    return;
  }
  const valid = await formRef.value?.validate();
  if (!valid) return;

  for (let i = 0; i < ordenForm.value.articulosUsados.length; i++) {
    const articuloUsado = ordenForm.value.articulosUsados[i];
    if (
      articuloUsado?.articulo &&
      articuloUsado.cantidad &&
      typeof articuloUsado.articulo.stock === "number"
    ) {
      if (articuloUsado.cantidad > articuloUsado.articulo.stock) {
        $q.notify({
          type: "negative",
          message: `Stock insuficiente para ${articuloUsado.articulo.descripcion}. Disponible: ${articuloUsado.articulo.stock}`,
        });
        return;
      }
    }
  }

  loading.value = true;

  try {
    if (esNuevaOrden.value) {
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

      if (ordenForm.value.articulosUsados.length > 0) {
        await guardarArticulosUsadosEnOrden(response.data.id);
      }

      $q.notify({ type: "positive", message: "Orden creada correctamente" });
      emit("created");
      resetForm();
    } else {
      const { articulosUsados: _, ...payloadSinArticulos } = ordenForm.value;
      void _;

      const hayChangesEnOrden =
        ordenForm.value.codigoOrden !== props.editData?.codigoOrden ||
        ordenForm.value.observaciones !== props.editData?.observaciones ||
        totalArticulos.value !== props.editData?.total;

      if (hayChangesEnOrden) {
        const payload = {
          id: props.editData!.id,
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
      if (!articulo.articulo?.id || !articulo.cantidad) {
        console.warn("Artículo sin datos completos, saltando:", articulo);
        continue;
      }

      const articuloPayload = {
        articuloId: articulo.articulo.id,
        cantidad: articulo.cantidad,
        ordenTrabajoId: parseInt(ordenId),
      };

      if (articulo.id && props.editData?.id) {
        await tallerApi.put(`/articulos-usados/guardar/${articulo.id}`, articuloPayload);
      } else {
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

      if (data.estadoOrden && estadosOrden.value.length > 0) {
        const estadoEncontrado = estadosOrden.value.find((e) => e.id == data.estadoOrden.id);
        estadoActual.value = estadoEncontrado?.nombre || "PENDIENTE";
      }

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

          if (vehiculo.cliente) {
            clienteNombre.value = vehiculo.cliente.nombre;
          }
        } catch (error) {
          console.error("Error cargando vehículo:", error);
          await cargarClienteDesdeVehiculo(data.vehiculo.id);
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
    }
  },
);

onMounted(async () => {
  await cargarEstadosOrden();
  if (esNuevaOrden.value) {
    await generarCodigoOrden();
  }
});
</script>
<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto auto auto;
  gap: 1.5rem;
  grid-template-areas:
    "info estado"
    "pago pago"
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
  grid-template-columns: 1fr auto auto auto;
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

.articulo-select-container {
  min-width: 0;
}

.articulo-select {
  width: 100%;
}

.precio-info {
  display: flex;
  flex-direction: column;
  text-align: right;
  min-width: 120px;
  align-items: flex-end;
}

.precio-unitario {
  font-size: 0.85em;
  color: #666;
  margin-bottom: 2px;
}

.precio-total {
  font-weight: 600;
  color: #1976d2;
  font-size: 1em;
}

.cantidad-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;
}

.cantidad-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
  text-align: center;
}

.cantidad-input {
  width: 70px;
  text-align: center;
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

.stock-info {
  font-size: 0.75em;
  color: #888;
  margin-top: 2px;
}

.textarea-full {
  width: 100%;
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

.readonly-field {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.readonly-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
  font-weight: 500;
}

.readonly-value {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #f5f5f5;
  color: #333;
  font-size: 14px;
  min-height: 20px;
  display: flex;
  align-items: center;
  cursor: default;
  user-select: none;
}

.readonly-notice {
  margin-bottom: 1rem;
  grid-column: 1 / -1;
}

.q-field--disabled {
  opacity: 0.6;
}

.q-input--readonly  {
  background-color: #f5f5f5;
  opacity: 0.8;
}
</style>
