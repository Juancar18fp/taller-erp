import type { RouteRecordRaw } from "vue-router";
import { authGuard } from "./guards";

const routes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "LoginPage",
    component: () => import("pages/LoginPage.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/unauthorized",
    name: "UnauthorizedPage",
    component: () => import("pages/UnauthorizedPage.vue"),
    meta: { requiresAuth: false },
  },

  {
    path: "/",
    component: () => import("layouts/MainLayout.vue"),
    meta: { requiresAuth: true },
    beforeEnter: authGuard,
    children: [
      {
        path: "",
        name: "dashboard",
        component: () => import("pages/WelcomePage.vue"),
      },
      {
        path: "ordenes",
        name: "ordenes",
        component: () => import("pages/OrdenesPage.vue"),
      },
      {
        path: "clientes",
        name: "clientes",
        component: () => import("pages/ClientesPage.vue"),
      },
      {
        path: "vehiculos",
        name: "vehiculos",
        component: () => import("pages/VehiculosPage.vue"),
      },
      {
        path: "articulos",
        name: "articulos",
        component: () => import("pages/ArticulosPage.vue"),
      },
      {
        path: "empleados",
        name: "empleados",
        component: () => import("pages/EmpleadosPage.vue"),
      },
      {
        path: "configuracion",
        name: "configuracion",
        component: () => import("pages/ConfiguracionPage.vue"),
      },
    ],
  },

  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;
