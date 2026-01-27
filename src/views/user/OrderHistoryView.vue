<template>
  <div class="container py-5">
    <div class="row g-4">
      <!-- Sidebar (Kế thừa từ Account) -->
      <div class="col-lg-3">
        <div class="bg-white rounded-4 shadow-sm p-4 h-100">
          <div class="text-center mb-4 pb-4 border-bottom">
            <div class="avatar-placeholder bg-light rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
              <i class="fas fa-user fs-4 text-secondary"></i>
            </div>
            <h6 class="fw-bold mb-1 small">Nguyễn Văn A</h6>
          </div>
          
          <div class="nav flex-column nav-pills small">
            <router-link to="/account" class="nav-link text-dark text-start rounded-3 mb-2 px-3 py-2 fw-bold">
              <i class="far fa-user me-2"></i> Thông tin tài khoản
            </router-link>
            <button class="nav-link active border-0 text-start rounded-3 mb-2 px-3 py-2 fw-bold shadow-none">
              <i class="fas fa-history me-2"></i> Lịch sử đơn hàng
            </button>
            <button class="nav-link text-dark text-start rounded-3 mb-2 px-3 py-2 fw-bold">
              <i class="fas fa-map-marker-alt me-2"></i> Sổ địa chỉ
            </button>
            <hr>
            <button class="nav-link text-danger text-start rounded-3 px-3 py-2 fw-bold">
              <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất
            </button>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="col-lg-9">
        <div class="bg-white rounded-4 shadow-sm p-4 p-md-5">
          <h4 class="fw-bold mb-4">Lịch Sử Đơn Hàng</h4>

          <!-- Filter Tabs -->
          <div class="d-flex gap-3 mb-4 overflow-auto pb-2 border-bottom">
            <button class="btn btn-sm fw-bold border-0 text-danger border-bottom border-2 border-danger rounded-0 px-3">Tất cả</button>
            <button class="btn btn-sm fw-bold border-0 text-secondary rounded-0 px-3">Chờ xác nhận</button>
            <button class="btn btn-sm fw-bold border-0 text-secondary rounded-0 px-3">Đang giao</button>
            <button class="btn btn-sm fw-bold border-0 text-secondary rounded-0 px-3">Đã giao</button>
          </div>

          <!-- Order List -->
          <div class="order-list">
            <div v-for="i in 3" :key="i" class="order-item border rounded-4 p-3 mb-4 p-md-4">
              <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom flex-wrap gap-2">
                <span class="fw-bold small">Mã đơn: <span class="text-danger">#BS1234{{i}}</span></span>
                <span class="badge bg-light text-success border border-success small px-3 py-2">ĐÃ GIAO</span>
              </div>
              
              <div class="d-flex gap-3 mb-3">
                <img src="https://via.placeholder.com/80x100" class="rounded-3" alt="Product">
                <div class="flex-grow-1">
                  <h6 class="fw-bold mb-1 small">Áo Thun Running Bee Pro 2024</h6>
                  <p class="text-secondary mb-0 small">SIZE: XL | MÀU: ĐEN</p>
                  <p class="mb-0 fw-bold small">x1 <span class="ms-4">450.000đ</span></p>
                </div>
              </div>

              <div class="d-flex justify-content-between align-items-center pt-3 border-top flex-wrap gap-3">
                <p class="mb-0 small fw-bold">Tổng tiền: <span class="text-danger fs-5">450.000đ</span></p>
                <div class="d-flex gap-2">
                  <button class="btn btn-outline-dark btn-sm rounded-pill px-4 fw-bold">CHI TIẾT</button>
                  <button class="btn btn-dark btn-sm rounded-pill px-4 fw-bold">MUA LẠI</button>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State (Hidden for demo) -->
          <div v-if="false" class="text-center py-5">
            <i class="fas fa-box-open fs-1 text-light mb-3"></i>
            <p class="text-secondary">Bạn chưa có đơn hàng nào.</p>
            <router-link to="/products" class="btn btn-danger btn-sm rounded-pill px-4">MUA SẮM NGAY</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.order-item { transition: 0.3s; }
.order-item:hover { border-color: #000 !important; }
.nav-pills .nav-link.active {
  background-color: #000;
  color: #fff !important;
}
</style>
