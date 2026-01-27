<template>
  <div class="container py-5">
    <div class="row g-4">
      <!-- Sidebar -->
      <div class="col-lg-3">
        <div class="bg-white rounded-4 shadow-sm p-4 h-100">
          <div class="text-center mb-4 pb-4 border-bottom">
            <div class="avatar-placeholder bg-light rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center" style="width: 80px; height: 80px;">
              <i class="fas fa-user fs-2 text-secondary"></i>
            </div>
            <h6 class="fw-bold mb-1">Nguyễn Văn A</h6>
            <p class="text-secondary small mb-0">nguyenvana@gmail.com</p>
          </div>
          
          <div class="nav flex-column nav-pills small">
            <button class="nav-link active border-0 text-start rounded-3 mb-2 px-3 py-2 fw-bold shadow-none">
              <i class="far fa-user me-2"></i> Thông tin tài khoản
            </button>
            <router-link to="/orders" class="nav-link text-dark text-start rounded-3 mb-2 px-3 py-2 fw-bold">
              <i class="fas fa-history me-2"></i> Lịch sử đơn hàng
            </router-link>
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

      <!-- Content -->
      <div class="col-lg-9">
        <div class="bg-white rounded-4 shadow-sm p-5 h-100">
          <h4 class="fw-bold mb-4">Thông Tin Tài Khoản</h4>
          
          <form class="row g-4">
            <div class="col-md-6">
              <label class="form-label small fw-bold text-secondary text-uppercase">Họ và tên</label>
              <input type="text" class="form-control border-0 bg-light rounded-3 px-3 py-2" value="Nguyễn Văn A">
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-bold text-secondary text-uppercase">Số điện thoại</label>
              <input type="tel" class="form-control border-0 bg-light rounded-3 px-3 py-2" value="0987654321">
            </div>
            <div class="col-md-12">
              <label class="form-label small fw-bold text-secondary text-uppercase">Email</label>
              <input type="email" class="form-control border-0 bg-light rounded-3 px-3 py-2" value="nguyenvana@gmail.com" disabled>
            </div>
            <div class="col-md-12 mt-5">
              <button type="submit" class="btn btn-dark rounded-pill px-5 py-2 fw-bold shadow-sm">LƯU THAY ĐỔI</button>
            </div>
          </form>

          <div class="mt-5 pt-5 border-top">
            <h5 class="fw-bold mb-3">Đổi mật khẩu</h5>
            <div class="row g-3">
              <div class="col-md-4">
                <input type="password" class="form-control border-0 bg-light rounded-3 px-3 py-2" placeholder="Mật khẩu hiện tại">
              </div>
              <div class="col-md-4">
                <input type="password" class="form-control border-0 bg-light rounded-3 px-3 py-2" placeholder="Mật khẩu mới">
              </div>
              <div class="col-md-4">
                <button class="btn btn-outline-dark w-100 rounded-pill py-2 fw-bold small">CẬP NHẬT</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.nav-pills .nav-link.active {
  background-color: #000;
  color: #fff !important;
}
.nav-link:hover:not(.active) {
  background-color: #f8f9fa;
}
.form-control:focus {
  background-color: #eee;
  box-shadow: none;
}
</style>
