<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps(['id']);
const router = useRouter();
const isEdit = ref(!!props.id);

const promotion = ref({
  name: '',
  discount: '',
  start: '',
  end: ''
});

onMounted(() => {
  if (isEdit.value) {
    promotion.value = {
      name: 'Siêu Sale Mùa Xuân',
      discount: 'Giảm 30%',
      start: '2024-01-01',
      end: '2024-02-01'
    };
  }
});

const handleSave = () => {
  alert("Lưu khuyến mãi thành công!");
  router.push('/admin/promotions');
};
</script>

<template>
  <div class="promotion-detail p-4">
    <h4 class="fw-bold mb-4">{{ isEdit ? 'Sửa Khuyến Mãi' : 'Thêm Khuyến Mãi' }}</h4>
    <div class="card border-0 shadow-sm p-4">
      <form @submit.prevent="handleSave">
        <div class="row g-3">
          <div class="col-12">
            <label class="form-label">Tên chương trình</label>
            <input type="text" v-model="promotion.name" class="form-control" required>
          </div>
          <div class="col-md-6">
            <label class="form-label">Mô tả giảm giá</label>
            <input type="text" v-model="promotion.discount" class="form-control" required>
          </div>
          <div class="col-md-3">
            <label class="form-label">Bắt đầu</label>
            <input type="date" v-model="promotion.start" class="form-control" required>
          </div>
          <div class="col-md-3">
            <label class="form-label">Kết thúc</label>
            <input type="date" v-model="promotion.end" class="form-control" required>
          </div>
          <div class="col-12 text-end">
            <button type="submit" class="btn btn-danger px-4">Lưu</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
