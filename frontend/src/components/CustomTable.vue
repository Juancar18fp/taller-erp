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
      v-model="createDialogVisible"
      @created="refreshTable"
      :route="customProps.route"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { QTable, useQuasar } from "quasar";
import type { QTableProps } from "quasar";
import type { ApiResponse, BaseEntity, CustomTableProps } from "../interfaces/index";
import tallerApi from "../api/tallerApi";
import CreateCustomDialog from "./CreateCustomDialog.vue";

const customProps = defineProps<CustomTableProps>();

const $q = useQuasar();
const tableRef = ref<QTable>();
const rows = ref<BaseEntity[]>([]);
const filter = ref("");
const loading = ref(false);
const createDialogVisible = ref(false);

const buttonLabel = computed(() =>
  customProps.route.toLowerCase().includes("clientes") ? "Nuevo Cliente" : "Nuevo Vehículo",
);

const showCreateDialog = () => {
  createDialogVisible.value = true;
};

const refreshTable = () => {
  tableRef.value?.requestServerInteraction();
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

    rows.value = data.items;
    pagination.value = {
      ...props.pagination,
      rowsNumber: data.total,
    };
  } catch {
    $q.notify({
      type: "negative",
      message: "Error cargando los datos",
    });
  } finally {
    loading.value = false;
  }
};

const onRowClick = (_evt: Event, row: BaseEntity) => {
  console.log("Editar cliente:", row.id);
};

onMounted(() => {
  tableRef.value?.requestServerInteraction();
});
</script>
