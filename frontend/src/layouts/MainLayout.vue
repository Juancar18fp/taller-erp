<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-dark text-white">
      <q-toolbar>
        <q-tabs active-color="white" indicator-color="primary">
          <q-route-tab exact>
            <img
              src="../assets/logo.svg"
              style="width: 24px; height: 24px; object-fit: cover; border-radius: 4px"
            /><q-menu class="bg-white text-black"
              ><q-item clickable v-close-popup>
                <q-item-section>Configuración</q-item-section>
              </q-item>
            </q-menu>
          </q-route-tab>
          <q-route-tab to="/" label="Inicio" exact class="q-mr-md" />
          <q-route-tab
            v-for="item in items"
            :key="item.route"
            :to="{ name: item.route }"
            :label="item.name"
            exact
          />
        </q-tabs>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
const items = [
  {
    route: "clientes",
    name: "Clientes",
  },
  {
    route: "vehiculos",
    name: "Vehículos",
  },
  {
    route: "articulos",
    name: "Artículos",
  },
  {
    route: "ordenes",
    name: "Órdenes de trabajo",
  },
];
</script>
