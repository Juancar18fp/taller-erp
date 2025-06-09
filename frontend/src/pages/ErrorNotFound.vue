<template>
  <div class="error-page">
    <div class="error-container">
      <div class="form-section error-section">
        <div class="section-header">
          <q-icon name="error_outline" class="section-icon error-icon" />
          <h2 class="section-title">Página No Encontrada</h2>
        </div>
        <div class="inputs-container">
          <div class="error-content">
            <div class="error-number">404</div>
            <h3 class="error-title">¡Ups! Parece que te has perdido</h3>
            <p class="error-message">
              La página que estás buscando no existe o ha sido movida a otra ubicación.
            </p>
            <q-btn
              color="primary"
              icon="home"
              label="Ir al Inicio"
              @click="goHome"
              unelevated
              size="lg"
              class="home-btn"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

const goHome = () => {
  void router.push("/");
};
</script>

<style scoped>
.error-page {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  padding: 2rem;
}

.error-container {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 4rem);
}

.error-section {
  width: 100%;
  text-align: center;
}

.error-icon {
  color: #ef4444 !important;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.error-number {
  font-size: 8rem;
  font-weight: 900;
  color: #3b82f6;
  line-height: 1;
  text-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
  opacity: 0.8;
}

.error-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.error-message {
  font-size: 1.1rem;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
  max-width: 400px;
}

.home-btn {
  min-width: 160px;
  border-radius: 8px;
  font-weight: 600;
  margin-top: 1rem;
}
</style>
