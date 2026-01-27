<script setup></script>
<template>
  <footer class="bg-dark text-light py-5">
    <div class="container">
      <div class="row gy-4">
        <div class="col-lg-4">
          <img src="/src/img/logo2.jpg" height="80" class="mb-3 rounded" alt="Logo">
          <p class="text-secondary small pe-lg-5">BeeSport - Hệ thống cửa hàng đồ thể thao nam uy tín. Chất lượng bền bỉ, phong cách dẫn đầu.</p>
          <div class="d-flex gap-3">
            <a href="#" class="text-white fs-5"><i class="fab fa-facebook"></i></a>
            <a href="#" class="text-white fs-5"><i class="fab fa-tiktok"></i></a>
          </div>
        </div>
        <div class="col-lg-2">
          <h6 class="fw-bold mb-4">KHÁM PHÁ</h6>
          <ul class="list-unstyled small text-secondary">
            <li class="mb-2"><router-link to="/" class="text-reset text-decoration-none">Trang chủ</router-link></li>
            <li class="mb-2"><router-link to="/products" class="text-reset text-decoration-none">Sản phẩm</router-link></li>
            <li class="mb-2"><router-link to="/about" class="text-reset text-decoration-none">Giới thiệu</router-link></li>
            <li class="mb-2"><router-link to="/contact" class="text-reset text-decoration-none">Liên hệ</router-link></li>
          </ul>
        </div>
        <div class="col-lg-2">
          <h6 class="fw-bold mb-4">HỖ TRỢ</h6>
          <ul class="list-unstyled small text-secondary">
            <li class="mb-2">Đổi trả 7 ngày</li>
            <li class="mb-2">Chính sách bảo hành</li>
            <li class="mb-2">Chính sách bảo mật</li>
            <li class="mb-2">Điều khoản dịch vụ</li>
          </ul>
        </div>
        <div class="col-lg-4">
          <h6 class="fw-bold mb-4">LIÊN HỆ</h6>
          <p class="small text-secondary"><i class="fas fa-phone me-2"></i> +84 363652758</p>
          <p class="small text-secondary"><i class="fas fa-envelope me-2"></i> beesport@gmail.com</p>
          <div class="input-group mt-3">
            <input type="text" class="form-control form-control-sm bg-transparent border-secondary text-white shadow-none" placeholder="Nhận ưu đãi qua email">
            <button class="btn btn-danger btn-sm px-3">GỬI</button>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>