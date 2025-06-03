import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "inicio",
    component: () => import("../layouts/MainLayout.vue"),
    children: [
      { path: "/clientes", name: "clientes", component: () => import("../pages/ClientesPage.vue") },
      {
        path: "/vehiculos",
        name: "vehiculos",
        component: () => import("../pages/VehiculosPage.vue"),
      },
      {
        path: "/articulos",
        name: "articulos",
        component: () => import("../pages/ArticulosPage.vue"),
      },
      {
        path: "/ordenes",
        name: "ordenes",
        component: () => import("../pages/OrdenesPage.vue"),
      },
      {
        path: "/empleados",
        name: "empleados",
        component: () => import("../pages/EmpleadosPage.vue"),
      },
      {
        path: "/configuracion",
        name: "configuracion",
        component: () => import("../pages/ConfiguracionPage.vue"),
      },
    ],
  },
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;
