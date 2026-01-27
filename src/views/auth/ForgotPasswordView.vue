<script setup>
import { ref } from 'vue';

const email = ref('');

const handleForgotPassword = () => {
  console.log("Yêu cầu khôi phục mật khẩu cho:", email.value);
  alert("Link khôi phục mật khẩu đã được gửi đến email: " + email.value);
};
</script>

<template>
  <div class="forgot-password-wrapper d-flex align-items-center">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
          <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
            <div class="card-body p-4 p-sm-5">
              <div class="text-center mb-4">
                <img src="/src/img/logo1.jpg" alt="BeeSport" class="img-fluid mb-3" style="max-height: 70px;">
                <h3 class="fw-bold text-dark">QUÊN MẬT KHẨU</h3>
                <p class="text-secondary small">Nhập email của bạn để nhận link khôi phục mật khẩu.</p>
              </div>

              <form @submit.prevent="handleForgotPassword">
                <div class="mb-4">
                  <label class="form-label small fw-bold text-uppercase">Email của bạn</label>
                  <div class="input-group custom-input-group">
                    <span class="input-group-text bg-light border-0"><i class="far fa-envelope"></i></span>
                    <input 
                      type="email" 
                      v-model="email"
                      class="form-control bg-light border-0 shadow-none" 
                      placeholder="example@gmail.com"
                      required
                    >
                  </div>
                </div>

                <button type="submit" class="btn btn-danger w-100 py-2 fw-bold rounded-pill shadow-sm mb-4">
                  GỬI YÊU CẦU
                </button>
              </form>

              <div class="text-center mt-3">
                <router-link to="/login" class="text-primary small text-decoration-none fw-bold">
                  <i class="fas fa-arrow-left me-1"></i> Quay lại Đăng nhập
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.forgot-password-wrapper {
  min-height: 80vh;
  padding: 40px 0;
}

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

.card {
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.1) !important;
}
</style>
