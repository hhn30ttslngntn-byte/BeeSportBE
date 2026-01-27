<script setup>
// Không cần thêm logic, Bootstrap JS sẽ tự điều khiển slide nếu bạn đã thêm link ở index.html
</script>

<template>
  <div class="home-page">
    <!-- 1. SLIDE BANNER (CAROUSEL) -->
    <div id="heroCarousel" class="carousel slide carousel-fade shadow-sm" data-bs-ride="carousel">
      <!-- Chỉ số Slide (Dấu gạch ở dưới) -->
      <div class="carousel-indicators">
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
      </div>

      <div class="carousel-inner">
        <!-- Slide 1 -->
        <div class="carousel-item active" data-bs-interval="5000">
          <div class="banner-slide slide-1">
            <div class="overlay"></div>
            <div class="container h-100 d-flex align-items-center justify-content-center text-center">
              <div class="banner-text">
                <h1 class="display-2 fw-bold text-white mb-3">BEE SPORT</h1>
                <p class="lead text-white mb-4 fs-4">Đẳng cấp thời trang thể thao nam - Nâng tầm phong độ</p>
                <router-link to="/products" class="btn btn-danger btn-lg px-5 rounded-pill fw-bold shadow-lg">MUA NGAY</router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Slide 2 -->
        <div class="carousel-item" data-bs-interval="5000">
          <div class="banner-slide slide-2">
            <div class="overlay"></div>
            <div class="container h-100 d-flex align-items-center justify-content-center text-center text-white">
              <div class="banner-text">
                <h1 class="display-2 fw-bold mb-3">BỘ SƯU TẬP MỚI</h1>
                <p class="lead mb-4 fs-4">Đột phá công năng - Bứt phá giới hạn cùng các dòng vải cao cấp</p>
                <router-link to="/products" class="btn btn-outline-light btn-lg px-5 rounded-pill fw-bold shadow-lg">XEM CHI TIẾT</router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Slide 3 -->
        <div class="carousel-item" data-bs-interval="5000">
          <div class="banner-slide slide-3">
            <div class="overlay"></div>
            <div class="container h-100 d-flex align-items-center justify-content-center text-center text-white">
              <div class="banner-text">
                <h1 class="display-2 fw-bold mb-3 text-warning">ƯU ĐÃI ĐẾN 50%</h1>
                <p class="lead mb-4 fs-4">Áp dụng cho toàn bộ các dòng phụ kiện và giày chạy bộ</p>
                <router-link to="/products" class="btn btn-warning btn-lg px-5 rounded-pill fw-bold shadow-lg text-dark">SĂN DEAL NGAY</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Nút điều hướng Trái/Phải -->
      <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon shadow"></span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon shadow"></span>
      </button>
    </div>

    <!-- 2. DANH MỤC SẢN PHẨM NỔI BẬT -->
    <section class="container py-5 mt-4">
      <div class="text-center mb-5">
        <h2 class="fw-bold text-dark text-uppercase">Danh mục nổi bật</h2>
        <div class="mx-auto bg-danger mt-2" style="width: 60px; height: 3px;"></div>
      </div>

      <div class="row g-4 text-center">
        <!-- Cột 1 -->
        <div class="col-md-4">
          <div class="category-card shadow-sm rounded-4 overflow-hidden position-relative">
            <img src="https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=600" class="img-fluid" alt="Đồ Nam">
            <div class="category-info d-flex flex-column justify-content-end p-4">
              <h4 class="fw-bold text-white mb-0">ĐỒ NAM</h4>
              <p class="text-white-50 small mb-0">Năng động & Khỏe khoắn</p>
            </div>
          </div>
        </div>
        <!-- Cột 2 -->
        <div class="col-md-4">
          <div class="category-card shadow-sm rounded-4 overflow-hidden position-relative">
            <img src="https://images.unsplash.com/photo-1552674605-db6ffd4facb5?q=80&w=600" class="img-fluid" alt="Đồ Nữ">
            <div class="category-info d-flex flex-column justify-content-end p-4">
              <h4 class="fw-bold text-white mb-0">ĐỒ NỮ</h4>
              <p class="text-white-50 small mb-0">Thanh lịch & Thể thao</p>
            </div>
          </div>
        </div>
        <!-- Cột 3 -->
        <div class="col-md-4">
          <div class="category-card shadow-sm rounded-4 overflow-hidden position-relative">
            <img src="https://images.unsplash.com/photo-1491553895911-0055eca6402d?q=80&w=600" class="img-fluid" alt="Bé Trai">
            <div class="category-info d-flex flex-column justify-content-end p-4">
              <h4 class="fw-bold text-white mb-0">BÉ TRAI</h4>
              <p class="text-white-50 small mb-0">Thoải mái & Vui tươi</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* CSS CHO SLIDE BANNER */
.banner-slide {
  height: 550px; /* Chiều cao lý tưởng cho banner PC */
  background-size: cover;
  background-position: center;
  position: relative;
}

/* Ảnh nền mẫu cho từng slide */
.slide-1 { background-image: url('/src/img/sl1.jpg'); }
.slide-2 { background-image: url('/src/img/sl2.jpg'); }
.slide-3 { background-image: url('/src/img/b4.jpg'); }

/* Lớp phủ mờ giúp chữ luôn nổi bật trên ảnh */
.overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.4); /* Làm tối ảnh 40% */
}

.banner-text {
  position: relative;
  z-index: 10;
  text-shadow: 2px 2px 10px rgba(0,0,0,0.3);
}

/* CSS CHO DANH MỤC SẢN PHẨM */
.category-card {
  transition: 0.4s ease;
  cursor: pointer;
  height: 400px;
}

.category-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: 0.6s ease;
}

.category-info {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 60%);
  transition: 0.3s;
}

/* Hiệu ứng hover cho card */
.category-card:hover img {
  transform: scale(1.1);
}

.category-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 1rem 3rem rgba(0,0,0,0.2) !important;
}

/* RESPONSIVE */
@media (max-width: 768px) {
  .banner-slide { height: 350px; }
  .display-2 { font-size: 2.5rem; }
  .category-card { height: 300px; }
}
</style>