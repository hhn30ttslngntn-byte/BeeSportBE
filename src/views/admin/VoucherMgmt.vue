<script setup>
import { ref } from 'vue';

const vouchers = ref([
  { id: 1, code: 'XUAN2024', discount: '20%', minOrder: 500000, expiry: '2024-03-01' },
  { id: 2, code: 'BEESPORT50', discount: '50,000 VNĐ', minOrder: 200000, expiry: '2024-02-15' },
]);

const confirmDelete = (code) => {
  if (confirm(`Xóa voucher "${code}"?`)) alert("Xóa thành công!");
};
</script>

<template>
  <div class="voucher-mgmt">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Quản Lý Voucher</h4>
      <router-link to="/admin/vouchers/add" class="btn btn-danger rounded-pill px-4">
        <i class="fas fa-plus me-2"></i>Thêm Voucher
      </router-link>
    </div>

    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr>
                <th class="ps-4">ID</th>
                <th>Mã Code</th>
                <th>Giảm giá</th>
                <th>Đơn tối thiểu</th>
                <th>Hết hạn</th>
                <th class="text-end pe-4">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="v in vouchers" :key="v.id">
                <td class="ps-4">#{{ v.id }}</td>
                <td><span class="badge bg-danger fw-bold">{{ v.code }}</span></td>
                <td>{{ v.discount }}</td>
                <td>{{ v.minOrder.toLocaleString() }} VNĐ</td>
                <td>{{ v.expiry }}</td>
                <td class="text-end pe-4">
                  <router-link :to="'/admin/vouchers/edit/' + v.id" class="btn btn-sm btn-outline-primary me-2"><i class="fas fa-edit"></i></router-link>
                  <button @click="confirmDelete(v.code)" class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
