import { computed } from "vue";
import { useAuthStore } from "src/stores/auth";

export const usePermissions = () => {
  const authStore = useAuthStore();

  const hasPermission = (permission: string) => {
    return computed(() => authStore.hasPermission(permission));
  };

  const hasAnyPermission = (permissions: string[]) => {
    return computed(() => authStore.hasAnyPermission(permissions));
  };

  const isAdmin = computed(() => authStore.isAdmin);
  const isTecnico = computed(() => authStore.isTecnico);
  const isRecepcionista = computed(() => authStore.isRecepcionista);
  const isAlmacen = computed(() => authStore.isAlmacen);

  const hasRole = (roleName: string) => {
    return computed(() => authStore.isRole(roleName));
  };

  const hasAnyRole = (roleNames: string[]) => {
    return computed(() => roleNames.some((roleName) => authStore.isRole(roleName)));
  };

  const getCurrentRole = computed(() => ({
    id: authStore.userRoleObject?.id,
    nombre: authStore.userRoleObject?.nombre,
    descripcion: authStore.userRoleObject?.descripcion,
    esAdmin: authStore.isAdmin,
    esTecnico: authStore.isTecnico,
    esRecepcionista: authStore.isRecepcionista,
    esAlmacen: authStore.isAlmacen,
  }));

  return {
    hasPermission,
    hasAnyPermission,
    isAdmin,
    isTecnico,
    isRecepcionista,
    isAlmacen,
    hasRole,
    hasAnyRole,
    getCurrentRole,
  };
};
