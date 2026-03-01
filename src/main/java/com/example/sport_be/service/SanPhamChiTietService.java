package com.example.sport_be.service;

import com.example.sport_be.DTO.SanPhamChiTietRequest;
import com.example.sport_be.DTO.SanPhamChiTietResponse;
import com.example.sport_be.model.SanPhamChiTiet;
import com.example.sport_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SanPhamChiTietService {
    private final SanPhamChiTietRepository repository;
    private final SanPhamRepository sanPhamRepository;
    private final KichThuocRepository kichThuocRepository;
    private final MauSacRepository mauSacRepository;
    private final ChatLieuRepository chatLieuRepository;

    public SanPhamChiTietResponse create(SanPhamChiTietRequest request) {

        SanPhamChiTiet entity = new SanPhamChiTiet();
        entity.setSanPham(
                sanPhamRepository.findById(request.getIdSanPham())
                        .orElseThrow(() -> new RuntimeException("SanPham not found"))
        );
        entity.setKichThuoc(
                kichThuocRepository.findById(request.getIdKichThuoc())
                        .orElseThrow(() -> new RuntimeException("KichThuoc not found"))
        );
        entity.setMauSac(
                mauSacRepository.findById(request.getIdMauSac())
                        .orElseThrow(() -> new RuntimeException("MauSac not found"))
        );
        entity.setChatLieu(
                chatLieuRepository.findById(request.getIdChatLieu())
                        .orElseThrow(() -> new RuntimeException("ChatLieu not found"))
        );

        entity.setSoLuong(request.getSoLuong());
        entity.setGiaBan(request.getGiaBan());
        entity.setTrangThai(true);

        return mapToResponse(repository.save(entity));
    }

    private SanPhamChiTietResponse mapToResponse(SanPhamChiTiet entity) {
        SanPhamChiTietResponse res = new SanPhamChiTietResponse();
        res.setId(entity.getId());
        res.setIdSanPham(entity.getSanPham().getId());
        res.setIdKichThuoc(entity.getKichThuoc().getId());
        res.setIdMauSac(entity.getMauSac().getId());
        res.setIdChatLieu(entity.getChatLieu().getId());
        res.setSoLuong(entity.getSoLuong());
        res.setGiaBan(entity.getGiaBan());
        res.setTrangThai(entity.getTrangThai());
        return res;
    }
}
