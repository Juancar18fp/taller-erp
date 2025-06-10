import { boot } from "quasar/wrappers";
import { useAuthStore } from "src/stores/auth";

export default boot(({ router }) => {
  const authStore = useAuthStore();

  authStore.initializeAuth();

  router.beforeEach((to, from, next) => {
    const publicRoutes = ["/login", "/unauthorized"];

    if (publicRoutes.includes(to.path)) {
      next();
      return;
    }

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
  });
});

function getRoutePermissions(path: string): string[] {
  if (path.startsWith("/ordenes")) return ["ordenes"];
  if (path.startsWith("/clientes")) return ["clientes"];
  if (path.startsWith("/vehiculos")) return ["vehiculos"];
  if (path.startsWith("/articulos")) return ["articulos"];
  if (path.startsWith("/empleados")) return ["empleados"];
  if (path.startsWith("/configuracion")) return ["configuracion"];

  return [];
}
