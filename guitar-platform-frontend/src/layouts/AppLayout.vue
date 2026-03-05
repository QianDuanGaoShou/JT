<template>
  <div class="app-layout">
    <AppNavbar />
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import AppNavbar from '@/components/AppNavbar.vue'
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #121212;
  display: flex;
  flex-direction: column;
}
.app-main {
  flex: 1;
  padding-top: 64px; /* navbar height */
}
</style>
