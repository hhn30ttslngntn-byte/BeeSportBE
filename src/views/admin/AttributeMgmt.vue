<script setup>
import { ref } from 'vue';

const attributes = ref([
  { id: 1, type: 'Kích cỡ', values: ['38', '39', '40', '41', '42', '43'] },
  { id: 2, type: 'Màu sắc', values: ['Đỏ', 'Xanh', 'Đen', 'Trắng'] },
  { id: 3, type: 'Chất liệu', values: ['Da thật', 'Da tổng hợp', 'Vải'] },
]);
</script>

<template>
  <div class="attribute-mgmt">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Quản Lý Thuộc Tính</h4>
      <button class="btn btn-danger rounded-pill px-4">
        <i class="fas fa-plus me-2"></i>Thêm Thuộc Tính
      </button>
    </div>

    <div class="row">
      <div v-for="attr in attributes" :key="attr.id" class="col-md-4 mb-4">
        <div class="card border-0 shadow-sm rounded-3">
          <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
            <h6 class="mb-0 fw-bold">{{ attr.type }}</h6>
            <button class="btn btn-sm btn-light"><i class="fas fa-edit"></i></button>
          </div>
          <div class="card-body">
            <div class="d-flex flex-wrap gap-2">
              <span v-for="val in attr.values" :key="val" class="badge bg-light text-dark border">{{ val }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
