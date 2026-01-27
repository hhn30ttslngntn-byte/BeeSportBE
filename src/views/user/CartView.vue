<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-5 text-center">GIỎ HÀNG CỦA BẠN</h2>
    
    <div class="row g-5">
      <!-- Cart Items -->
      <div class="col-lg-8">
        <div class="table-responsive bg-white rounded-4 shadow-sm p-4">
          <table class="table align-middle mb-0">
            <thead class="text-secondary small">
              <tr>
                <th>SẢN PHẨM</th>
                <th>GIÁ</th>
                <th>SỐ LƯỢNG</th>
                <th>TỔNG</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in 2" :key="i">
                <td>
                  <div class="d-flex align-items-center">
                    <img src="https://via.placeholder.com/80x100" class="rounded-3 me-3" alt="Product">
                    <div>
                      <h6 class="fw-bold mb-1 small">Áo Thun Running Bee Pro {{ i }}</h6>
                      <p class="text-secondary mb-0" style="font-size: 11px;">SIZE: L | MÀU: ĐEN</p>
                    </div>
                  </div>
                </td>
                <td class="fw-bold small">450.000đ</td>
                <td>
                  <div class="input-group input-group-sm" style="width: 100px;">
                    <button class="btn btn-outline-secondary border-0 shadow-none"><i class="fas fa-minus small"></i></button>
                    <input type="text" class="form-control text-center border-0 fw-bold" value="1">
                    <button class="btn btn-outline-secondary border-0 shadow-none"><i class="fas fa-plus small"></i></button>
                  </div>
                </td>
                <td class="fw-bold text-danger small">450.000đ</td>
                <td>
                  <button class="btn btn-link text-secondary p-0 shadow-none"><i class="far fa-trash-alt"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <router-link to="/products" class="btn btn-link text-dark text-decoration-none mt-4 p-0 small fw-bold">
          <i class="fas fa-arrow-left me-2"></i> TIẾP TỤC MUA SẮM
        </router-link>
      </div>

      <!-- Summary -->
      <div class="col-lg-4">
        <div class="bg-white rounded-4 shadow-sm p-4">
          <h5 class="fw-bold mb-4">TỔNG ĐƠN HÀNG</h5>
          <div class="d-flex justify-content-between mb-3 small">
            <span class="text-secondary">Tạm tính:</span>
            <span class="fw-bold">900.000đ</span>
          </div>
          <div class="d-flex justify-content-between mb-3 small">
            <span class="text-secondary">Phí vận chuyển:</span>
            <span class="fw-bold">Miễn phí</span>
          </div>
          <hr>
          <div class="d-flex justify-content-between mb-4">
            <span class="fw-bold text-uppercase">Tổng cộng:</span>
            <span class="fw-bold text-danger fs-5">900.000đ</span>
          </div>
          <button class="btn btn-dark w-100 rounded-pill py-3 fw-bold mb-3">THANH TOÁN NGAY</button>
          <div class="text-center">
            <img src="https://img.icons8.com/color/48/000000/visa.png" width="30" class="mx-1">
            <img src="https://img.icons8.com/color/48/000000/mastercard.png" width="30" class="mx-1">
            <img src="https://img.icons8.com/color/48/000000/paypal.png" width="30" class="mx-1">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.table th { border-top: none; }
.form-control:focus { background: #f8f9fa; }
</style>
