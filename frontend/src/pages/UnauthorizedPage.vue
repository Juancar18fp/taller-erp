<template>
  <div class="restricted-page">
    <div class="restricted-container">
      <div class="form-section error-section">
        <div class="section-header">
          <q-icon name="block" class="section-icon error-icon" />
          <h2 class="section-title">Acceso Restringido</h2>
        </div>
        <div class="inputs-container">
          <div class="error-content">
            <q-icon name="security" size="4rem" color="warning" class="main-icon" />
            <h3 class="error-title">Sin Permisos Suficientes</h3>
            <p class="error-message">
              No tienes autorización para acceder a esta sección del sistema. Contacta con tu
              administrador si necesitas acceso.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.restricted-page {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  padding: 2rem;
}

.restricted-container {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 4rem);
}

.error-section {
  width: 100%;
  max-width: 600px;
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
</style>
