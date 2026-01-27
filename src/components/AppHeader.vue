

<template>
  <nav class="navbar navbar-expand-lg navbar-light sticky-top main-header p-0">
    <div class="container-fluid px-lg-5 d-flex align-items-center justify-content-between">
      
      <!-- 1. LOGO (Bên trái) -->
      <div class="header-left">
        <router-link class="navbar-brand m-0 p-0" to="/">
          <img src="/src/img/logo2.jpg" alt="BeeSport" class="logo-img">
        </router-link>
      </div>

      <!-- 2. MENU CHÍNH (Ở giữa - Ép cùng hàng) -->
      <div class="header-center d-none d-lg-block">
        <ul class="nav-menu-list m-0 p-0">
          <li>
            <router-link class="menu-link" to="/">TRANG CHỦ</router-link>
          </li>
          
          <!-- SẢN PHẨM CÓ HOVER -->
          <li class="has-submenu">
            <router-link class="menu-link" to="/products">
              SẢN PHẨM 
            </router-link>
            <!-- Menu con hiện ra khi hover -->
            <ul class="submenu-box shadow-lg">
              <li><router-link to="/products">Chạy bộ</router-link></li>
              <li><router-link to="/products">Cầu lông</router-link></li>
              <li><router-link to="/products">Đá bóng</router-link></li>
            </ul>
          </li>

          <li>
            <router-link class="menu-link" to="/about">GIỚI THIỆU</router-link>
          </li>
          <li>
            <router-link class="menu-link" to="/contact">LIÊN HỆ</router-link>
          </li>
        </ul>
      </div>

      <!-- 3. SEARCH & ICONS (Bên phải) -->
      <div class="header-right d-flex align-items-center gap-3">
        <div class="search-box d-none d-xl-flex">
          <input type="text" placeholder="Tìm kiếm...">
          <i class="fas fa-search"></i>
        </div>
        <div class="icon-group d-flex align-items-center gap-3 gap-lg-4">
          <router-link to="/account" class="icon-item"><i class="far fa-user"></i></router-link>
          <router-link to="/wishlist" class="icon-item d-none d-sm-block"><i class="far fa-heart"></i></router-link>
          <router-link to="/cart" class="icon-item position-relative">
            <i class="fas fa-shopping-bag"></i>
            <span class="cart-badge">0</span>
          </router-link>
        </div>
      </div>

    </div>
  </nav>
</template>

<style scoped>
/* KHUNG HEADER */
.main-header {
  height: 90px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.05);
  background: #305a78;
}

.logo-img {
  height: 80px; /* Logo to rõ nét */
  width: auto;
  display: block;
  margin-left: 50px;
}

/* MENU CHÍNH */
.nav-menu-list {
  display: flex;
  list-style: none;
  align-items: center;
  gap: 30px; /* Khoảng cách giữa các mục menu */
}

.menu-link {
  text-decoration: none;
  color: #fff !important;
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.5px;
  white-space: nowrap; /* QUAN TRỌNG: Ngăn chữ xuống dòng */
  padding: 10px 0;
  display: block;
  transition: 0.3s;
}

.menu-link:hover {
  color: #dc3545 !important; /* Đổi màu khi hover */
}

/* HIỆU ỨNG HOVER SẢN PHẨM */
.has-submenu {
  position: relative;
}

.submenu-box {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(15px);
  background: #fff;
  min-width: 200px;
  list-style: none;
  padding: 15px 0;
  border-radius: 8px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 1000;
}

/* Hiện submenu và đổi màu link cha khi hover */
.has-submenu:hover .submenu-box {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.has-submenu:hover .menu-link {
  color: #dc3545 !important;
}

.submenu-box li a {
  display: block;
  padding: 10px 25px;
  color: #444;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: 0.2s;
  text-align: left;
}

.submenu-box li a:hover {
  background: #f8f9fa;
  color: #dc3545;
  padding-left: 30px;
}

/* THANH SEARCH & ICONS */
.search-box {
  background: #f4f4f4;
  border-radius: 50px;
  padding: 7px 18px;
  display: flex;
  align-items: center;
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 12px;
  width: 140px;
}

.icon-item {
  color: #fff;
  font-size: 20px;
  text-decoration: none;
  transition: 0.3s;
}

.icon-item:hover { color: #dc3545; transform: translateY(-2px); }

.cart-badge {
  position: absolute;
  top: -8px;
  right: -10px;
  background: #dc3545;
  color: #fff;
  font-size: 9px;
  padding: 2px 6px;
  border-radius: 50px;
}

.icon-small { font-size: 10px; }

/* RESPONSIVE */
@media (max-width: 991px) {
  .main-header { height: 75px; }
  .logo-img { height: 45px; }
}
</style>