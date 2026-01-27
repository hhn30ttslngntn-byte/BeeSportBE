<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');

const handleLogin = () => {
  console.log("Đăng nhập với:", username.value, password.value);
  alert("Hệ thống đang xử lý đăng nhập cho: " + username.value);
};
</script>

<template>
  <div class="login-wrapper d-flex align-items-center">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
          <!-- Card Đăng nhập -->
          <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
            <div class="card-body p-4 p-sm-5">
              
              <!-- Logo BeeSport (To và Rõ) -->
              <div class="text-center mb-4">
                <img src="/src/img/logo1.jpg" alt="BeeSport" class="img-fluid mb-3" style="max-height: 70px;">
                <h3 class="fw-bold text-dark">ĐĂNG NHẬP</h3>
                <p class="text-secondary small">Chào mừng bạn đến với BeeSport!</p>
              </div>

              <form @submit.prevent="handleLogin">
                <!-- Nhập Tài khoản -->
                <div class="mb-4">
                  <label class="form-label small fw-bold text-uppercase">Tên tài khoản hoặc Email</label>
                  <div class="input-group custom-input-group">
                    <span class="input-group-text bg-light border-0"><i class="far fa-user"></i></span>
                    <input 
                      type="text" 
                      v-model="username"
                      class="form-control bg-light border-0 shadow-none" 
                      placeholder="Nhập tài khoản..."
                      required
                    >
                  </div>
                </div>

                <!-- Nhập Mật khẩu -->
                <div class="mb-3">
                  <label class="form-label small fw-bold text-uppercase">Mật khẩu</label>
                  <div class="input-group custom-input-group">
                    <span class="input-group-text bg-light border-0"><i class="fas fa-lock"></i></span>
                    <input 
                      type="password" 
                      v-model="password"
                      class="form-control bg-light border-0 shadow-none" 
                      placeholder="********"
                      required
                    >
                  </div>
                </div>

                <!-- Ghi nhớ & Quên mật khẩu -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                  <div class="form-check">
                    <input type="checkbox" class="form-check-input shadow-none" id="rememberMe">
                    <label class="form-check-label small" for="rememberMe">Ghi nhớ</label>
                  </div>
                  <router-link to="/forgot-password" class="text-danger small text-decoration-none fw-bold">Quên mật khẩu?</router-link>
                </div>

                <!-- Nút Đăng nhập -->
                <button type="submit" class="btn btn-danger w-100 py-2 fw-bold rounded-pill shadow-sm mb-4">
                  ĐĂNG NHẬP NGAY
                </button>

                <!-- Đăng nhập bằng MXH (Làm cho xịn hơn) -->
                <div class="text-center position-relative mb-4">
                  <hr class="text-secondary">
                  <span class="position-absolute top-50 start-50 translate-middle bg-white px-3 small text-secondary">Hoặc</span>
                </div>

                <div class="d-flex gap-2 mb-4">
                  <button type="button" class="btn btn-outline-secondary w-100 rounded-pill small">
                    <i class="fab fa-google text-danger me-2"></i> Google
                  </button>
                  <button type="button" class="btn btn-outline-secondary w-100 rounded-pill small">
                    <i class="fab fa-facebook text-primary me-2"></i> Facebook
                  </button>
                </div>
              </form>

              <!-- Link Đăng ký -->
              <div class="text-center mt-3">
                <span class="small text-secondary">Bạn chưa có tài khoản? </span>
                <router-link to="/register" class="text-primary small text-decoration-none fw-bold">Tạo tài khoản ngay</router-link>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Bao phủ toàn màn hình để căn giữa */
.login-wrapper {
  min-height: 80vh;
  padding: 40px 0;
}

/* Hiệu ứng cho ô input */
.custom-input-group {
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid transparent;
  transition: 0.3s;
}

.custom-input-group:focus-within {
  border-color: #dc3545;
  box-shadow: 0 0 10px rgba(220, 53, 69, 0.1);
}

.form-control {
  padding: 12px;
}

/* Nút bấm */
.btn-danger {
  background-color: #dc3545;
  border: none;
  letter-spacing: 1px;
  transition: 0.3s;
}

.btn-danger:hover {
  background-color: #bb2d3b;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(220, 53, 69, 0.3);
}

.btn-outline-secondary {
  font-size: 13px;
  border-color: #eee;
}

.btn-outline-secondary:hover {
  background-color: #f8f9fa;
  color: #333;
  border-color: #ddd;
}

/* Card shadow */
.card {
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.1) !important;
}
</style>