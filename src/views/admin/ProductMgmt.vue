<script setup>
import { ref } from 'vue';

const products = ref([
  { id: 1, name: 'Giày Bóng Đá Nike Zoom', category: 'Giày', price: 1200000, stock: 50 },
  { id: 2, name: 'Áo Thi Đấu Adidas', category: 'Quần Áo', price: 500000, stock: 100 },
  { id: 3, name: 'Quả Bóng Động Lực', category: 'Phụ Kiện', price: 300000, stock: 200 },
]);

const confirmDelete = (name) => {
  if (confirm(`Bạn có chắc chắn muốn xóa sản phẩm "${name}"?`)) {
    alert("Đã xóa thành công!");
  }
};
</script>

<template>
  <div class="product-mgmt">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Quản Lý Sản Phẩm</h4>
      <router-link to="/admin/products/add" class="btn btn-danger rounded-pill px-4">
        <i class="fas fa-plus me-2"></i>Thêm Sản Phẩm
      </router-link>
    </div>

    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr>
                <th class="ps-4">ID</th>
                <th>Tên Sản Phẩm</th>
                <th>Danh Mục</th>
                <th>Giá (VNĐ)</th>
                <th>Kho hàng</th>
                <th class="text-end pe-4">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in products" :key="product.id">
                <td class="ps-4">#{{ product.id }}</td>
                <td class="fw-bold">{{ product.name }}</td>
                <td><span class="badge bg-info text-dark opacity-75">{{ product.category }}</span></td>
                <td>{{ product.price.toLocaleString() }}</td>
                <td>{{ product.stock }}</td>
                <td class="text-end pe-4">
                  <router-link :to="'/admin/products/edit/' + product.id" class="btn btn-sm btn-outline-primary me-2">
                    <i class="fas fa-edit"></i>
                  </router-link>
                  <button @click="confirmDelete(product.name)" class="btn btn-sm btn-outline-danger">
                    <i class="fas fa-trash"></i>
                  </button>
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
