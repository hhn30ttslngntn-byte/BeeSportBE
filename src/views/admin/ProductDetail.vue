<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps(['id']);
const router = useRouter();
const isEdit = ref(!!props.id);

const product = ref({
  name: '',
  category: '',
  price: 0,
  stock: 0,
  description: ''
});

onMounted(() => {
  if (isEdit.value) {
    // Giả lập load dữ liệu
    product.value = {
      name: 'Giày Nike Zoom Edit',
      category: 'Giày',
      price: 1200000,
      stock: 50,
      description: 'Mô tả chi tiết sản phẩm...'
    };
  }
});

const handleSave = () => {
  alert(isEdit.value ? "Cập nhật thành công!" : "Thêm mới thành công!");
  router.push('/admin/products');
};
</script>

<template>
  <div class="product-detail">
    <div class="d-flex align-items-center mb-4">
      <button @click="router.back()" class="btn btn-outline-secondary rounded-circle me-3">
        <i class="fas fa-arrow-left"></i>
      </button>
      <h4 class="fw-bold mb-0">{{ isEdit ? 'Chỉnh sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}</h4>
    </div>

    <div class="card border-0 shadow-sm rounded-4">
      <div class="card-body p-4 p-md-5">
        <form @submit.prevent="handleSave">
          <div class="row g-4">
            <div class="col-md-6">
              <label class="form-label small fw-bold text-uppercase">Tên sản phẩm</label>
              <input type="text" v-model="product.name" class="form-control bg-light border-0" required>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-bold text-uppercase">Danh mục</label>
              <select v-model="product.category" class="form-select bg-light border-0">
                <option value="">Chọn danh mục</option>
                <option value="Giày">Giày</option>
                <option value="Quần Áo">Quần Áo</option>
                <option value="Phụ Kiện">Phụ Kiện</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-bold text-uppercase">Giá bán (VNĐ)</label>
              <input type="number" v-model="product.price" class="form-control bg-light border-0" required>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-bold text-uppercase">Số lượng kho</label>
              <input type="number" v-model="product.stock" class="form-control bg-light border-0" required>
            </div>
            <div class="col-12">
              <label class="form-label small fw-bold text-uppercase">Mô tả chi tiết</label>
              <textarea v-model="product.description" rows="4" class="form-control bg-light border-0"></textarea>
            </div>
            <div class="col-12 text-end mt-4">
              <button type="button" @click="router.back()" class="btn btn-light rounded-pill px-4 me-2">Hủy</button>
              <button type="submit" class="btn btn-danger rounded-pill px-5 fw-bold">Lưu lại</button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
