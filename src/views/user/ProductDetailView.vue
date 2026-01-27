<template>
  <div class="container py-5">
    <div class="row g-5">
      <!-- Product Images -->
      <div class="col-lg-7">
        <div class="row g-3">
          <div class="col-2">
            <div class="d-flex flex-column gap-3 thumb-container">
              <img v-for="i in 4" :key="i" src="https://via.placeholder.com/100x120" class="img-fluid rounded-3 cursor-pointer border shadow-sm thumb" alt="Thumb">
            </div>
          </div>
          <div class="col-10">
            <div class="main-img-container bg-white rounded-4 shadow-sm overflow-hidden border">
              <img src="https://via.placeholder.com/600x750" class="img-fluid w-100 h-100 object-fit-cover" alt="Main Product Image">
            </div>
          </div>
        </div>
      </div>

      <!-- Product Info -->
      <div class="col-lg-5">
        <div class="ps-lg-4">
          <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb small">
              <li class="breadcrumb-item"><router-link to="/" class="text-secondary text-decoration-none">TRANG CHỦ</router-link></li>
              <li class="breadcrumb-item"><router-link to="/products" class="text-secondary text-decoration-none">SẢN PHẨM</router-link></li>
              <li class="breadcrumb-item active fw-bold text-dark">CHI TIẾT</li>
            </ol>
          </nav>

          <h2 class="fw-bold mb-3">Áo Thun Running Bee Pro</h2>
          <div class="d-flex align-items-center mb-4">
            <div class="text-warning me-2 small">
              <i class="fas fa-star" v-for="i in 5" :key="i"></i>
            </div>
            <span class="text-secondary small">(12 đánh giá)</span>
          </div>

          <h3 class="text-danger fw-bold mb-4 fs-2">450.000đ</h3>
          
          <p class="text-secondary mb-5 small lh-lg">
            Áo thun thể thao chuyên dụng dành cho chạy bộ và các hoạt động cường độ cao. Chất liệu vải mesh siêu nhẹ, thoáng khí, hỗ trợ thoát mồ hôi cực tốt giúp bạn luôn khô thoáng suốt buổi tập.
          </p>

          <!-- Selectors -->
          <div class="mb-4">
            <h6 class="fw-bold small mb-3 text-uppercase">Màu sắc: <span class="text-secondary fw-normal">Đen</span></h6>
            <div class="d-flex gap-2">
              <div class="color-swatch bg-dark active"></div>
              <div class="color-swatch bg-primary"></div>
              <div class="color-swatch bg-danger"></div>
            </div>
          </div>

          <div class="mb-5">
            <h6 class="fw-bold small mb-3 text-uppercase">Kích thước: <span class="text-secondary fw-normal">L</span></h6>
            <div class="d-flex gap-2">
              <div class="size-box active">S</div>
              <div class="size-box">M</div>
              <div class="size-box">L</div>
              <div class="size-box">XL</div>
            </div>
          </div>

          <!-- Actions -->
          <div class="row g-3 mb-5">
            <div class="col-4">
              <div class="qty-container d-flex align-items-center justify-content-between border rounded-pill px-3 py-2">
                <button class="btn btn-link text-dark p-0 shadow-none"><i class="fas fa-minus small"></i></button>
                <span class="fw-bold">1</span>
                <button class="btn btn-link text-dark p-0 shadow-none"><i class="fas fa-plus small"></i></button>
              </div>
            </div>
            <div class="col-8">
              <button class="btn btn-dark w-100 rounded-pill py-2 fw-bold shadow-sm">
                <i class="fas fa-shopping-cart me-2"></i> THÊM VÀO GIỎ HÀNG
              </button>
            </div>
          </div>

          <!-- Meta -->
          <div class="pt-4 border-top">
            <p class="small mb-2"><span class="fw-bold me-2">SKU:</span> <span class="text-secondary">BE-RUN-001</span></p>
            <p class="small mb-2"><span class="fw-bold me-2">DANH MỤC:</span> <span class="text-secondary">Áo Thể Thao Nam</span></p>
            <div class="d-flex gap-3 mt-4">
              <a href="#" class="text-secondary small text-decoration-none"><i class="fab fa-facebook me-1"></i> Chia sẻ</a>
              <a href="#" class="text-secondary small text-decoration-none"><i class="fab fa-twitter me-1"></i> Tweet</a>
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
.thumb-container .thumb {
  opacity: 0.6;
  transition: 0.3s;
}
.thumb-container .thumb:hover, .thumb-container .thumb.active {
  opacity: 1;
  border-color: #000 !important;
}
.color-swatch {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px #ddd;
}
.color-swatch.active {
  box-shadow: 0 0 0 2px #000;
}
.size-box {
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  font-size: 13px;
  transition: 0.2s;
}
.size-box:hover, .size-box.active {
  background-color: #000;
  color: #fff;
  border-color: #000;
}
.main-img-container {
  aspect-ratio: 4/5;
}
</style>
