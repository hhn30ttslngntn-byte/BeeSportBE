<script setup>
import { ref } from 'vue';

const categories = ref([
  { id: 1, name: 'Giày Bóng Đá', description: 'Các loại giày thi đấu chuyên nghiệp' },
  { id: 2, name: 'Quần Áo Thể Thao', description: 'Trang phục tập luyện và thi đấu' },
  { id: 3, name: 'Phụ Kiện', description: 'Tất, găng tay, bóng, bảo vệ ống đồng' },
]);
</script>

<template>
  <div class="category-mgmt">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Quản Lý Danh Mục</h4>
      <button class="btn btn-danger rounded-pill px-4">
        <i class="fas fa-plus me-2"></i>Thêm Danh Mục
      </button>
    </div>

    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr>
                <th class="ps-4">ID</th>
                <th>Tên Danh Mục</th>
                <th>Mô Tả</th>
                <th class="text-end pe-4">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="category in categories" :key="category.id">
                <td class="ps-4">#{{ category.id }}</td>
                <td class="fw-bold">{{ category.name }}</td>
                <td class="text-secondary small">{{ category.description }}</td>
                <td class="text-end pe-4">
                  <button class="btn btn-sm btn-outline-primary me-2"><i class="fas fa-edit"></i></button>
                  <button class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.table thead th {
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 15px 10px;
}
.table tbody td {
  padding: 15px 10px;
}
</style>
