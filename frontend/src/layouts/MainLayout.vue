<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-gradient-to-r from-blue-900 to-blue-800 text-white shadow-lg">
      <q-toolbar class="q-py-sm">
        <div class="row items-center full-width">
          <div class="col-auto flex items-center">
            <div class="flex items-center cursor-pointer" @click="goHome">
              <img
                src="../assets/logo.svg"
                style="width: 32px; height: 32px; object-fit: cover; border-radius: 6px"
                alt="Logo"
                class="q-mr-sm"
              />
              <div class="text-h6 font-weight-bold">TallerPro</div>
            </div>
          </div>

          <div class="col-grow flex">
            <q-tabs
              v-model="activeTab"
              active-color="white"
              indicator-color="amber-4"
              align="left"
              class="q-ml-lg"
            >
              <q-tab
                name="inicio"
                label="Dashboard"
                icon="dashboard"
                @click="navigateTo('/')"
                class="text-weight-medium"
              />
              <q-tab
                v-for="item in navigationItems"
                :key="item.route"
                :name="item.route"
                :label="item.name"
                :icon="item.icon"
                @click="navigateTo(item.route)"
                class="text-weight-medium"
              />
            </q-tabs>
          </div>

          <div class="col-auto flex items-center q-gutter-sm">
            <q-btn flat round class="q-ml-sm">
              <q-avatar size="32px" color="white" text-color="primary">
                <span>{{ userInitials }}</span>
              </q-avatar>

              <q-menu class="bg-white text-dark shadow-lg" style="min-width: 200px">
                <div class="q-pa-md border-bottom">
                  <div class="text-weight-bold">{{ userName }}</div>
                  <div class="text-caption text-grey">{{ userRole }}</div>
                </div>

                <q-list dense>
                  <q-item clickable v-close-popup @click="showSettings">
                    <q-item-section avatar>
                      <q-icon name="settings" />
                    </q-item-section>
                    <q-item-section>Configuración</q-item-section>
                  </q-item>

                  <q-separator />

                  <q-item clickable v-close-popup @click="logout">
                    <q-item-section avatar>
                      <q-icon name="logout" color="negative" />
                    </q-item-section>
                    <q-item-section>Cerrar Sesión</q-item-section>
                  </q-item>
                </q-list>
              </q-menu>
            </q-btn>
          </div>
        </div>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const activeTab = ref("inicio");

const userName = ref("Juan Pérez");
const userRole = ref("Administrador");
const userInitials = computed(() => {
  return userName.value
    .split(" ")
    .map((n) => n[0])
    .join("");
});

const navigationItems = [
  {
    route: "clientes",
    name: "Clientes",
    icon: "people",
  },
  {
    route: "vehiculos",
    name: "Vehículos",
    icon: "directions_car",
  },
  {
    route: "articulos",
    name: "Artículos",
    icon: "inventory",
  },
  {
    route: "ordenes",
    name: "Órdenes de trabajo",
    icon: "build",
  },
  {
    route: "empleados",
    name: "Empleados",
    icon: "badge",
  },
  {
    route: "configuracion",
    name: "Configuración",
    icon: "settings",
  },
];

const navigateTo = async (path: string) => {
  try {
    if (path.startsWith("/")) {
      await router.push(path);
      activeTab.value = "inicio";
    } else {
      await router.push({ name: path });
      activeTab.value = path;
    }
  } catch (error) {
    console.error("Error navegando:", error);
  }
};

const goHome = async () => {
  await navigateTo("/");
};

const showSettings = () => {};

const logout = () => {};

watch(
  () => route.name,
  (newRoute) => {
    if (newRoute) {
      const item = navigationItems.find((item) => item.route === newRoute);
      activeTab.value = item ? item.route : "inicio";
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.bg-gradient-to-r {
  background: linear-gradient(to right, #1e3a8a, #1e40af);
}

.border-bottom {
  border-bottom: 1px solid #e0e0e0;
}

.shadow-lg {
  box-shadow:
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05);
}
</style>
