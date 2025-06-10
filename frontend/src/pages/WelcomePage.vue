<template>
  <q-page class="welcome-page">
    <div class="welcome-container">
      <div class="welcome-greeting">
        <h2 class="greeting-title">¡Bienvenido, {{ userName }}!</h2>
      </div>

      <div class="main-content" v-if="companyInfo">
        <q-card class="info-card" flat bordered>
          <q-card-section class="text-center q-pa-md">
            <q-icon name="business" size="lg" color="primary" class="q-mb-sm" />
            <h3 class="company-name">{{ companyInfo.nombre }}</h3>
            <div class="company-contact">
              <span v-if="companyInfo.telefono" class="contact-item">
                <q-icon name="phone" size="sm" />
                {{ companyInfo.telefono }}
              </span>
              <span v-if="companyInfo.email" class="contact-item">
                <q-icon name="email" size="sm" />
                {{ companyInfo.email }}
              </span>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "src/stores/auth";
import tallerApi from "src/api/tallerApi";

interface DatosEmpresa {
  id?: number;
  nombre: string;
  razonSocial?: string;
  cif: string;
  numeroRegistro?: string;
  direccion: string;
  codigoPostal: string;
  poblacion: string;
  provincia: string;
  pais: string;
  telefono: string;
  telefonoSecundario?: string;
  email: string;
  emailSecundario?: string;
  sitioWeb?: string;
}

const authStore = useAuthStore();

const currentDateTime = ref("");
const companyInfo = ref<DatosEmpresa | null>(null);

const userName = computed(() => authStore.usuario?.nombre || "Usuario");

const updateDateTime = () => {
  const now = new Date();
  currentDateTime.value = now.toLocaleString("es-ES", {
    weekday: "long",
    day: "numeric",
    month: "long",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const loadCompanyInfo = async () => {
  try {
    const response = await tallerApi.get("/configuracion/empresa");
    if (response.data) {
      companyInfo.value = response.data;
    }
  } catch {
    companyInfo.value = null;
  }
};

onMounted(async () => {
  updateDateTime();
  setInterval(updateDateTime, 60000);
  await loadCompanyInfo();
});
</script>

<style scoped>
.welcome-page {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow: hidden;
}

.welcome-container {
  max-width: 600px;
  width: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 24px;
}

.welcome-greeting {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.greeting-title {
  font-size: 2.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.main-content {
  display: flex;
  justify-content: center;
}

.info-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  background: white;
  border: 1px solid #ecf0f1;
  max-width: 450px;
}

.company-name {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 8px 0 12px 0;
}

.company-contact {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.95rem;
  color: #7f8c8d;
}
</style>
