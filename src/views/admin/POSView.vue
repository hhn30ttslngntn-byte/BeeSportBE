<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';

// 1. Quản lý trạng thái thời gian
const currentTime = ref(new Date());
let timer;

onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = new Date();
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timer);
});

const formatDateTime = (date) => {
  return date.toLocaleString('vi-VN');
};

// 2. Dữ liệu mẫu
const products = ref([
  { id: 1, name: 'Giày Nike Zoom Pegasus 40', price: 3200000, img: 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/60f47e3a-c852-45e0-94c6-e9185a676a08/air-zoom-pegasus-40-road-running-shoes-6S2dzM.png' },
  { id: 2, name: 'Áo Adidas Manchester United', price: 1850000, img: 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/71e05d04849a46458514af3d0092289c_9366/Ao_Dau_San_Nha_Manchester_United_23-24_DJỏ_IP1735_01_laydown.jpg' },
  { id: 3, name: 'Quả Bóng Động Lực FIFA Quality', price: 950000, img: 'https://dongluc.vn/images/products/2023/04/original/qua-bong-da-so-5-dong-luc-uhv-2-07-dat-tieu-chuan-fifa-quality-pro-1_1681721528.jpg' },
  { id: 4, name: 'Vợt Cầu Lông Yonex Astrox 88D', price: 4200000, img: 'https://shopvnb.com/uploads/gallery/vot-cau-long-yonex-astrox-88d-pro-chinh-hang.jpg' },
]);

const customers = ref([
  { id: 1, name: 'Nguyễn Văn A', phone: '0987654321', type: 'Hội viên' },
  { id: 2, name: 'Trần Thị B', phone: '0123456789', type: 'Hội viên' },
  { id: 3, name: 'Lê Văn C', phone: '0909123456', type: 'Khách lẻ' },
]);

const vouchers = ref([
  { id: 1, code: 'BEESPORT10', discount: 10, type: 'percent', minOrder: 500000 },
  { id: 2, code: 'GIAM50K', discount: 50000, type: 'fixed', minOrder: 300000 },
]);

// 3. Quản lý hóa đơn
const invoices = ref([]);

const activeInvoiceIndex = ref(0);
const activeInvoice = computed(() => invoices.value[activeInvoiceIndex.value] || null);

const addInvoice = () => {
  if (invoices.value.length >= 5) {
    alert('Chỉ có thể tạo tối đa 5 hóa đơn chờ!');
    return;
  }
  const newInvoice = {
    code: 'HD' + Date.now(),
    cart: [],
    customer: null,
    voucher: null,
    createdAt: new Date()
  };
  invoices.value.push(newInvoice);
  activeInvoiceIndex.value = invoices.value.length - 1;
};

const removeInvoice = (index) => {
  invoices.value.splice(index, 1);
  if (activeInvoiceIndex.value >= invoices.value.length) {
    activeInvoiceIndex.value = Math.max(0, invoices.value.length - 1);
  }
};

// 4. Logic Giỏ hàng
const addToCart = (product) => {
  if (!activeInvoice.value) return;
  const item = activeInvoice.value.cart.find(i => i.id === product.id);
  if (item) {
    item.quantity++;
  } else {
    activeInvoice.value.cart.push({ ...product, quantity: 1 });
  }
  search.value = ''; // Xóa tìm kiếm sau khi thêm
};

const removeFromCart = (index) => {
  activeInvoice.value.cart.splice(index, 1);
};

const subTotal = computed(() => {
  return activeInvoice.value.cart.reduce((acc, item) => acc + (item.price * item.quantity), 0);
});

const discountAmount = computed(() => {
  const voucher = activeInvoice.value.voucher;
  if (!voucher || subTotal.value < voucher.minOrder) return 0;
  
  if (voucher.type === 'percent') {
    return (subTotal.value * voucher.discount) / 100;
  } else {
    return voucher.discount;
  }
});

const total = computed(() => {
  return Math.max(0, subTotal.value - discountAmount.value);
});

// 5. Tìm kiếm
const search = ref('');
const filteredProducts = computed(() => {
  return products.value.filter(p => p.name.toLowerCase().includes(search.value.toLowerCase()));
});

const customerSearch = ref('');
const filteredCustomers = computed(() => {
  if (!customerSearch.value) return [];
  return customers.value.filter(c => 
    c.name.toLowerCase().includes(customerSearch.value.toLowerCase()) || 
    c.phone.includes(customerSearch.value)
  );
});

const selectCustomer = (customer) => {
  activeInvoice.value.customer = customer;
  customerSearch.value = '';
};

const selectVoucher = (voucher) => {
  if (subTotal.value < voucher.minOrder) {
    alert(`Đơn hàng tối thiểu ${voucher.minOrder.toLocaleString()} đ để áp dụng mã này!`);
    return;
  }
  activeInvoice.value.voucher = voucher;
};
</script>

<template>
  <div class="pos-view container-fluid p-4">
    <!-- Màn hình trống khi chưa có hóa đơn -->
    <div v-if="invoices.length === 0" class="d-flex flex-column align-items-center justify-content-center" style="min-height: 70vh;">
      <div class="text-center mb-4">
        <i class="fas fa-file-invoice fa-5x text-secondary opacity-25 mb-3"></i>
        <h3 class="fw-bold text-secondary">Chưa có hóa đơn nào được tạo</h3>
        <p class="text-muted">Vui lòng tạo hóa đơn mới để bắt đầu bán hàng</p>
      </div>
      <button class="btn btn-danger btn-lg rounded-pill px-5 py-3 shadow-lg fw-bold" @click="addInvoice">
        <i class="fas fa-plus me-2"></i>TẠO HÓA ĐƠN MỚI
      </button>
    </div>

    <!-- Giao diện bán hàng khi đã có ít nhất 1 hóa đơn -->
    <template v-else>
      <!-- Hàng trên cùng: Quản lý hóa đơn chờ -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card border-0 shadow-sm rounded-4">
            <div class="card-body p-3 d-flex align-items-center flex-wrap gap-2">
              <div 
                v-for="(inv, index) in invoices" 
                :key="inv.code"
                class="invoice-tab d-flex align-items-center gap-2 px-3 py-2 rounded-3 border transition-all pointer"
                :class="activeInvoiceIndex === index ? 'bg-danger text-white border-danger shadow' : 'bg-light border-light'"
                @click="activeInvoiceIndex = index"
              >
                <span class="small fw-bold">Hóa đơn {{ index + 1 }}</span>
                <i class="fas fa-times small opacity-50 hover-opacity-100" @click.stop="removeInvoice(index)"></i>
              </div>
              <button class="btn btn-outline-danger btn-sm rounded-circle shadow-sm" @click="addInvoice" v-if="invoices.length < 5">
                <i class="fas fa-plus"></i>
              </button>
              <div class="ms-auto d-flex align-items-center text-muted">
                <i class="far fa-clock me-2"></i>
                <span class="small fw-bold">{{ formatDateTime(currentTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-4">
        <!-- Cột bên trái: Tìm kiếm sản phẩm -->
        <div class="col-lg-7 col-xl-8">
          <div class="card border-0 shadow-sm rounded-4 h-100">
            <div class="card-body p-4">
              <div class="input-group mb-4 rounded-pill overflow-hidden border bg-light shadow-sm">
                <span class="input-group-text bg-transparent border-0 ps-3"><i class="fas fa-barcode text-muted"></i></span>
                <input 
                  type="text" 
                  v-model="search" 
                  class="form-control bg-transparent border-0 shadow-none py-2" 
                  placeholder="Tìm kiếm sản phẩm (Tên, Mã, Barcode) hoặc quét mã..."
                  autofocus
                >
              </div>
              
              <!-- Danh sách kết quả tìm kiếm -->
              <div class="search-results">
                <div v-if="!search" class="text-center py-5">
                  <i class="fas fa-search fa-3x text-light mb-3"></i>
                  <p class="text-muted">Nhập tên sản phẩm hoặc quét mã barcode để tìm kiếm</p>
                </div>
                <div v-else-if="filteredProducts.length === 0" class="text-center py-5">
                  <i class="fas fa-exclamation-circle fa-3x text-light mb-3"></i>
                  <p class="text-muted">Không tìm thấy sản phẩm nào phù hợp</p>
                </div>
                <div v-else class="table-responsive">
                  <table class="table table-hover align-middle border-top">
                    <thead class="bg-light">
                      <tr class="small text-secondary">
                        <th class="border-0">SẢN PHẨM</th>
                        <th class="border-0">MÃ SP</th>
                        <th class="border-0">GIÁ BÁN</th>
                        <th class="border-0 text-center">THAO TÁC</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="product in filteredProducts" :key="product.id" class="pointer" @click="addToCart(product)">
                        <td>
                          <div class="d-flex align-items-center">
                            <img :src="product.img" width="50" height="50" class="rounded object-fit-cover me-3 border">
                            <span class="fw-bold small text-dark">{{ product.name }}</span>
                          </div>
                        </td>
                        <td><span class="badge bg-light text-dark border small">SP00{{ product.id }}</span></td>
                        <td><span class="text-danger fw-bold">{{ product.price.toLocaleString() }} đ</span></td>
                        <td class="text-center">
                          <button class="btn btn-danger btn-sm rounded-pill px-3">
                            <i class="fas fa-plus me-1"></i> Thêm
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Cột bên phải: Chi tiết hóa đơn & Thanh toán -->
        <div class="col-lg-5 col-xl-4" v-if="activeInvoice">
        <div class="card border-0 shadow-sm rounded-4 sticky-top" style="top: 20px;">
          <div class="card-header bg-white border-0 pt-4 px-4 pb-0 d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold"><i class="fas fa-receipt me-2 text-danger"></i>HÓA ĐƠN</h5>
            <span class="badge bg-light text-dark border fw-normal">{{ activeInvoice.code }}</span>
          </div>

          <div class="card-body p-4">
            <!-- Thông tin khách hàng -->
            <div class="customer-section mb-4 p-3 bg-light rounded-3">
              <div v-if="!activeInvoice.customer">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="small fw-bold text-secondary">KHÁCH HÀNG</span>
                  <span class="badge bg-secondary-subtle text-secondary">Khách lẻ</span>
                </div>
                <div class="position-relative">
                  <div class="input-group input-group-sm rounded-pill border overflow-hidden bg-white shadow-sm">
                    <span class="input-group-text bg-white border-0"><i class="fas fa-user-plus text-muted"></i></span>
                    <input 
                      type="text" 
                      v-model="customerSearch" 
                      class="form-control border-0 shadow-none" 
                      placeholder="Tìm khách hàng (Tên, SĐT)..."
                    >
                  </div>
                  <!-- Dropdown kết quả tìm kiếm khách hàng -->
                  <div v-if="filteredCustomers.length > 0" class="position-absolute w-100 mt-1 shadow-lg rounded-3 border bg-white overflow-hidden" style="z-index: 1000;">
                    <div 
                      v-for="c in filteredCustomers" 
                      :key="c.id" 
                      class="p-2 border-bottom hover-bg-light pointer d-flex justify-content-between align-items-center"
                      @click="selectCustomer(c)"
                    >
                      <div>
                        <div class="fw-bold small">{{ c.name }}</div>
                        <div class="text-muted small" style="font-size: 11px;">{{ c.phone }}</div>
                      </div>
                      <span class="badge bg-danger-subtle text-danger" style="font-size: 10px;">{{ c.type }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="d-flex align-items-center justify-content-between">
                <div class="d-flex align-items-center">
                  <div class="avatar-sm me-3">{{ activeInvoice.customer.name.charAt(0) }}</div>
                  <div>
                    <div class="fw-bold text-dark">{{ activeInvoice.customer.name }}</div>
                    <div class="text-muted small">{{ activeInvoice.customer.phone }}</div>
                  </div>
                </div>
                <button class="btn btn-sm btn-light rounded-circle" @click="activeInvoice.customer = null">
                  <i class="fas fa-times text-muted"></i>
                </button>
              </div>
            </div>

            <!-- Giỏ hàng -->
            <div class="cart-section mb-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="small fw-bold text-secondary">DANH SÁCH SẢN PHẨM</span>
                <span class="badge bg-danger rounded-pill">{{ activeInvoice.cart.length }}</span>
              </div>
              <div class="cart-items" style="max-height: 280px; overflow-y: auto;">
                <div v-if="activeInvoice.cart.length === 0" class="text-center py-5 text-secondary opacity-50">
                  <i class="fas fa-shopping-basket fa-2x mb-2"></i>
                  <p class="small mb-0">Chưa có sản phẩm nào</p>
                </div>
                <div v-for="(item, index) in activeInvoice.cart" :key="item.id" class="d-flex align-items-center mb-3 p-2 rounded-3 bg-white border border-light shadow-sm">
                  <img :src="item.img" width="45" height="45" class="rounded object-fit-cover me-3">
                  <div class="flex-grow-1 min-width-0">
                    <p class="small mb-0 fw-bold text-truncate">{{ item.name }}</p>
                    <p class="small text-danger mb-0 fw-bold">{{ item.price.toLocaleString() }} đ</p>
                  </div>
                  <div class="d-flex align-items-center ms-2">
                    <div class="input-group input-group-sm" style="width: 80px;">
                      <button class="btn btn-light border p-0" style="width: 24px; height: 24px;" @click="item.quantity > 1 ? item.quantity-- : removeFromCart(index)">
                        <i class="fas" :class="item.quantity > 1 ? 'fa-minus' : 'fa-trash-alt text-danger'" style="font-size: 10px;"></i>
                      </button>
                      <input type="text" class="form-control border-0 bg-transparent text-center p-0 fw-bold" style="font-size: 12px;" :value="item.quantity" readonly>
                      <button class="btn btn-light border p-0" style="width: 24px; height: 24px;" @click="item.quantity++">
                        <i class="fas fa-plus" style="font-size: 10px;"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Voucher & Khuyến mại -->
            <div class="voucher-section mb-4">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="small fw-bold text-secondary">VOUCHER / KHUYẾN MẠI</span>
              </div>
              <div class="dropdown">
                <button 
                  class="btn btn-light btn-sm w-100 rounded-3 text-start d-flex justify-content-between align-items-center border shadow-sm"
                  type="button"
                  data-bs-toggle="dropdown"
                >
                  <span v-if="!activeInvoice.voucher" class="text-muted small"><i class="fas fa-ticket-alt me-2"></i>Chọn voucher áp dụng</span>
                  <span v-else class="text-danger fw-bold small"><i class="fas fa-ticket-alt me-2"></i>{{ activeInvoice.voucher.code }}</span>
                  <i class="fas fa-chevron-down small opacity-50"></i>
                </button>
                <ul class="dropdown-menu w-100 shadow-lg border-0 rounded-3 mt-1">
                  <li v-for="v in vouchers" :key="v.id">
                    <a class="dropdown-item p-3" href="#" @click.prevent="selectVoucher(v)">
                      <div class="d-flex justify-content-between">
                        <span class="fw-bold text-danger">{{ v.code }}</span>
                        <span class="badge bg-danger-subtle text-danger">-{{ v.type === 'percent' ? v.discount + '%' : v.discount.toLocaleString() + 'đ' }}</span>
                      </div>
                      <div class="text-muted" style="font-size: 11px;">Đơn tối thiểu: {{ v.minOrder.toLocaleString() }}đ</div>
                    </a>
                  </li>
                  <li v-if="activeInvoice.voucher">
                    <hr class="dropdown-divider">
                    <a class="dropdown-item text-center text-muted small py-2" href="#" @click.prevent="activeInvoice.voucher = null">Hủy áp dụng</a>
                  </li>
                </ul>
              </div>
            </div>

            <!-- Tổng kết thanh toán -->
            <div class="summary-section border-top pt-4">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-secondary small">Tạm tính:</span>
                <span class="fw-bold">{{ subTotal.toLocaleString() }} đ</span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span class="text-secondary small">Giảm giá:</span>
                <span class="text-success fw-bold">-{{ discountAmount.toLocaleString() }} đ</span>
              </div>
              <div class="d-flex justify-content-between mb-4">
                <span class="h5 fw-bold mb-0">Tổng tiền:</span>
                <span class="h5 fw-bold text-danger mb-0">{{ total.toLocaleString() }} đ</span>
              </div>
              <button class="btn btn-danger w-100 py-3 fw-bold rounded-4 shadow btn-checkout" :disabled="activeInvoice.cart.length === 0">
                XÁC NHẬN THANH TOÁN
              </button>
            </div>
          </div>
        </div>
      </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.pos-view {
  background-color: #f8f9fa;
  min-height: 100vh;
}

.invoice-tab {
  cursor: pointer;
  min-width: 120px;
  transition: all 0.2s ease;
}

.invoice-tab:hover {
  background-color: #f1f3f5;
}

.invoice-tab.bg-danger:hover {
  background-color: #dc3545;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(220, 53, 69, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: 0.3s;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.avatar-sm {
  width: 36px;
  height: 36px;
  background: #343a40;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: bold;
}

.hover-bg-light:hover {
  background-color: #f8f9fa;
}

.pointer {
  cursor: pointer;
}

.btn-checkout {
  transition: all 0.3s;
  letter-spacing: 1px;
}

.btn-checkout:not(:disabled):hover {
  transform: scale(1.02);
  box-shadow: 0 5px 15px rgba(220, 53, 69, 0.4);
}

.cart-items::-webkit-scrollbar {
  width: 4px;
}

.cart-items::-webkit-scrollbar-thumb {
  background: #dee2e6;
  border-radius: 10px;
}

.transition-all {
  transition: all 0.2s;
}

.hover-opacity-100:hover {
  opacity: 1 !important;
}
</style>
