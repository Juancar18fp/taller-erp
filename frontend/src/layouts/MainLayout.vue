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
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "src/stores/auth";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const activeTab = ref("inicio");

const userName = computed(() => authStore.usuario?.nombre || "Usuario");
const userRole = computed(() => {
  const roleNames = {
    ADMINISTRADOR: "Administrador",
    TECNICO: "Técnico",
    RECEPCIONISTA: "Recepcionista",
    ALMACEN: "Almacén",
  };
  return roleNames[authStore.userRole as keyof typeof roleNames] || authStore.userRole;
});

const userInitials = computed(() => {
  const names = userName.value.trim().split(" ");
  if (names.length === 0) return "";

  const firstInitial = names[0]?.[0] || "";
  const secondInitial = names[1]?.[0] || "";

  return (firstInitial + secondInitial).toUpperCase();
});

const allNavigationItems = [
  {
    route: "clientes",
    name: "Clientes",
    icon: "people",
    permission: "clientes",
  },
  {
    route: "vehiculos",
    name: "Vehículos",
    icon: "directions_car",
    permission: "vehiculos",
  },
  {
    route: "articulos",
    name: "Artículos",
    icon: "inventory",
    permission: "articulos",
  },
  {
    route: "ordenes",
    name: "Órdenes de trabajo",
    icon: "build",
    permission: "ordenes",
  },
  {
    route: "empleados",
    name: "Empleados",
    icon: "badge",
    permission: "empleados",
  },
  {
    route: "configuracion",
    name: "Configuración",
    icon: "settings",
    permission: "configuracion",
  },
];

const navigationItems = computed(() => {
  return allNavigationItems.filter((item) => authStore.hasPermission(item.permission));
});

const navigateTo = async (path: string) => {
  try {
    if (path.startsWith("/")) {
      await router.push(path);
      activeTab.value = "inicio";
    } else {
      const item = allNavigationItems.find((item) => item.route === path);
      if (item && !authStore.hasPermission(item.permission)) {
        console.warn("No tienes permisos para acceder a esta sección");
        return;
      }

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

const logout = async () => {
  try {
    authStore.logout();
    await router.push("/login");
  } catch (error) {
    console.error("Error al cerrar sesión:", error);
  }
};

onMounted(() => {
  authStore.initializeAuth();
  if (!authStore.isAuthenticated) {
    void router.push("/login");
  }
});

watch(
  () => route.name,
  (newRoute) => {
    if (newRoute) {
      const item = navigationItems.value.find((item) => item.route === newRoute);
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
