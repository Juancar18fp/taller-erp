import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { LoginRequest, Usuario } from "src/types/auth";
import { authService } from "src/services/authService";

export const useAuthStore = defineStore("auth", () => {
  const token = ref<string | null>(localStorage.getItem("token"));
  const usuario = ref<Usuario | null>(null);
  const isLoading = ref(false);

  const isAuthenticated = computed(() => !!token.value && !!usuario.value);
  const userRole = computed(() => usuario.value?.rol?.nombre || "");
  const userRoleObject = computed(() => usuario.value?.rol || null);
  const userPermissions = computed(() => usuario.value?.permisos || []);

  const isAdmin = computed(() => usuario.value?.rol?.nombre === "ADMINISTRADOR");
  const isTecnico = computed(() => usuario.value?.rol?.nombre === "TECNICO");
  const isRecepcionista = computed(() => usuario.value?.rol?.nombre === "RECEPCIONISTA");
  const isAlmacen = computed(() => usuario.value?.rol?.nombre === "ALMACEN");

  const hasPermission = (permission: string): boolean => {
    return userPermissions.value.includes(permission);
  };

  const hasAnyPermission = (permissions: string[]): boolean => {
    return permissions.some((permission) => hasPermission(permission));
  };

  const isRole = (roleName: string): boolean => {
    return usuario.value?.rol?.nombre === roleName;
  };

  const initializeAuth = () => {
    if (token.value) {
      try {
        const userData = localStorage.getItem("usuario");
        if (userData) {
          usuario.value = JSON.parse(userData);
        }
      } catch (error) {
        console.error("Error al inicializar autenticación:", error);
        logout();
      }
    }
  };

  const login = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      isLoading.value = true;
      const response = await authService.login(credentials);

      if (!response) {
        throw new Error("No se recibió respuesta del servidor.");
      }
      token.value = response.token;
      usuario.value = {
        id: response.empleadoId,
        dni: response.dni,
        nombre: response.nombre,
        email: response.email,
        rol: response.rol,
        permisos: response.permisos,
      };

      localStorage.setItem("token", response.token);
      localStorage.setItem("usuario", JSON.stringify(usuario.value));

      return true;
    } catch (error) {
      console.error("Error en login:", error);
      return false;
    } finally {
      isLoading.value = false;
    }
  };

  const logout = () => {
    token.value = null;
    usuario.value = null;
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
  };

  return {
    token,
    usuario,
    isLoading,
    isAuthenticated,
    userRole,
    userRoleObject,
    userPermissions,
    isAdmin,
    isTecnico,
    isRecepcionista,
    isAlmacen,
    login,
    logout,
    initializeAuth,
    hasPermission,
    hasAnyPermission,
    isRole,
  };
});
