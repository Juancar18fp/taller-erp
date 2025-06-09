<template>
  <q-card flat bordered class="puestos-table-card">
    <q-card-section>
      <div class="table-header q-mb-md">
        <div class="row items-center justify-between">
          <div class="row items-center">
            <q-icon name="work" size="sm" color="primary" class="q-mr-sm" />
            <div>
              <div class="text-h6 text-primary">Puestos de Trabajo</div>
              <div class="text-body2 text-grey-7">Gestiona los puestos de trabajo del taller</div>
            </div>
          </div>

          <q-btn
            color="primary"
            icon="add"
            label="Nuevo Puesto"
            @click="showCreateDialog = true"
            unelevated
            size="md"
          />
        </div>
      </div>

      <q-table
        :rows="rows"
        :columns="puestosColumns"
        row-key="id"
        :loading="loading"
        :pagination="{ rowsPerPage: 15, sortBy: 'nombre' }"
        flat
        bordered
        separator="cell"
        @row-click="editItem"
        class="puestos-table"
      >
        <template v-slot:no-data>
          <div class="no-data-container">
            <q-icon name="work_off" size="xl" color="grey-5" />
            <div class="text-h6 text-grey-7 q-mt-md">No hay puestos registrados</div>
            <div class="text-body2 text-grey-6">Comienza creando el primer puesto de trabajo</div>
            <q-btn
              color="primary"
              icon="add"
              label="Crear Primer Puesto"
              @click="showCreateDialog = true"
              class="q-mt-md"
              unelevated
            />
          </div>
        </template>
      </q-table>
    </q-card-section>

    <q-dialog v-model="showCreateDialog" persistent>
      <q-card style="min-width: 500px; max-width: 600px">
        <q-card-section class="dialog-header">
          <div class="text-h6 text-primary">
            <q-icon :name="editingItem ? 'edit' : 'add'" class="q-mr-sm" />
            {{ editingItem ? "Editar" : "Crear" }} Puesto de Trabajo
          </div>
        </q-card-section>

        <q-separator />

        <q-card-section>
          <q-form @submit="saveItem" class="puesto-form">
            <div class="form-grid">
              <q-input
                v-model="form.nombre"
                label="Nombre del Puesto *"
                outlined
                dense
                :rules="[
                  (val) => !!val || 'El nombre es obligatorio',
                  (val) => val.length >= 2 || 'Mínimo 2 caracteres',
                ]"
                placeholder="Ej: Mecánico Senior, Recepcionista..."
                class="form-field"
              />

              <q-select
                v-model="form.rol.id"
                :options="roles"
                label="Selecciona un rol *"
                outlined
                dense
                option-value="id"
                option-label="nombre"
                emit-value
                map-options
                :rules="[(val) => !!val || 'El rol es obligatorio']"
                placeholder="Elige un rol..."
                :loading="loadingRoles"
              >
                <template v-slot:no-option>
                  <q-item>
                    <q-item-section class="text-grey"> No hay roles disponibles </q-item-section>
                  </q-item>
                </template>
              </q-select>
            </div>
          </q-form>
        </q-card-section>

        <q-separator />

        <q-card-actions align="right" class="q-pa-md">
          <q-btn
            v-if="editingItem"
            flat
            label="Eliminar"
            @click="deleteItem(editingItem)"
            color="negative"
            icon="delete"
          />
          <q-space v-if="editingItem" />
          <q-btn flat label="Cancelar" @click="cancelEdit" color="grey-7" />
          <q-btn
            color="primary"
            :label="editingItem ? 'Actualizar' : 'Crear'"
            @click="saveItem"
            :loading="saving"
            unelevated
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useQuasar } from "quasar";
import type { QNotifyCreateOptions, QTableColumn } from "quasar";
import tallerApi from "../api/tallerApi";

const puestosColumns: QTableColumn[] = [
  {
    name: "id",
    label: "ID",
    field: "id",
    sortable: true,
    required: true,
    align: "left",
    style: "width: 80px",
  },
  {
    name: "nombre",
    label: "Nombre del Puesto",
    field: "nombre",
    sortable: true,
    align: "left",
    style: "min-width: 200px",
  },
  {
    name: "rol",
    label: "Rol del Puesto",
    field: (row) => row.rol?.nombre || "Sin rol",
    sortable: true,
    align: "left",
    style: "min-width: 200px",
  },
];

export interface Rol {
  id: string;
  nombre: string;
}

interface PuestoItem {
  id: number;
  nombre: string;
  rol: {
    id: string;
    nombre?: string;
  };
}

interface Puesto {
  nombre: string;
  rol: {
    id: string;
    nombre?: string;
  };
}

const $q = useQuasar();

const rows = ref<PuestoItem[]>([]);
const loading = ref<boolean>(false);
const loadingRoles = ref<boolean>(false);
const saving = ref<boolean>(false);
const showCreateDialog = ref<boolean>(false);
const editingItem = ref<PuestoItem | null>(null);
const roles = ref<Rol[]>([]);

const form = ref<Puesto>({
  nombre: "",
  rol: {
    id: "",
  },
});

const cargarRoles = async () => {
  loadingRoles.value = true;
  try {
    const { data } = await tallerApi.get<Rol[]>("/roles/all");
    roles.value = data.map((m) => ({
      id: m.id,
      nombre: m.nombre,
    }));
  } catch (error) {
    console.error("Error cargando roles:", error);
    $q.notify({
      type: "negative",
      message: "Error cargando roles",
      position: "top",
    } as QNotifyCreateOptions);
  } finally {
    loadingRoles.value = false;
  }
};

const loadData = async (): Promise<void> => {
  loading.value = true;
  try {
    const response = await tallerApi.get("/puestos/all");
    rows.value = response.data || [];
  } catch (error) {
    console.error("Error cargando puestos:", error);
    $q.notify({
      type: "negative",
      message: "Error al cargar los puestos de trabajo",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

const editItem = (_event: unknown, row: PuestoItem): void => {
  editingItem.value = row;
  form.value = {
    nombre: row.nombre || "",
    rol: {
      id: row.rol?.id || "",
      nombre: row.rol?.nombre || "",
    },
  };
  showCreateDialog.value = true;
};

const deleteItem = (item: PuestoItem): void => {
  if (confirm(`¿Estás seguro de eliminar el puesto "${item.nombre}"?`)) {
    void handleDelete(item);
  }
};

const handleDelete = async (item: PuestoItem): Promise<void> => {
  try {
    await tallerApi.delete(`/puestos/${item.id}`);
    $q.notify({
      type: "positive",
      message: `Puesto "${item.nombre}" eliminado correctamente`,
      icon: "check_circle",
    });
    await loadData();
  } catch (error) {
    console.error("Error eliminando puesto:", error);
    $q.notify({
      type: "negative",
      message: "Error al eliminar el puesto",
      icon: "error",
    });
  }
};

const saveItem = async (): Promise<void> => {
  if (!form.value.nombre.trim()) {
    $q.notify({
      type: "warning",
      message: "El nombre del puesto es obligatorio",
      icon: "warning",
    });
    return;
  }

  if (form.value.nombre.length < 2) {
    $q.notify({
      type: "warning",
      message: "El nombre debe tener al menos 2 caracteres",
      icon: "warning",
    });
    return;
  }

  if (!form.value.rol.id) {
    $q.notify({
      type: "warning",
      message: "Debe seleccionar un rol",
      icon: "warning",
    });
    return;
  }

  saving.value = true;
  try {
    const payload = {
      nombre: form.value.nombre,
      rol: {
        id: form.value.rol.id,
      },
    };
    const editPayload = {
      id: editingItem.value ? editingItem.value.id : undefined,
      nombre: form.value.nombre,
      rol: {
        id: form.value.rol.id,
      },
    };

    if (editingItem.value) {
      await tallerApi.put(`/puestos/${editingItem.value.id}`, editPayload);
      $q.notify({
        type: "positive",
        message: `Puesto "${form.value.nombre}" actualizado correctamente`,
        icon: "check_circle",
      });
    } else {
      await tallerApi.post("/puestos", payload);
      $q.notify({
        type: "positive",
        message: `Puesto "${form.value.nombre}" creado correctamente`,
        icon: "check_circle",
      });
    }

    cancelEdit();
    await loadData();
  } catch (error) {
    console.error("Error guardando puesto:", error);
    $q.notify({
      type: "negative",
      message: "Error al guardar el puesto",
      icon: "error",
    });
  } finally {
    saving.value = false;
  }
};

const cancelEdit = (): void => {
  showCreateDialog.value = false;
  editingItem.value = null;
  form.value = {
    nombre: "",
    rol: {
      id: "",
      nombre: "",
    },
  };
};

onMounted(async () => {
  await Promise.all([loadData(), cargarRoles()]);
});
</script>

<style scoped>
.puestos-table-card {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

.table-header {
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.puestos-table {
  border-radius: 8px;
  overflow: hidden;
}

.puestos-table {
  padding: 0;
}

.no-data-container {
  text-align: center;
  padding: 3rem 1rem;
}

.dialog-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;
}

.puesto-form {
  width: 100%;
}

.form-grid {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-field {
  width: 100%;
}

.puestos-table tbody tr:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

.action-buttons  {
  transform: scale(1.1);
  transition: transform 0.2s ease;
}
</style>
