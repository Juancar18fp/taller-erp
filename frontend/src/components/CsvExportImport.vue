<template>
  <div class="csv-container">
    <q-card class="q-mb-lg" flat bordered>
      <q-card-section>
        <div class="text-h6 text-primary q-mb-md flex items-center">
          <q-icon name="file_download" class="q-mr-sm" />
          Exportar Datos
        </div>

        <div class="row q-gutter-md">
          <div class="col-12 col-md-6">
            <q-select
              v-model="exportEntity"
              :options="entityOptions"
              label="Selecciona entidad"
              outlined
              dense
              option-value="value"
              option-label="label"
              emit-value
              map-options
            />
          </div>
        </div>

        <div class="q-mt-md">
          <q-btn
            color="primary"
            icon="file_download"
            label="Exportar CSV"
            @click="exportToCSV"
            :loading="exportLoading"
            :disable="!exportEntity"
            class="q-mr-sm"
          />
          <q-btn
            color="secondary"
            icon="description"
            label="Descargar Plantilla"
            @click="downloadTemplate"
            :disable="!exportEntity"
            outline
          />
        </div>
      </q-card-section>
    </q-card>

    <q-card flat bordered>
      <q-card-section>
        <div class="text-h6 text-primary q-mb-md flex items-center">
          <q-icon name="file_upload" class="q-mr-sm" />
          Importar Datos
        </div>

        <div class="row q-gutter-md q-mb-md">
          <div class="col-12 col-md-6">
            <q-select
              v-model="importEntity"
              :options="entityOptions"
              label="Selecciona entidad"
              outlined
              dense
              option-value="value"
              option-label="label"
              emit-value
              map-options
            />
          </div>
        </div>

        <div
          class="file-drop-zone"
          :class="{ 'drag-over': isDragOver }"
          @dragover="handleDragOver"
          @dragleave="handleDragLeave"
          @drop="handleDrop"
          @click="triggerFileInput"
        >
          <q-icon name="cloud_upload" size="48px" color="grey-5" class="q-mb-sm" />
          <div class="text-subtitle1 text-grey-7">
            <strong>Arrastra tu archivo CSV aquí</strong>
          </div>
          <div class="text-body2 text-grey-5 q-mb-md">o haz clic para seleccionar</div>
          <q-btn
            color="secondary"
            icon="attach_file"
            label="Seleccionar Archivo"
            outline
            @click.stop="triggerFileInput"
          />
        </div>

        <input
          ref="fileInput"
          type="file"
          accept=".csv"
          style="display: none"
          @change="handleFileSelect"
        />

        <div v-if="csvPreview.length > 0" class="q-mt-md">
          <q-separator class="q-mb-md" />
          <div class="text-subtitle1 text-grey-8 q-mb-sm">Vista previa del archivo:</div>

          <div class="preview-info q-mb-md">
            <div class="text-body2"><strong>Archivo:</strong> {{ selectedFileName }}</div>
            <div class="text-body2"><strong>Registros:</strong> {{ csvPreview.length }}</div>
            <div class="text-body2"><strong>Campos:</strong> {{ previewFields.join(", ") }}</div>
          </div>

          <q-table
            :rows="csvPreview.slice(0, 5)"
            :columns="previewColumns"
            dense
            flat
            bordered
            :pagination="{ rowsPerPage: 5 }"
            hide-pagination
          >
            <template v-slot:bottom v-if="csvPreview.length > 5">
              <div class="full-width text-center text-grey-6 q-pa-sm">
                ... y {{ csvPreview.length - 5 }} registros más
              </div>
            </template>
          </q-table>
        </div>

        <q-linear-progress
          v-if="importProgress.show"
          :value="importProgress.value"
          color="primary"
          class="q-mt-md"
        />

        <div class="q-mt-md">
          <q-btn
            color="positive"
            icon="file_upload"
            label="Importar Datos"
            @click="importCSV"
            :loading="importLoading"
            :disable="csvPreview.length === 0 || !importEntity"
          />
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useQuasar } from "quasar";
import Papa from "papaparse";
import tallerApi from "../api/tallerApi";

interface CsvRecord {
  [key: string]: string | number | null | undefined;
}

interface ImportProgress {
  show: boolean;
  value: number;
}

interface EntityOption {
  label: string;
  value: string;
}

interface PreviewColumn {
  name: string;
  label: string;
  field: string;
  align: "left" | "right" | "center";
  sortable: boolean;
}

const $q = useQuasar();

const exportEntity = ref<string>("");
const importEntity = ref<string>("");
const exportLoading = ref<boolean>(false);
const importLoading = ref<boolean>(false);
const isDragOver = ref<boolean>(false);
const csvPreview = ref<CsvRecord[]>([]);
const selectedFileName = ref<string>("");
const previewFields = ref<string[]>([]);
const fileInput = ref<HTMLInputElement>();

const importProgress = ref<ImportProgress>({
  show: false,
  value: 0,
});

const entityOptions: EntityOption[] = [
  { label: "Clientes", value: "clientes" },
  { label: "Empleados", value: "empleados" },
  { label: "Vehículos", value: "vehiculos" },
  { label: "Artículos", value: "articulos" },
  { label: "Órdenes de Trabajo", value: "ordenes" },
  { label: "Marcas", value: "marcas" },
  { label: "Modelos", value: "modelos" },
  { label: "Contratos", value: "contratos" },
  { label: "Puestos", value: "puestos" },
];

const previewColumns = computed<PreviewColumn[]>(() => {
  return previewFields.value.map((field) => ({
    name: field,
    label: field,
    field: field,
    align: "left" as const,
    sortable: false,
  }));
});

const handleDragOver = (e: DragEvent): void => {
  e.preventDefault();
  isDragOver.value = true;
};

const handleDragLeave = (): void => {
  isDragOver.value = false;
};

const handleDrop = (e: DragEvent): void => {
  e.preventDefault();
  isDragOver.value = false;

  const files = e.dataTransfer?.files;
  if (files && files.length > 0 && files[0]) {
    processFile(files[0]);
  }
};

const triggerFileInput = (): void => {
  fileInput.value?.click();
};

const handleFileSelect = (e: Event): void => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files.length > 0 && target.files[0]) {
    processFile(target.files[0]);
  }
};

const processFile = (file: File): void => {
  if (!file.name.toLowerCase().endsWith(".csv")) {
    $q.notify({
      type: "negative",
      message: "Por favor selecciona un archivo CSV válido",
    });
    return;
  }

  selectedFileName.value = file.name;

  const reader = new FileReader();
  reader.onload = (e): void => {
    try {
      const csv = e.target?.result as string;
      const parsed = Papa.parse<CsvRecord>(csv, {
        header: true,
        skipEmptyLines: true,
        dynamicTyping: true,
      });

      if (parsed.errors.length > 0 && parsed.errors[0]) {
        $q.notify({
          type: "negative",
          message: `Error al procesar el archivo: ${parsed.errors[0].message}`,
        });
        return;
      }

      csvPreview.value = parsed.data;
      previewFields.value = parsed.meta.fields || [];

      $q.notify({
        type: "positive",
        message: `Archivo cargado: ${parsed.data.length} registros encontrados`,
      });
    } catch (error) {
      $q.notify({
        type: "negative",
        message: `Error al leer el archivo: ${String(error)}`,
      });
    }
  };

  reader.readAsText(file);
};

const exportToCSV = (): void => {
  if (!exportEntity.value) return;

  exportLoading.value = true;

  try {
    const token = localStorage.getItem("token");

    if (!token) {
      $q.notify({
        type: "negative",
        message: "Sesión expirada. Por favor, inicia sesión nuevamente.",
      });
      exportLoading.value = false;
      return;
    }

    const baseURL = tallerApi.defaults.baseURL || "http://localhost:8080";
    const downloadUrl = `${baseURL}/csv/${exportEntity.value}/export?token=${encodeURIComponent(token)}`;

    const link = document.createElement("a");
    link.href = downloadUrl;
    link.download = `${exportEntity.value}_${new Date().toISOString().split("T")[0]}.csv`;
    link.style.display = "none";

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    $q.notify({
      type: "positive",
      message: "Descarga iniciada exitosamente",
    });
  } catch (error) {
    console.error("Error al exportar:", error);
    $q.notify({
      type: "negative",
      message: "Error al exportar los datos",
    });
  } finally {
    exportLoading.value = false;
  }
};

const downloadTemplate = (): void => {
  if (!exportEntity.value) return;

  try {
    const token = localStorage.getItem("token");

    if (!token) {
      $q.notify({
        type: "negative",
        message: "Sesión expirada. Por favor, inicia sesión nuevamente.",
      });
      return;
    }

    const baseURL = tallerApi.defaults.baseURL || "http://localhost:8080";
    const downloadUrl = `${baseURL}/csv/${exportEntity.value}/template?token=${encodeURIComponent(token)}`;

    const link = document.createElement("a");
    link.href = downloadUrl;
    link.download = `plantilla_${exportEntity.value}.csv`;
    link.style.display = "none";

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    $q.notify({
      type: "positive",
      message: "Plantilla descargada exitosamente",
    });
  } catch (error) {
    console.error("Error al descargar plantilla:", error);
    $q.notify({
      type: "negative",
      message: "Error al descargar la plantilla",
    });
  }
};

const importCSV = async (): Promise<void> => {
  if (!importEntity.value || csvPreview.value.length === 0) return;

  importLoading.value = true;
  importProgress.value.show = true;
  importProgress.value.value = 0;

  try {
    const progressInterval = setInterval(() => {
      if (importProgress.value.value < 0.9) {
        importProgress.value.value += 0.1;
      }
    }, 200);

    await tallerApi.post(`/csv/${importEntity.value}/import`, {
      data: csvPreview.value,
    });

    clearInterval(progressInterval);
    importProgress.value.value = 1;

    setTimeout(() => {
      importProgress.value.show = false;
      importProgress.value.value = 0;
    }, 1000);

    $q.notify({
      type: "positive",
      message: `Se importaron ${csvPreview.value.length} registros exitosamente`,
    });

    resetImportForm();
  } catch (error) {
    importProgress.value.show = false;
    importProgress.value.value = 0;

    console.error("Error al importar:", error);
    $q.notify({
      type: "negative",
      message: "Error al importar los datos",
    });
  } finally {
    importLoading.value = false;
  }
};

const resetImportForm = (): void => {
  csvPreview.value = [];
  selectedFileName.value = "";
  previewFields.value = [];
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};
</script>

<style scoped>
.csv-container {
  max-width: 1200px;
  margin: 0 auto;
}

.file-drop-zone {
  border: 2px dashed #ccc;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.file-drop-zone:hover,
.file-drop-zone.drag-over {
  border-color: #1976d2;
  background: #f0f8ff;
}

.preview-info {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 8px;
}
</style>
