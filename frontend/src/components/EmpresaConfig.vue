<template>
  <q-card flat bordered>
    <q-card-section>
      <div class="text-h6 text-primary q-mb-lg flex items-center">
        <q-icon name="business" class="q-mr-sm" />
        Datos de la Empresa
      </div>

      <q-form @submit="guardarConfiguracion" class="empresa-form">
        <div class="form-section">
          <div class="section-title">
            <q-icon name="info" class="q-mr-sm" />
            Información Básica
          </div>

          <div class="form-grid">
            <q-input
              v-model="config.nombre"
              label="Nombre de la Empresa *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />

            <q-input
              v-model="config.razonSocial"
              label="Razón Social"
              outlined
              dense
              class="form-field"
            />

            <q-input
              v-model="config.cif"
              label="CIF/NIF *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />

            <q-input
              v-model="config.numeroRegistro"
              label="Número de Registro Mercantil"
              outlined
              dense
              class="form-field"
            />
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">
            <q-icon name="location_on" class="q-mr-sm" />
            Dirección
          </div>

          <div class="form-grid">
            <q-input
              v-model="config.direccion"
              label="Dirección *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field full-width"
            />

            <q-input
              v-model="config.codigoPostal"
              label="Código Postal *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />

            <q-input
              v-model="config.poblacion"
              label="Población *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />

            <q-input
              v-model="config.provincia"
              label="Provincia *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />

            <q-select
              v-model="config.pais"
              :options="paises"
              label="País *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">
            <q-icon name="contact_phone" class="q-mr-sm" />
            Información de Contacto
          </div>

          <div class="form-grid">
            <q-input
              v-model="config.telefono"
              label="Teléfono Principal *"
              outlined
              dense
              :rules="[(val) => !!val || 'Campo obligatorio']"
              class="form-field"
            />

            <q-input
              v-model="config.telefonoSecundario"
              label="Teléfono Secundario"
              outlined
              dense
              class="form-field"
            />

            <q-input
              v-model="config.email"
              label="Email Principal *"
              type="email"
              outlined
              dense
              :rules="[
                (val) => !!val || 'Campo obligatorio',
                (val) => /.+@.+\..+/.test(val) || 'Email no válido',
              ]"
              class="form-field"
            />

            <q-input
              v-model="config.emailSecundario"
              label="Email Secundario"
              type="email"
              outlined
              dense
              :rules="[(val) => !val || /.+@.+\..+/.test(val) || 'Email no válido']"
              class="form-field"
            />

            <q-input
              v-model="config.sitioWeb"
              label="Sitio Web"
              outlined
              dense
              placeholder="https://www.ejemplo.com"
              class="form-field full-width"
            />
          </div>
        </div>

        <div class="action-buttons">
          <q-btn
            type="submit"
            color="primary"
            icon="save"
            label="Guardar Configuración"
            :loading="loading"
            size="md"
            unelevated
          />

          <q-btn
            color="grey-7"
            icon="refresh"
            label="Restablecer"
            outline
            @click="cargarConfiguracion"
            :disable="loading"
            size="md"
          />

          <q-btn
            color="secondary"
            icon="preview"
            label="Vista Previa"
            outline
            @click="mostrarVistaPrevia"
            size="md"
          />
        </div>
      </q-form>
    </q-card-section>

    <q-dialog v-model="showPreview" maximized>
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">Vista Previa - Datos en Facturas</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section>
          <div class="preview-container">
            <div class="invoice-header">
              <div class="company-info">
                <h2>{{ config.nombre || "Nombre de la Empresa" }}</h2>
                <p v-if="config.razonSocial">{{ config.razonSocial }}</p>
                <p>{{ config.cif || "CIF/NIF" }}</p>
                <div class="address">
                  <p>{{ config.direccion || "Dirección" }}</p>
                  <p>{{ config.codigoPostal || "CP" }} {{ config.poblacion || "Población" }}</p>
                  <p>{{ config.provincia || "Provincia" }}, {{ config.pais || "País" }}</p>
                </div>
                <div class="contact">
                  <p>Tel: {{ config.telefono || "Teléfono" }}</p>
                  <p v-if="config.email">Email: {{ config.email }}</p>
                  <p v-if="config.sitioWeb">Web: {{ config.sitioWeb }}</p>
                </div>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useQuasar } from "quasar";
import tallerApi from "../api/tallerApi";

const $q = useQuasar();
const loading = ref(false);
const showPreview = ref(false);

const config = ref({
  nombre: "",
  razonSocial: "",
  cif: "",
  numeroRegistro: "",
  direccion: "",
  codigoPostal: "",
  poblacion: "",
  provincia: "",
  pais: "España",
  telefono: "",
  telefonoSecundario: "",
  email: "",
  emailSecundario: "",
  sitioWeb: "",
});

const paises = ["España", "Francia", "Portugal", "Italia", "Alemania", "Reino Unido", "Otros"];

const cargarConfiguracion = async (): Promise<void> => {
  try {
    const response = await tallerApi.get("/configuracion/empresa");
    if (response.data) {
      config.value = { ...config.value, ...response.data };
    }
  } catch (error) {
    console.error("Error al cargar configuración:", error);
    $q.notify({
      type: "info",
      message: "Usando configuración por defecto",
      timeout: 2000,
    });
  }
};

const guardarConfiguracion = async (): Promise<void> => {
  loading.value = true;

  try {
    await tallerApi.put("/configuracion/empresa", config.value);

    $q.notify({
      type: "positive",
      message: "Configuración de empresa guardada exitosamente",
      icon: "check_circle",
    });
  } catch (error) {
    console.error("Error al guardar:", error);
    $q.notify({
      type: "negative",
      message: "Error al guardar la configuración de empresa",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

const mostrarVistaPrevia = (): void => {
  showPreview.value = true;
};

onMounted(() => {
  void cargarConfiguracion();
});
</script>

<style scoped>
.empresa-form {
  max-width: 100%;
}

.form-section {
  margin-bottom: 2.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 1.5rem;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 1.5rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.25rem;
  align-items: start;
}

.form-field {
  width: 100%;
}

.form-field.full-width {
  grid-column: 1 / -1;
}



.action-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e0e0e0;
}

.preview-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  border: 1px solid #ddd;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.invoice-header {
  text-align: left;
}

.company-info h2 {
  margin: 0 0 0.5rem 0;
  color: #1976d2;
  font-size: 1.8rem;
  font-weight: bold;
}

.company-info p {
  margin: 0.25rem 0;
  color: #555;
  line-height: 1.4;
}

.address {
  margin: 1rem 0;
}

.contact {
  margin-top: 1rem;
}

.contact p {
  color: #1976d2;
  font-weight: 500;
}
</style>
