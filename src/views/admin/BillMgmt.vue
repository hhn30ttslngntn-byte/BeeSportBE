<script setup>
import { ref } from 'vue';

const bills = ref([
  { id: 'HD001', customer: 'Nguyễn Văn A', date: '2024-01-20', total: 1500000, status: 'Đã thanh toán' },
  { id: 'HD002', customer: 'Trần Thị B', date: '2024-01-21', total: 500000, status: 'Chờ thanh toán' },
  { id: 'HD003', customer: 'Lê Văn C', date: '2024-01-21', total: 300000, status: 'Đã hủy' },
]);

const getStatusBadge = (status) => {
  switch (status) {
    case 'Đã thanh toán': return 'bg-success';
    case 'Chờ thanh toán': return 'bg-warning text-dark';
    case 'Đã hủy': return 'bg-danger';
    default: return 'bg-secondary';
  }
};
</script>

<template>
  <div class="bill-mgmt">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Quản Lý Hóa Đơn</h4>
    </div>

    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr>
                <th class="ps-4">Mã HĐ</th>
                <th>Khách Hàng</th>
                <th>Ngày Lập</th>
                <th>Tổng Tiền</th>
                <th>Trạng Thái</th>
                <th class="text-end pe-4">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="bill in bills" :key="bill.id">
                <td class="ps-4 fw-bold text-danger">{{ bill.id }}</td>
                <td>{{ bill.customer }}</td>
                <td>{{ bill.date }}</td>
                <td>{{ bill.total.toLocaleString() }} VNĐ</td>
                <td><span :class="['badge rounded-pill px-3', getStatusBadge(bill.status)]">{{ bill.status }}</span></td>
                <td class="text-end pe-4">
                  <button class="btn btn-sm btn-outline-dark me-2"><i class="fas fa-eye"></i></button>
                  <button class="btn btn-sm btn-outline-primary"><i class="fas fa-print"></i></button>
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
