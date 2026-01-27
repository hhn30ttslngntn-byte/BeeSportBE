<template>
  <div class="container py-5">
    <div class="row g-4">
      <!-- 1. Sidebar Filters -->
      <div class="col-lg-3">
        <div class="filter-sidebar p-4 bg-white rounded-4 shadow-sm sticky-top" style="top: 100px;">
          <h5 class="fw-bold mb-4">BỘ LỌC</h5>
          
          <!-- Lọc theo Giá (Đã kết nối logic) -->
          <div class="mb-4">
            <h6 class="fw-bold small mb-3 text-uppercase">Giá tối đa: <span class="text-danger">{{ formatPrice(maxPrice) }}</span></h6>
            <input type="range" class="form-range custom-range" min="0" max="2000000" step="50000" v-model="maxPrice">
            <div class="d-flex justify-content-between small text-secondary mt-2">
              <span>0đ</span>
              <span>2.000.000đ</span>
            </div>
          </div>

          <!-- Lọc theo Danh mục -->
          <div class="mb-4">
            <h6 class="fw-bold small mb-3 text-uppercase">Danh mục</h6>
            <div class="form-check mb-2" v-for="cat in categories" :key="cat.id">
              <input class="form-check-input shadow-none" type="checkbox" :id="'cat'+cat.id" :value="cat.name" v-model="selectedCats">
              <label class="form-check-label small" :for="'cat'+cat.id">{{ cat.name }}</label>
            </div>
          </div>

          <button class="btn btn-dark w-100 rounded-pill fw-bold btn-sm" @click="resetFilter">XÓA BỘ LỌC</button>
        </div>
      </div>

      <!-- 2. Product Grid -->
      <div class="col-lg-9">
        <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded-4 shadow-sm">
          <span class="text-secondary small">Tìm thấy <b>{{ filteredProducts.length }}</b> sản phẩm</span>
          <select class="form-select form-select-sm border-0 shadow-none w-auto fw-bold">
            <option>Mới nhất</option>
          </select>
        </div>

        <!-- Thông báo nếu không tìm thấy SP -->
        <div v-if="paginatedProducts.length === 0" class="text-center py-5">
          <i class="fas fa-search fa-3x text-light mb-3"></i>
          <p class="text-muted">Không tìm thấy sản phẩm nào phù hợp với mức giá này.</p>
        </div>

        <!-- Grid sản phẩm -->
        <div class="row g-4">
          <div class="col-md-3 col-sm-6" v-for="product in paginatedProducts" :key="product.id">
            <div class="product-card card border-0 shadow-sm rounded-4 overflow-hidden h-100">
              <div class="position-relative overflow-hidden img-container">
                <img :src="product.image" class="card-img-top product-img" :alt="product.name">
                <div v-if="product.isNew" class="product-badge bg-danger text-white">MỚI</div>
                <div class="product-actions">
                  <button class="btn btn-white btn-sm rounded-circle shadow-sm me-1"><i class="far fa-heart"></i></button>
                  <button class="btn btn-white btn-sm rounded-circle shadow-sm"><i class="fas fa-shopping-bag"></i></button>
                </div>
              </div>
              <div class="card-body p-3 text-center">
                <h6 class="fw-bold mb-2 product-title">{{ product.name }}</h6>
                <p class="text-danger fw-bold mb-0 small">{{ formatPrice(product.price) }}</p>
              </div>
              <router-link :to="'/product/' + product.id" class="stretched-link"></router-link>
            </div>
          </div>
        </div>

        <!-- PHÂN TRANG -->
        <nav class="mt-5" v-if="totalPages > 1">
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <a class="page-link border-0 shadow-none" href="#" @click.prevent="currentPage--"><i class="fas fa-chevron-left"></i></a>
            </li>
            <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
              <a class="page-link border-0 shadow-none" href="#" @click.prevent="currentPage = page">{{ page }}</a>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <a class="page-link border-0 shadow-none" href="#" @click.prevent="currentPage++"><i class="fas fa-chevron-right"></i></a>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const categories = [
  { id: 1, name: 'Chạy bộ' },
  { id: 2, name: 'Đá bóng' },
  { id: 3, name: 'Cầu lông' },
  { id: 4, name: 'Bơi' },
  { id: 5, name: 'Phụ Kiện' }
];

// DATA: Tạo 12 sản phẩm để demo phân trang 8sp/trang
const allProducts = [
  { id: 1, name: 'Áo Pro Running 01', price: 350000, category: 'Thời Trang Nam', isNew: true, image: 'https://images.unsplash.com/photo-1581009146145-b5ef03a7403f?q=80&w=400' },
  { id: 2, name: 'Quần Short Gym 02', price: 280000, category: 'Thời Trang Nam', isNew: false, image: 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?q=80&w=400' },
  { id: 3, name: 'Giày SpeedX 03', price: 1250000, category: 'Phụ Kiện', isNew: true, image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=400' },
  { id: 4, name: 'Áo Hoodie Nỉ 04', price: 550000, category: 'Thời Trang Nam', isNew: false, image: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=400' },
  { id: 5, name: 'Túi Trống Bee 05', price: 420000, category: 'Phụ Kiện', isNew: false, image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=400' },
  { id: 6, name: 'Bộ Yoga Nữ 06', price: 890000, category: 'Thời Trang Nữ', isNew: true, image: 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=400' },
  { id: 7, name: 'Áo Gió Nam 07', price: 650000, category: 'Thời Trang Nam', isNew: false, image: 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?q=80&w=400' },
  { id: 8, name: 'Bình Nước 08', price: 150000, category: 'Phụ Kiện', isNew: true, image: 'https://images.unsplash.com/photo-1523362628745-0c100150b504?q=80&w=400' },
  { id: 9, name: 'Tất Thể Thao 09', price: 50000, category: 'Phụ Kiện', isNew: false, image: 'https://images.unsplash.com/photo-1582562124811-c09040d0a901?q=80&w=400' },
  { id: 10, name: 'Áo Polo Thể Thao 10', price: 390000, category: 'Thời Trang Nam', isNew: false, image: 'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?q=80&w=400' },
  { id: 11, name: 'Thảm Tập Yoga 11', price: 320000, category: 'Phụ Kiện', isNew: true, image: 'https://images.unsplash.com/photo-1592432676556-381e61846b07?q=80&w=400' },
  { id: 12, name: 'Quần Legging Nữ 12', price: 450000, category: 'Thời Trang Nữ', isNew: false, image: 'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?q=80&w=400' }
];

// --- BIẾN LỌC ---
const maxPrice = ref(2000000);
const selectedCats = ref([]);
const currentPage = ref(1);
const itemsPerPage = 8;

// --- HÀM HỖ TRỢ ---
const formatPrice = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const resetFilter = () => {
  maxPrice.value = 2000000;
  selectedCats.value = [];
  currentPage.value = 1;
};

// --- LOGIC LỌC SẢN PHẨM ---
const filteredProducts = computed(() => {
  return allProducts.filter(p => {
    const matchPrice = p.price <= maxPrice.value;
    const matchCat = selectedCats.value.length === 0 || selectedCats.value.includes(p.category);
    return matchPrice && matchCat;
  });
});

// --- LOGIC PHÂN TRANG ---
const totalPages = computed(() => Math.ceil(filteredProducts.value.length / itemsPerPage));

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return filteredProducts.value.slice(start, start + itemsPerPage);
});

// Reset về trang 1 khi lọc
watch([maxPrice, selectedCats], () => {
  currentPage.value = 1;
});
</script>

<style scoped>
.product-card { transition: 0.3s; background: #fff; }
.product-card:hover { transform: translateY(-8px); box-shadow: 0 10px 25px rgba(0,0,0,0.1) !important; }
.img-container { height: 240px; }
.product-img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.product-card:hover .product-img { transform: scale(1.08); }
.product-actions { position: absolute; bottom: -50px; left: 0; right: 0; text-align: center; transition: 0.3s; opacity: 0; }
.product-card:hover .product-actions { bottom: 15px; opacity: 1; }
.btn-white { background: #fff; border: none; width: 32px; height: 32px; display: inline-flex; align-items: center; justify-content: center; }
.product-badge { position: absolute; top: 10px; left: 10px; font-size: 9px; padding: 2px 8px; border-radius: 50px; font-weight: bold; }
.product-title { font-size: 13px; height: 38px; overflow: hidden; color: #333; }
.custom-range::-webkit-slider-runnable-track { background: #eee; height: 4px; }
.custom-range::-webkit-slider-thumb { background: #dc3545; margin-top: -6px; }
.page-link { border-radius: 50% !important; margin: 0 3px; width: 35px; height: 35px; display: flex; align-items: center; justify-content: center; color: #333; font-size: 13px; }
.page-item.active .page-link { background-color: #dc3545 !important; border: none; }
</style>