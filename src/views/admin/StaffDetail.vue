<template>
  <div class="container-fluid py-4">
    <div class="card border-0 shadow-sm rounded-4">
      <div class="card-body p-4 p-md-5">
        <!-- Tiêu đề thay đổi theo chế độ -->
        <div class="d-flex align-items-center mb-5">
            <button @click="router.back()" class="btn btn-light rounded-circle me-3">
                <i class="fas fa-arrow-left"></i>
            </button>
            <h4 class="fw-bold m-0">{{ isEdit ? 'Cập Nhật Thông Tin Nhân Viên' : 'Thêm Nhân Viên Mới' }}</h4>
        </div>

        <form @submit.prevent="handleSave">
          <div class="row g-4">
            <!-- Ảnh đại diện -->
            <div class="col-12 text-center mb-4">
                <div class="avatar-upload position-relative d-inline-block">
                    <div class="avatar-preview rounded-circle border d-flex align-items-center justify-content-center bg-light">
                        <i class="fas fa-user fa-3x text-secondary"></i>
                    </div>
                    <button type="button" class="btn btn-sm btn-danger rounded-circle position-absolute bottom-0 end-0">
                        <i class="fas fa-camera"></i>
                    </button>
                </div>
            </div>

            <div class="col-md-6">
              <label class="form-label small fw-bold">Họ và Tên <span class="text-danger">*</span></label>
              <input v-model="staff.name" type="text" class="form-control rounded-3" placeholder="Nguyễn Văn A" required>
            </div>

            <div class="col-md-6">
              <label class="form-label small fw-bold">Email <span class="text-danger">*</span></label>
              <input v-model="staff.email" type="email" class="form-control rounded-3" placeholder="example@gmail.com" required>
            </div>

            <div class="col-md-6">
              <label class="form-label small fw-bold">Số điện thoại</label>
              <input v-model="staff.phone" type="text" class="form-control rounded-3" placeholder="09xxxxxxxx">
            </div>

            <div class="col-md-6">
              <label class="form-label small fw-bold">Chức vụ</label>
              <select v-model="staff.role" class="form-select rounded-3">
                <option value="Admin">Quản lý (Admin)</option>
                <option value="Nhân viên bán hàng">Nhân viên bán hàng</option>
                <option value="Nhân viên kho">Nhân viên kho</option>
              </select>
            </div>

            <div class="col-12">
              <label class="form-label small fw-bold">Địa chỉ</label>
              <textarea v-model="staff.address" class="form-control rounded-3" rows="2" placeholder="Số nhà, tên đường..."></textarea>
            </div>

            <div class="col-md-6">
              <label class="form-label small fw-bold">Mật khẩu {{ isEdit ? '(Để trống nếu không đổi)' : '' }}</label>
              <input type="password" class="form-control rounded-3" placeholder="********">
            </div>
            
            <div class="col-md-6">
              <label class="form-label small fw-bold">Trạng thái</label>
              <div class="d-flex gap-3 mt-2">
                <div class="form-check">
                  <input class="form-check-input" type="radio" value="Đang làm việc" v-model="staff.status" id="s1">
                  <label class="form-check-label" for="s1">Đang làm việc</label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="radio" value="Nghỉ phép" v-model="staff.status" id="s2">
                  <label class="form-check-label" for="s2">Nghỉ phép</label>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-5 pt-4 border-top d-flex gap-3">
            <button type="submit" class="btn btn-danger px-5 rounded-pill fw-bold">
              <i class="fas fa-save me-2"></i>{{ isEdit ? 'CẬP NHẬT' : 'LƯU NHÂN VIÊN' }}
            </button>
            <button type="button" @click="router.back()" class="btn btn-outline-secondary px-5 rounded-pill">HỦY BỎ</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

// Kiểm tra xem là Sửa hay Thêm dựa trên URL
const isEdit = ref(!!route.params.id);

const staff = ref({
  name: '',
  email: '',
  phone: '',
  role: 'Nhân viên bán hàng',
  address: '',
  status: 'Đang làm việc'
});

onMounted(() => {
  if (isEdit.value) {
    // Giả lập lấy dữ liệu từ server khi sửa
    console.log("Đang lấy dữ liệu nhân viên ID:", route.params.id);
    staff.value = {
      name: 'Lê Quản Lý',
      email: 'admin@beesport.com',
      phone: '0987.654.321',
      role: 'Admin',
      address: '125A6 Trần Huy Liệu, Ba Đình, Hà Nội',
      status: 'Đang làm việc'
    };
  }
});

const handleSave = () => {
  // Logic lưu dữ liệu (Sau này sẽ gọi API)
  const message = isEdit.value ? 'Cập nhật thành công!' : 'Thêm nhân viên thành công!';
  alert(message);
  router.push('/admin/staff'); // Quay lại trang danh sách
};
</script>

<style scoped>
.avatar-preview {
    width: 120px;
    height: 120px;
}
.form-control:focus, .form-select:focus {
    border-color: #dc3545;
    box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.1);
}
label {
    letter-spacing: 0.5px;
    color: #555;
}
</style>