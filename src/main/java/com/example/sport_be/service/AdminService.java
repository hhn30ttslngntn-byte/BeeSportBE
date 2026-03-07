package com.example.sport_be.service;

import com.example.sport_be.entity.*;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final DanhMucRepository danhMucRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoaDonRepository hoaDonRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final ChatLieuRepository chatLieuRepository;

    // --- Product ---
    public List<SanPham> getAllProducts() {
        return sanPhamRepository.findAll();
    }

    public SanPham saveProduct(SanPham sanPham) {
        return sanPhamRepository.save(sanPham);
    }

    public void deleteProduct(Integer id) {
        sanPhamRepository.deleteById(id);
    }

    // --- Product Details ---
    public List<SanPhamChiTiet> getAllProductDetails() {
        return sanPhamChiTietRepository.findAll();
    }

    // --- Category ---
    public List<DanhMuc> getAllCategories() {
        return danhMucRepository.findAll();
    }

    public DanhMuc saveCategory(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    // --- User (Staff & Customer) ---
    public List<NguoiDung> getAllUsers() {
        return nguoiDungRepository.findAll();
    }

    public List<NguoiDung> getUsersByRole(Integer roleId) {
        return nguoiDungRepository.findByVaiTroId(roleId);
    }

    // --- Bill ---
    public List<HoaDon> getAllBills() {
        return hoaDonRepository.findAll();
    }

    // --- Voucher ---
    public List<MaGiamGia> getAllVouchers() {
        return maGiamGiaRepository.findAll();
    }

    // --- Attributes ---
    public List<MauSac> getAllColors() { return mauSacRepository.findAll(); }
    public List<KichThuoc> getAllSizes() { return kichThuocRepository.findAll(); }
    public List<ChatLieu> getAllMaterials() { return chatLieuRepository.findAll(); }
}
