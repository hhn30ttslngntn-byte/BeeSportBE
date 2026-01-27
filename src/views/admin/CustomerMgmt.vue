<script setup>
import { ref, computed } from 'vue';

// 1. Dữ liệu mẫu khách hàng (Đã thêm thông tin chi tiết)
const customers = ref([
  { id: 1, name: 'Nguyễn Văn A', email: 'a@gmail.com', phone: '0987654321', orders: 5, status: 'Hoạt động', joinDate: '10/01/2024', address: '123 Cầu Giấy, Hà Nội', totalSpent: '4.500.000đ', dob: '15/05/1995' },
  { id: 2, name: 'Trần Thị B', email: 'b@gmail.com', phone: '0123456789', orders: 2, status: 'Hoạt động', joinDate: '15/02/2024', address: '456 Lê Lợi, TP.HCM', totalSpent: '1.200.000đ', dob: '20/10/1998' },
  { id: 3, name: 'Lê Văn C', email: 'c@gmail.com', phone: '0909123456', orders: 0, status: 'Bị khóa', joinDate: '01/03/2024', address: '789 Trần Hưng Đạo, Đà Nẵng', totalSpent: '0đ', dob: '05/12/1990' },
]);

const searchQuery = ref('');
const selectedCustomer = ref(null); // Lưu khách hàng đang xem chi tiết

// 2. Logic Tìm kiếm
const filteredCustomers = computed(() => {
  return customers.value.filter(c => 
    c.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.phone.includes(searchQuery.value)
  );
});

// 3. Hàm xem chi tiết
const viewDetails = (customer) => {
  selectedCustomer.value = customer;
};

// 4. Hàm thay đổi trạng thái (Toggle)
const toggleStatus = (customer) => {
  const isLocking = customer.status === 'Hoạt động';
  const message = isLocking 
    ? `Bạn có chắc chắn muốn KHÓA tài khoản của ${customer.name}? Khách hàng này sẽ không thể đăng nhập.`
    : `Bạn có muốn MỞ KHÓA cho tài khoản của ${customer.name}?`;

  if (confirm(message)) {
    customer.status = isLocking ? 'Bị khóa' : 'Hoạt động';
  }
};
</script>

<template>
  <div class="customer-mgmt p-3">
    <!-- THỐNG KÊ NHANH -->
    <div class="row g-3 mb-4">
      <div class="col-md-4" v-for="(val, label) in { 'TỔNG KHÁCH': customers.length, 'ĐANG HOẠT ĐỘNG': 2, 'BỊ KHÓA': 1 }" :key="label">
        <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="small fw-bold text-secondary text-uppercase">{{ label }}</div>
          <div class="h3 fw-bold m-0 text-danger">{{ val }}</div>
        </div>
      </div>
    </div>

    <!-- DANH SÁCH KHÁCH HÀNG -->
    <div class="card border-0 shadow-sm rounded-4">
      <div class="card-body p-4">
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
          <h5 class="fw-bold m-0"><i class="fas fa-users me-2"></i>QUẢN LÝ KHÁCH HÀNG</h5>
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input v-model="searchQuery" type="text" placeholder="Tìm tên hoặc SĐT...">
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr class="small text-secondary">
                <th class="border-0 px-3">KHÁCH HÀNG</th>
                <th class="border-0">THÔNG TIN LIÊN HỆ</th>
                <th class="border-0 text-center">TRẠNG THÁI</th>
                <th class="border-0 text-center">THAO TÁC</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="customer in filteredCustomers" :key="customer.id">
                <td class="px-3">
                  <div class="d-flex align-items-center">
                    <div class="avatar-circle me-3">{{ customer.name.charAt(0) }}</div>
                    <div>
                      <div class="fw-bold text-dark">{{ customer.name }}</div>
                      <div class="text-muted small">Ngày tham gia: {{ customer.joinDate }}</div>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="small"><i class="fas fa-phone me-2 text-muted"></i>{{ customer.phone }}</div>
                  <div class="small"><i class="fas fa-envelope me-2 text-muted"></i>{{ customer.email }}</div>
                </td>
                <td class="text-center">
                  <span :class="customer.status === 'Hoạt động' ? 'badge-active' : 'badge-locked'">
                    {{ customer.status }}
                  </span>
                </td>
                <td class="text-center">
                  <div class="d-flex justify-content-center gap-2">
                    <!-- Nút Xem chi tiết -->
                    <button @click="viewDetails(customer)" class="btn-tool btn-view" data-bs-toggle="modal" data-bs-target="#detailModal">
                      <i class="fas fa-eye"></i>
                    </button>
                    <!-- Nút Thay đổi trạng thái -->
                    <button @click="toggleStatus(customer)" class="btn-tool" :class="customer.status === 'Hoạt động' ? 'btn-lock' : 'btn-unlock'">
                      <i class="fas" :class="customer.status === 'Hoạt động' ? 'fa-user-slash' : 'fa-user-check'"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- MODAL CHI TIẾT KHÁCH HÀNG -->
    <div class="modal fade" id="detailModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-0 pb-0">
            <h5 class="fw-bold m-0">Hồ Sơ Khách Hàng</h5>
            <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-4" v-if="selectedCustomer">
            <div class="text-center mb-4">
              <div class="avatar-large mx-auto mb-3">{{ selectedCustomer.name.charAt(0) }}</div>
              <h4 class="fw-bold mb-1">{{ selectedCustomer.name }}</h4>
              <span class="badge bg-danger rounded-pill px-3">Mã: KH00{{ selectedCustomer.id }}</span>
            </div>

            <div class="row g-3">
              <div class="col-6 border-end">
                <label class="small text-muted d-block">Ngày sinh</label>
                <span class="fw-bold">{{ selectedCustomer.dob }}</span>
              </div>
              <div class="col-6 ps-3">
                <label class="small text-muted d-block">Tổng chi tiêu</label>
                <span class="fw-bold text-danger">{{ selectedCustomer.totalSpent }}</span>
              </div>
              <div class="col-12 bg-light p-3 rounded-3 mt-3">
                <div class="mb-2">
                  <i class="fas fa-map-marker-alt text-danger me-2"></i>
                  <span class="small fw-bold">Địa chỉ giao hàng:</span>
                </div>
                <div class="small text-secondary">{{ selectedCustomer.address }}</div>
              </div>
              <div class="col-12 mt-3">
                <label class="small text-muted d-block mb-1 text-uppercase fw-bold" style="font-size: 10px;">Lịch sử hoạt động</label>
                <div class="small">• Đã thực hiện <b>{{ selectedCustomer.orders }}</b> đơn hàng</div>
                <div class="small">• Trạng thái hiện tại: <b :class="selectedCustomer.status === 'Hoạt động' ? 'text-success' : 'text-danger'">{{ selectedCustomer.status }}</b></div>
              </div>
            </div>
          </div>
          <div class="modal-footer border-0">
            <button type="button" class="btn btn-dark rounded-pill px-4" data-bs-dismiss="modal">Đóng</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Thanh tìm kiếm */
.search-box {
  display: flex; align-items: center; background: #f1f3f5;
  padding: 8px 20px; border-radius: 50px; width: 300px;
}
.search-box input { border: none; background: transparent; outline: none; margin-left: 10px; width: 100%; font-size: 14px; }
.search-box i { color: #adb5bd; }

/* Avatar */
.avatar-circle {
  width: 42px; height: 42px; background: #343a40; color: white;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%; font-weight: bold; font-size: 18px;
}
.avatar-large {
  width: 80px; height: 80px; background: #dc3545; color: white;
  display: flex; align-items: center; justify-content: center;
  border-radius: 20px; font-weight: bold; font-size: 32px;
}

/* Badge Trạng thái */
.badge-active { background: #e6fcf5; color: #0ca678; padding: 5px 12px; border-radius: 50px; font-size: 12px; font-weight: 700; }
.badge-locked { background: #fff5f5; color: #f03e3e; padding: 5px 12px; border-radius: 50px; font-size: 12px; font-weight: 700; }

/* Nút công cụ */
.btn-tool {
  width: 36px; height: 36px; border-radius: 10px; border: 1px solid #eee;
  background: white; transition: 0.3s;
}
.btn-view:hover { background: #e7f1ff; color: #0d6efd; border-color: #0d6efd; }
.btn-lock:hover { background: #fff5f5; color: #e03131; border-color: #f03e3e; }
.btn-unlock:hover { background: #ebfbee; color: #2f9e44; border-color: #40c057; }

/* Table */
thead th { font-size: 11px; letter-spacing: 0.5px; }
tr { transition: 0.2s; cursor: default; }
</style>