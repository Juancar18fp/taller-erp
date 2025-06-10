import type { NavigationGuardNext, RouteLocationNormalized } from "vue-router";
import { useAuthStore } from "src/stores/auth";

export const authGuard = (
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
) => {
  const authStore = useAuthStore();

  if (!authStore.isAuthenticated) {
    next("/login");
    return;
  }
  const routePermissions = getRoutePermissions(to.path);

  if (routePermissions.length > 0 && !authStore.hasAnyPermission(routePermissions)) {
    next("/unauthorized");
    return;
  }

  next();
};

const getRoutePermissions = (path: string): string[] => {
  if (path.startsWith("/ordenes")) return ["ordenes"];
  if (path.startsWith("/clientes")) return ["clientes"];
  if (path.startsWith("/vehiculos")) return ["vehiculos"];
  if (path.startsWith("/articulos")) return ["articulos"];
  if (path.startsWith("/empleados")) return ["empleados"];
  if (path.startsWith("/configuracion")) return ["configuracion"];

  return [];
};
