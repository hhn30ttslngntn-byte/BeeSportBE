import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // --- USER ---
    { path: '/', component: () => import('../views/user/HomeView.vue') },
    { path: '/about', component: () => import('../views/user/AboutView.vue') },
    { path: '/contact', component: () => import('../views/user/ContactView.vue') },
    { path: '/products', component: () => import('../views/user/ProductListView.vue') },
    { path: '/product/:id', component: () => import('../views/user/ProductDetailView.vue') },
    { path: '/cart', component: () => import('../views/user/CartView.vue') },
    { path: '/account', component: () => import('../views/user/AccountView.vue') },
    { path: '/orders', component: () => import('../views/user/OrderHistoryView.vue') },
    // --- AUTH ---
    { path: '/login', component: () => import('../views/auth/LoginView.vue') },
    { path: '/register', component: () => import('../views/auth/RegisterView.vue') },
    { path: '/forgot-password', component: () => import('../views/auth/ForgotPasswordView.vue') },
    // --- ADMIN ---
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      children: [
        { path: 'products', component: () => import('../views/admin/ProductMgmt.vue') },
        { path: 'products/add', component: () => import('../views/admin/ProductDetail.vue') },
        { path: 'products/edit/:id', component: () => import('../views/admin/ProductDetail.vue'), props: true },
        
        { path: 'bills', component: () => import('../views/admin/BillMgmt.vue') },
        
        { path: 'categories', component: () => import('../views/admin/CategoryMgmt.vue') },
        
        { path: 'customers', component: () => import('../views/admin/CustomerMgmt.vue') },
        
        { path: 'staff', component: () => import('../views/admin/StaffMgmt.vue') },
        { path: 'staff/add', component: () => import('../views/admin/StaffDetail.vue') },
        
        { path: 'staff/edit/:id', component: () => import('../views/admin/StaffDetail.vue'), props: true },

        { path: 'revenue', component: () => import('../views/admin/RevenueMgmt.vue') },
        { path: 'pos', component: () => import('../views/admin/POSView.vue') },
        
        { path: 'vouchers', component: () => import('../views/admin/VoucherMgmt.vue') },
        { path: 'vouchers/add', component: () => import('../views/admin/VoucherDetail.vue') },
        { path: 'vouchers/edit/:id', component: () => import('../views/admin/VoucherDetail.vue'), props: true },

        { path: 'promotions', component: () => import('../views/admin/PromotionMgmt.vue') },
        { path: 'promotions/add', component: () => import('../views/admin/PromotionDetail.vue') },
        { path: 'promotions/edit/:id', component: () => import('../views/admin/PromotionDetail.vue'), props: true },

        { path: 'attributes', component: () => import('../views/admin/AttributeMgmt.vue') },
      ]
    }
  ]
})

export default router