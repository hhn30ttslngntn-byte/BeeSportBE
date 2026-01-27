<template>
  <div class="container py-5">
    <div class="row g-5">
      <div class="col-lg-6">
        <h2 class="fw-bold mb-4">Liên Hệ Với Chúng Tôi</h2>
        <p class="text-secondary mb-5">Nếu bạn có thắc mắc hoặc cần hỗ trợ, vui lòng để lại lời nhắn.</p>
        
        <form @submit.prevent="handleSubmit">
          <div class="mb-3">
            <label class="form-label small fw-bold">HỌ VÀ TÊN</label>
            <input type="text" class="form-control rounded-0 shadow-none border-secondary" required>
          </div>
          <div class="mb-3">
            <label class="form-label small fw-bold">EMAIL</label>
            <input type="email" class="form-control rounded-0 shadow-none border-secondary" required>
          </div>
          <div class="mb-3">
            <label class="form-label small fw-bold">SỐ ĐIỆN THOẠI</label>
            <input type="tel" class="form-control rounded-0 shadow-none border-secondary">
          </div>
          <div class="mb-4">
            <label class="form-label small fw-bold">NỘI DUNG</label>
            <textarea class="form-control rounded-0 shadow-none border-secondary" rows="5" required></textarea>
          </div>
          <button type="submit" class="btn btn-dark rounded-0 px-5 py-2 fw-bold">GỬI LỜI NHẮN</button>
        </form>
      </div>
      
      <div class="col-lg-6">
        <div class="ps-lg-5">
          <h4 class="fw-bold mb-4">Thông Tin Cửa Hàng</h4>
          <div class="mb-4">
            <p class="mb-1 fw-bold">Địa chỉ:</p>
            <p class="text-secondary">Tòa nhà BeeSport, Quận 1, TP. Hồ Chí Minh</p>
          </div>
          <div class="mb-4">
            <p class="mb-1 fw-bold">Hotline:</p>
            <p class="text-secondary">+84 363652758</p>
          </div>
          <div class="mb-4">
            <p class="mb-1 fw-bold">Email:</p>
            <p class="text-secondary">beesport@gmail.com</p>
          </div>
          
          <div class="ratio ratio-16x9 border shadow-sm mt-5">
            <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.460232428328!2d106.6970724!3d10.7756586!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f385570472f%3A0x118ad94169618a!2zRGluaCDEkOG7mWMgTOG6rXA!5e0!3m2!1svi!2s!4v1700000000000" style="border:0;" allowfullscreen="" loading="lazy"></iframe>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const handleSubmit = () => {
  alert('Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất.');
};
</script>

<style scoped>
.form-control:focus {
  border-color: #000 !important;
}
</style>
