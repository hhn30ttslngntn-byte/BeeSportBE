<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router'; // Cần thiết để chuyển trang

const router = useRouter();
const selectedStaff = ref(null); // Lưu nhân viên để xem chi tiết

// Dữ liệu mẫu nhân viên đầy đủ để hiện trong Modal
const staffList = ref([
  { id: 1, name: 'Lê Quản Lý', role: 'Admin', email: 'admin@beesport.com', status: 'Đang làm việc', phone: '0987.654.321', startDate: '01/01/2023', address: '125A6 Trần Huy Liệu, Ba Đình, Hà Nội' },
  { id: 2, name: 'Nguyễn Nhân Viên', role: 'Nhân viên bán hàng', email: 'nv1@beesport.com', status: 'Đang làm việc', phone: '0123.456.789', startDate: '15/05/2023', address: 'Quận Cầu Giấy, Hà Nội' },
  { id: 3, name: 'Trần Kho', role: 'Nhân viên kho', email: 'kho@beesport.com', status: 'Nghỉ phép', phone: '0333.222.111', startDate: '10/02/2023', address: 'Quận Nam Từ Liêm, Hà Nội' },
]);

const getStatusClass = (status) => {
  switch (status) {
    case 'Đang làm việc': return 'bg-success bg-opacity-10 text-success border border-success border-opacity-25';
    case 'Nghỉ phép': return 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-25';
    default: return 'bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25';
  }
};

// Hàm gán nhân viên vào Modal
const showDetail = (staff) => {
  selectedStaff.value = staff;
};

// Hàm đổi trạng thái
const toggleStatus = (staff) => {
  if (staff.status === 'Đang làm việc') {
    if(confirm(`Ngừng kích hoạt nhân viên ${staff.name}?`)) staff.status = 'Đã nghỉ việc';
  } else {
    staff.status = 'Đang làm việc';
  }
};
</script>

<template>
  <div class="card border-0 shadow-sm rounded-4">
    <div class="card-body p-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="fw-bold m-0 text-dark">Quản Lý Nhân Viên</h4>
        <button @click="router.push('/admin/staff/add')" class="btn btn-danger rounded-pill px-4 fw-bold shadow-sm">
          <i class="fas fa-plus-circle me-2"></i>Thêm Nhân Viên
        </button>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th class="py-3 px-3">ID</th>
              <th class="py-3">HỌ TÊN</th>
              <th class="py-3 text-center">TRẠNG THÁI</th>
              <th class="py-3 text-center">THAO TÁC</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="staff in staffList" :key="staff.id">
              <td class="px-3 fw-bold">#{{ staff.id }}</td>
              <td>
                <div class="fw-bold">{{ staff.name }}</div>
                <div class="text-secondary small">{{ staff.email }}</div>
              </td>
              <td class="text-center">
                <span :class="getStatusClass(staff.status)" class="badge rounded-pill px-3 py-2">
                  {{ staff.status }}
                </span>
              </td>
              <td class="text-center">
                <div class="d-flex justify-content-center gap-2">
                  <!-- NÚT XEM CHI TIẾT -->
                  <button @click="showDetail(staff)" class="btn-action btn-view" data-bs-toggle="modal" data-bs-target="#staffModal" title="Xem chi tiết">
                    <i class="fas fa-eye"></i>
                  </button>
                  <!-- NÚT SỬA MỚI -->
<button @click="router.push('/admin/staff/edit/' + staff.id)" class="btn-action btn-edit-custom" title="Chỉnh sửa">
  <i class="far fa-edit"></i> 
</button>
                  <!-- NÚT KHÓA -->
                  <button @click="toggleStatus(staff)" class="btn-action" :class="staff.status === 'Đang làm việc' ? 'btn-status-off' : 'btn-status-on'">
                    <i class="fas" :class="staff.status === 'Đang làm việc' ? 'fa-user-slash' : 'fa-user-check'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL XEM CHI TIẾT NHÂN VIÊN -->
    <div class="modal fade" id="staffModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-0 pb-0">
            <h5 class="fw-bold">Thông Tin Nhân Viên</h5>
            <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-4" v-if="selectedStaff">
            <div class="text-center mb-4">
                <div class="avatar-lg mx-auto mb-3 bg-danger text-white rounded-circle d-flex align-items-center justify-content-center display-6 fw-bold">
                    {{ selectedStaff.name.charAt(0) }}
                </div>
                <h4 class="fw-bold mb-1">{{ selectedStaff.name }}</h4>
                <span class="text-muted small fw-bold">{{ selectedStaff.role }}</span>
            </div>
            
            <div class="row g-3">
                <div class="col-6 border-end">
                    <label class="small text-muted d-block">Mã số</label>
                    <span class="fw-bold">BS-{{ selectedStaff.id }}</span>
                </div>
                <div class="col-6">
                    <label class="small text-muted d-block">Điện thoại</label>
                    <span class="fw-bold">{{ selectedStaff.phone }}</span>
                </div>
                <div class="col-12">
                    <label class="small text-muted d-block">Địa chỉ Email</label>
                    <span class="fw-bold">{{ selectedStaff.email }}</span>
                </div>
                <div class="col-12">
                    <label class="small text-muted d-block">Ngày bắt đầu</label>
                    <span class="fw-bold">{{ selectedStaff.startDate }}</span>
                </div>
                <div class="col-12 bg-light p-3 rounded-3 mt-3">
                    <label class="small text-muted d-block">Địa chỉ thường trú</label>
                    <span class="small">{{ selectedStaff.address }}</span>
                </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-action {
  width: 36px; height: 36px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid #eee; background: white; transition: 0.3s;
}
/* Kiểu dáng nút sửa giống hình mẫu */
.btn-edit-custom {
  width: 34px;
  height: 34px;
  border-radius: 6px; /* Bo góc nhẹ để ra hình vuông */
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border: 1.5px solid #007bff !important; /* Viền màu xanh dương */
  color: #007bff !important;           /* Icon màu xanh dương */
  transition: 0.3s;
}

.btn-edit-custom:hover {
  background-color: #007bff !important; /* Đổi nền xanh khi di chuột vào */
  color: white !important;             /* Đổi icon trắng khi di chuột vào */
}

/* Các nút khác giữ nguyên hoặc chỉnh tương tự */
.btn-view {
  border: 1.5px solid #6c757d;
  color: #6c757d;
}
.btn-view:hover { background: #6c757d; color: white; }

.btn-status-off {
  border: 1.5px solid #dc3545;
  color: #dc3545;
}
.btn-status-off:hover { background: #dc3545; color: white; }
.btn-view:hover { background: #e0f7fa; color: #00acc1; }
.btn-edit:hover { background: #e8eaf6; color: #3f51b5; }
.btn-status-off:hover { background: #fff3e0; color: #fb8c00; }
.btn-status-on:hover { background: #e8f5e9; color: #43a047; }
.avatar-lg { width: 70px; height: 70px; }
.table thead th { font-size: 11px; letter-spacing: 1px; text-transform: uppercase; color: #888; border: none; }
</style>