<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar Admin bên trái -->
      <nav class="col-md-3 col-lg-2 d-md-block sidebar vh-100 p-3 sticky-top" style="background-color: #305a78;" >
        <img src="/src/img/logo2.jpg" height="85px" style="margin-left: 10px;">
        <ul class="nav flex-column gap-2">
          <li class="nav-item">
            <router-link to="/admin/pos" class="nav-link text-white small hover-link"><i class="fas fa-cash-register me-2"></i>Bán hàng</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/products" class="nav-link text-white small hover-link"><i class="fas fa-box me-2"></i>Sản phẩm</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/categories" class="nav-link text-white small hover-link"><i class="fas fa-list me-2"></i>Danh mục</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/attributes" class="nav-link text-white small hover-link"><i class="fas fa-tags me-2"></i>Thuộc tính</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/bills" class="nav-link text-white small hover-link"><i class="fas fa-file-invoice me-2"></i>Hóa đơn</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/vouchers" class="nav-link text-white small hover-link"><i class="fas fa-ticket-alt me-2"></i>Voucher</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/promotions" class="nav-link text-white small hover-link"><i class="fas fa-percentage me-2"></i>Khuyến mại</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/customers" class="nav-link text-white small hover-link"><i class="fas fa-users me-2"></i>Khách hàng</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/staff" class="nav-link text-white small hover-link"><i class="fas fa-user-shield me-2"></i>Nhân viên</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/revenue" class="nav-link text-white small hover-link"><i class="fas fa-chart-line me-2"></i>Doanh thu</router-link>
          </li>
          <hr class="text-secondary">
          <li class="nav-item">
            <router-link to="/" class="nav-link text-warning small"><i class="fas fa-sign-out-alt me-2"></i>Quay lại Web</router-link>
          </li>
        </ul>
      </nav>

      <!-- Nội dung chính bên phải -->
      <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
        <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
          <h1 class="h2">Hệ Thống Quản Trị BeeSport</h1>
        </div>
        
        <!-- Dòng này là QUAN TRỌNG NHẤT: Nó sẽ hiện các trang con như ProductMgmt, BillMgmt... -->
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.hover-link:hover {
  background-color: #495057;
  border-radius: 5px;
}
.nav-link.router-link-active {
  background-color: #dc3545;
  border-radius: 5px;
}

</style>