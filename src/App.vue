<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'

const route = useRoute()
const isAdminPage = computed(() => route.path.startsWith('/admin'))
</script>

<template>
  <div class="d-flex flex-column min-vh-100">
    <!-- Header luôn ở trên (ẩn nếu là trang admin) -->
    <AppHeader v-if="!isAdminPage" />
    
    <!-- Nội dung thay đổi theo trang -->
    <main class="flex-grow-1">
      <RouterView />
    </main>

    <!-- Footer luôn ở dưới (ẩn nếu là trang admin) -->
    <AppFooter v-if="!isAdminPage" />
  </div>
</template>

<style>
body { 
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
}

/* Utility classes for responsive spacing */
@media (max-width: 768px) {
  .container { padding-left: 15px; padding-right: 15px; }
  .py-5 { padding-top: 2rem !important; padding-bottom: 2rem !important; }
  h2 { font-size: 1.5rem; }
  .display-3 { font-size: 2.5rem; }
}

/* Global scrollbar for dropdowns/filters on mobile */
.overflow-auto::-webkit-scrollbar {
  height: 0px;
}

/* Custom transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>