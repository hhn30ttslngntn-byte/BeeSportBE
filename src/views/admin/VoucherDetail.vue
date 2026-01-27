<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps(['id']);
const router = useRouter();
const isEdit = ref(!!props.id);

const voucher = ref({
  code: '',
  discount: '',
  minOrder: 0,
  expiry: ''
});

onMounted(() => {
  if (isEdit.value) {
    voucher.value = {
      code: 'XUAN2024',
      discount: '20%',
      minOrder: 500000,
      expiry: '2024-03-01'
    };
  }
});

const handleSave = () => {
  alert("Lưu voucher thành công!");
  router.push('/admin/vouchers');
};
</script>

<template>
  <div class="voucher-detail p-4">
    <h4 class="fw-bold mb-4">{{ isEdit ? 'Sửa Voucher' : 'Thêm Voucher' }}</h4>
    <div class="card border-0 shadow-sm p-4">
      <form @submit.prevent="handleSave">
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Mã Voucher</label>
            <input type="text" v-model="voucher.code" class="form-control" required>
          </div>
          <div class="col-md-6">
            <label class="form-label">Mức giảm</label>
            <input type="text" v-model="voucher.discount" class="form-control" placeholder="Ví dụ: 20% hoặc 50,000 VNĐ" required>
          </div>
          <div class="col-md-6">
            <label class="form-label">Đơn tối thiểu</label>
            <input type="number" v-model="voucher.minOrder" class="form-control" required>
          </div>
          <div class="col-md-6">
            <label class="form-label">Ngày hết hạn</label>
            <input type="date" v-model="voucher.expiry" class="form-control" required>
          </div>
          <div class="col-12 text-end">
            <button type="submit" class="btn btn-danger px-4">Lưu</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
