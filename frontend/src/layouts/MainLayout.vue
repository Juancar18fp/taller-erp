<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-tabs active-color="white" indicator-color="yellow-8">
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
