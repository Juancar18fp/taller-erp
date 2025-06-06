<template>
  <div class="login-container">
    <div class="bg-pattern"></div>

    <div class="login-content">
      <div class="login-brand">
        <div class="brand-content">
          <div class="logo-container">
            <i class=""><img src="../assets/logo.svg" alt="" width="100px" /></i>
            <h1 class="brand-title">TallerPro</h1>
          </div>
          <p class="brand-subtitle">Sistema de Gestión de Talleres</p>
          <div class="features">
            <div class="feature">
              <i class="fas fa-car"></i>
              <span>Gestión de Vehículos</span>
            </div>
            <div class="feature">
              <i class="fas fa-users"></i>
              <span>Control de Clientes</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-form-section">
        <div class="form-container">
          <div class="form-header">
            <h2 class="form-title">Acceso al Sistema</h2>
            <p class="form-subtitle">Ingresa tus credenciales corporativas</p>
          </div>

          <form @submit.prevent="handleLogin" class="login-form">
            <div class="input-group">
              <label for="dni" class="input-label">
                <i class="fas fa-id-badge"></i>
                Documento de Identidad
              </label>
              <div class="input-wrapper">
                <input
                  id="dni"
                  v-model="loginForm.dni"
                  type="text"
                  required
                  class="form-input"
                  placeholder="Ej: 12345678A"
                  :class="{ error: errorMessage && !loginForm.dni }"
                />
                <div class="input-icon">
                  <i class="fas fa-user"></i>
                </div>
              </div>
            </div>

            <div class="input-group">
              <label for="password" class="input-label">
                <i class="fas fa-lock"></i>
                Contraseña
              </label>
              <div class="input-wrapper">
                <input
                  id="password"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  class="form-input"
                  placeholder="Ingresa tu contraseña"
                  :class="{ error: errorMessage && !loginForm.password }"
                />
                <button type="button" @click="showPassword = !showPassword" class="password-toggle">
                  <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
            </div>

            <div v-if="errorMessage" class="error-message">
              <i class="fas fa-exclamation-triangle"></i>
              {{ errorMessage }}
            </div>

            <button
              type="submit"
              :disabled="isLoading"
              class="submit-button"
              :class="{ loading: isLoading }"
            >
              <span v-if="!isLoading" class="button-content">
                <i class="fas fa-sign-in-alt"></i>
                Iniciar Sesión
              </span>
              <span v-else class="button-content loading">
                <i class="fas fa-spinner fa-spin"></i>
                Iniciando sesión...
              </span>
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "src/stores/auth";
import type { LoginRequest } from "src/types/auth";

const router = useRouter();
const authStore = useAuthStore();

const loginForm = ref<LoginRequest>({
  dni: "",
  password: "",
});

const showPassword = ref(false);
const errorMessage = ref("");
const isLoading = ref(false);

const handleLogin = async () => {
  try {
    isLoading.value = true;
    errorMessage.value = "";

    const success = await authStore.login(loginForm.value);

    if (success) {
      await router.push("/");
    } else {
      errorMessage.value = "DNI o contraseña incorrectos";
    }
  } catch (error) {
    console.error("Error en login:", error);
    errorMessage.value = "Error al iniciar sesión. Inténtalo de nuevo.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* ===== BASE STYLES ===== */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
  padding: 1rem;
  overflow: hidden;
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(circle at 20% 80%, rgba(37, 99, 235, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(59, 130, 246, 0.15) 0%, transparent 50%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(1deg);
  }
}

.login-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1200px;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow:
    0 32px 64px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  overflow: hidden;
  min-height: 600px;
}

.login-brand {
  background: linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 100%);
  color: white;
  padding: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.login-brand::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.3;
}

.brand-content {
  text-align: center;
  z-index: 1;
  position: relative;
}

.logo-container {
  margin-bottom: 2rem;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  font-size: 2rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.brand-title {
  font-size: 2.75rem;
  font-weight: 700;
  margin: 0;
  color: white;
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 1rem;
  opacity: 0.85;
  margin: 0.5rem 0 2.5rem;
  font-weight: 400;
  color: #dbeafe;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feature {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.95rem;
  opacity: 0.9;
}

.feature i {
  width: 20px;
  text-align: center;
}

.login-form-section {
  padding: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-container {
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.company-logo {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  font-size: 1.5rem;
  color: white;
  box-shadow: 0 8px 32px rgba(37, 99, 235, 0.3);
}

.form-title {
  font-size: 1.875rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.5rem;
  letter-spacing: -0.01em;
}

.form-subtitle {
  color: #6b7280;
  margin: 0;
  font-size: 0.9rem;
  font-weight: 400;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.input-label i {
  color: #6b7280;
  width: 16px;
}

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem;
  padding-right: 3rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.2s ease;
  background: #fafafa;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  background: white;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-input.error {
  border-color: #ef4444;
  background: #fef2f2;
}

.input-icon,
.password-toggle {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.password-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: #2563eb;
  background: rgba(37, 99, 235, 0.1);
}

.error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #dc2626;
  font-size: 0.875rem;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

.submit-button {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(37, 99, 235, 0.3);
}

.submit-button:active:not(:disabled) {
  transform: translateY(0);
}

.submit-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.button-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.button-content.loading {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

/* ===== RESPONSIVE DESIGN ===== */
@media (max-width: 768px) {
  .login-content {
    grid-template-columns: 1fr;
    max-width: 480px;
  }

  .login-brand {
    padding: 2rem;
    min-height: auto;
  }

  .brand-title {
    font-size: 2rem;
  }

  .features {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    gap: 1.5rem;
  }

  .feature {
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
    font-size: 0.875rem;
  }

  .login-form-section {
    padding: 2rem;
  }

  .form-title {
    font-size: 1.75rem;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 0.5rem;
  }

  .login-content {
    border-radius: 16px;
  }

  .login-brand {
    padding: 1.5rem;
  }

  .brand-title {
    font-size: 1.75rem;
  }

  .login-form-section {
    padding: 1.5rem;
  }

  .form-title {
    font-size: 1.5rem;
  }

  .features {
    gap: 1rem;
  }

  .feature {
    font-size: 0.8rem;
  }
}

/* ===== ACCESSIBILITY ===== */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* ===== DARK MODE SUPPORT ===== */
@media (prefers-color-scheme: dark) {
  .login-content {
    background: rgba(31, 41, 55, 0.95);
    color: #f9fafb;
  }

  .form-title {
    color: #f9fafb;
  }

  .form-subtitle {
    color: #d1d5db;
  }

  .input-label {
    color: #e5e7eb;
  }

  .form-input {
    background: #374151;
    border-color: #4b5563;
    color: #f9fafb;
  }

  .form-input:focus {
    background: #1f2937;
    border-color: #6366f1;
  }

  .test-credentials {
    background: #374151;
    border-color: #4b5563;
  }

  .credential-item .label {
    color: #d1d5db;
  }

  .credential-item code {
    background: #4b5563;
    color: #f3f4f6;
  }
}
</style>
