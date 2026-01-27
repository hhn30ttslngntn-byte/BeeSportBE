<script setup>
import { ref } from 'vue';

const promotions = ref([
  { id: 1, name: 'Siêu Sale Mùa Xuân', discount: 'Giảm 30% toàn bộ giày', start: '2024-01-01', end: '2024-02-01' },
]);

const confirmDelete = (name) => {
  if (confirm(`Xóa khuyến mãi "${name}"?`)) alert("Xóa thành công!");
};
</script>

<template>
  <div class="promotion-mgmt">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Quản Lý Khuyến Mãi</h4>
      <router-link to="/admin/promotions/add" class="btn btn-danger rounded-pill px-4">
        <i class="fas fa-plus me-2"></i>Thêm Khuyến Mãi
      </router-link>
    </div>

    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr>
                <th class="ps-4">ID</th>
                <th>Tên Khuyến Mãi</th>
                <th>Chi tiết</th>
                <th>Bắt đầu</th>
                <th>Kết thúc</th>
                <th class="text-end pe-4">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in promotions" :key="p.id">
                <td class="ps-4">#{{ p.id }}</td>
                <td class="fw-bold">{{ p.name }}</td>
                <td>{{ p.discount }}</td>
                <td>{{ p.start }}</td>
                <td>{{ p.end }}</td>
                <td class="text-end pe-4">
                  <router-link :to="'/admin/promotions/edit/' + p.id" class="btn btn-sm btn-outline-primary me-2"><i class="fas fa-edit"></i></router-link>
                  <button @click="confirmDelete(p.name)" class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
