<template>
  <div class="q-pa-md">
    <q-table
      flat
      bordered
      ref="tableRef"
      :rows="rows"
      :columns="columns"
      row-key="id"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      binary-state-sort
      virtual-scroll
      @request="onRequest"
      :rows-per-page-options="[5, 10, 20]"
      @row-click="onRowClick"
    >
      <template v-slot:top>
        <div class="row justify-between items-center full-width">
          <div class="row items-center q-gutter-sm">
            <span class="text-h6">{{ customProps.title }}</span>
          </div>
          <div class="row items-center q-gutter-sm absolute-center">
            <q-input outlined dense debounce="300" v-model="filter" placeholder="Buscar">
              <template v-slot:append><q-icon name="search" /></template>
            </q-input>
          </div>
          <div class="row items-center q-gutter-sm">
            <q-btn color="primary" icon="add" :label="buttonLabel" @click="showCreateDialog" />
          </div>
        </div>
      </template>
    </q-table>

    <CreateCustomDialog
      v-if="createDialogVisible"
      v-model="createDialogVisible"
      @created="refreshTable"
      @updated="refreshTable"
      :route="customProps.route"
      :edit-data="selectedItem || undefined"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from "vue";
import { QTable, useQuasar } from "quasar";
import type { QTableProps, QTableColumn } from "quasar";
import tallerApi from "../api/tallerApi";
import CreateCustomDialog from "./CustomDialog.vue";

interface BaseEntity {
  id: number;
  [key: string]: unknown;
}

interface CustomTableProps {
  columns: QTableColumn[];
  title: string;
  route: string;
}

interface ApiResponse<T = BaseEntity> {
  items: T[];
  total: number;
}

const customProps = defineProps<CustomTableProps>();
const $q = useQuasar();
const tableRef = ref<QTable>();
// Asegurar que rows siempre sea un array y reactivo
const rows = ref<BaseEntity[]>([]);

// Verificación adicional para debugging
console.log("Initial rows state:", {
  rowsValue: rows.value,
  rowsType: typeof rows.value,
  isArray: Array.isArray(rows.value),
});
const filter = ref("");
const loading = ref(false);
const createDialogVisible = ref(false);
const selectedItem = ref<BaseEntity | undefined>(undefined);

const buttonLabel = computed(() =>
  customProps.route.toLowerCase().includes("clientes")
    ? "Nuevo Cliente"
    : customProps.route.toLowerCase().includes("vehiculos")
      ? "Nuevo Vehículo"
      : customProps.route.toLowerCase().includes("articulos")
        ? "Nuevo Artículo"
        : customProps.route.toLowerCase().includes("empleados")
          ? "Nuevo Empleado"
          : customProps.route.toLowerCase().includes("ordenes")
            ? "Nueva Orden de Trabajo"
            : "Nuevo",
);

const showCreateDialog = () => {
  selectedItem.value = undefined;
  createDialogVisible.value = true;
};

const showEditDialog = async (item: BaseEntity) => {
  try {
    loading.value = true;
    const { data } = await tallerApi.get(`${customProps.route}/${item.id}`);
    selectedItem.value = data;
    createDialogVisible.value = true;
  } catch (error) {
    console.error("Error loading item:", error);
    $q.notify({
      type: "negative",
      message: "Error al cargar los datos del elemento",
    });
  } finally {
    loading.value = false;
  }
};

const refreshTable = () => {
  // Usar nextTick para asegurar que el DOM se actualice
  void nextTick(() => {
    tableRef.value?.requestServerInteraction();
  });
};

const pagination = ref<QTableProps["pagination"]>({
  sortBy: "id",
  descending: false,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
});

const onRequest = async (props: Parameters<NonNullable<QTableProps["onRequest"]>>[0]) => {
  try {
    loading.value = true;
    const { page, rowsPerPage, sortBy, descending } = props.pagination;
    const filterVal = props.filter;

    const params = {
      page,
      size: rowsPerPage,
      sortBy,
      direction: descending ? "desc" : "asc",
      filter: filterVal,
    };

    const { data } = await tallerApi.get<ApiResponse>(customProps.route, { params });

    // Debug: Verificar la estructura de la respuesta
    console.log("API Response structure:", {
      data: data,
      hasItems: data && "items" in data,
      itemsType: typeof data?.items,
      isArray: Array.isArray(data?.items),
      itemsLength: data?.items?.length,
    });

    // Validar que data exista y tenga items como array
    if (data && Array.isArray(data.items)) {
      rows.value = data.items;
      pagination.value = {
        ...props.pagination,
        rowsNumber: data.total || 0,
      };
    } else {
      console.error("Invalid API response structure:", {
        data: data,
        dataType: typeof data,
        hasItems: data && "items" in data,
        itemsValue: data?.items,
        itemsType: typeof data?.items,
      });
      rows.value = [];
      pagination.value = {
        ...props.pagination,
        rowsNumber: 0,
      };
    }
  } catch (error) {
    console.error("Error loading data:", error);
    // Asegurar que rows siempre sea un array en caso de error
    rows.value = [];
    pagination.value = {
      ...pagination.value,
      rowsNumber: 0,
    };
    $q.notify({
      type: "negative",
      message: "Error cargando los datos",
    });
  } finally {
    loading.value = false;
  }
};

const onRowClick = (_evt: Event, row: BaseEntity) => {
  if (row && row.id) {
    void showEditDialog(row);
  }
};

onMounted(async () => {
  // Verificar el estado antes de la primera carga
  console.log("OnMounted - rows state:", {
    rowsValue: rows.value,
    isArray: Array.isArray(rows.value),
    length: rows.value?.length,
  });

  // Esperar al siguiente tick para asegurar que el componente esté completamente montado
  await nextTick();

  // Verificar que tableRef esté disponible
  if (tableRef.value) {
    tableRef.value.requestServerInteraction();
  } else {
    console.error("Table ref not available");
  }
});
</script>
