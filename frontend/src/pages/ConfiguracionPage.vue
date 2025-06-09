<template>
  <q-page padding>
    <div class="page-container">
      <div class="row q-gutter-lg">
        <div class="col-12 col-md-3">
          <q-card flat bordered class="navigation-card">
            <q-card-section class="q-pa-none">
              <q-list>
                <q-item-label header class="text-primary text-weight-bold q-px-md q-pt-md">
                  <q-icon name="tune" class="q-mr-xs" />
                  Configuración
                </q-item-label>

                <q-item
                  v-for="section in configSections"
                  :key="section.key"
                  :active="activeSection === section.key"
                  @click="activeSection = section.key"
                  clickable
                  v-ripple
                  :class="{
                    'bg-primary text-white': activeSection === section.key,
                    'text-primary': activeSection !== section.key,
                  }"
                  class="config-menu-item"
                >
                  <q-item-section avatar>
                    <q-icon
                      :name="section.icon"
                      :color="activeSection === section.key ? 'white' : 'primary'"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-weight-medium">
                      {{ section.label }}
                    </q-item-label>
                    <q-item-label
                      caption
                      :class="activeSection === section.key ? 'text-blue-2' : 'text-grey-6'"
                    >
                      {{ section.description }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-12 col-md-9">
          <div class="content-area">
            <div v-if="activeSection === 'empresa'" class="section-content">
              <EmpresaConfig />
            </div>

            <div v-else-if="activeSection === 'puestos'" class="section-content">
              <PuestosTable />
            </div>

            <div v-else-if="activeSection === 'import-export'" class="section-content">
              <CsvExportImport />
            </div>
          </div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from "vue";
import CsvExportImport from "src/components/CsvExportImport.vue";
import EmpresaConfig from "src/components/EmpresaConfig.vue";
import PuestosTable from "src/components/PuestosTable.vue";

const activeSection = ref("empresa");

const configSections = [
  {
    key: "empresa",
    label: "Datos de la Empresa",
    icon: "business",
    description: "Información y datos de contacto",
  },

  {
    key: "puestos",
    label: "Puestos de Trabajo",
    icon: "work",
    description: "Gestionar puestos de trabajo",
  },
  {
    key: "import-export",
    label: "Importar/Exportar",
    icon: "import_export",
    description: "Gestión de datos CSV",
  },
];
</script>

<style scoped>
.row.q-gutter-lg {
  display: flex !important;
  flex-wrap: nowrap !important;
}

.col-12.col-md-3 {
  flex: 0 0 300px !important;
  min-width: 300px;
}

.col-12.col-md-9 {
  flex: 1 !important;
  min-width: 0;
}

.content-area {
  min-height: 600px;
  width: 100%;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
}

.navigation-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.config-menu-item {
  margin: 0 8px 4px 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.config-menu-item:hover {
  background-color: rgba(25, 118, 210, 0.1);
}

.config-menu-item.bg-primary {
  margin: 0 8px 4px 8px;
  border-radius: 8px;
}

.content-area {
  min-height: 600px;
}

.section-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .row.q-gutter-lg {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 0.5rem;
  }
}
</style>
