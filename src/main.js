import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router' // Dòng này sẽ tìm đến src/router/index.js

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')