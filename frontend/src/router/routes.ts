import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "home",
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
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;
