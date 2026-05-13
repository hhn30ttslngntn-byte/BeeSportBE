package com.example.sport_be.service;

import com.example.sport_be.dto.CauHinhDoiTraRequest;
import com.example.sport_be.entity.CauHinhDoiTra;
import com.example.sport_be.repository.CauHinhDoiTraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CauHinhDoiTraService {
    private final CauHinhDoiTraRepository cauHinhDoiTraRepository;
    private final DoiTraService doiTraService;

    public CauHinhDoiTra get() {
        return cauHinhDoiTraRepository.findTopByOrderByIdAsc().orElseGet(() -> {
            CauHinhDoiTra ch = new CauHinhDoiTra();
            ch.setPhiXuLyPhanTram(new BigDecimal("5.00"));
            ch.setPhiShipHoan(new BigDecimal("30000"));
            ch.setSoNgayChoPhep(7);
            return cauHinhDoiTraRepository.save(ch);
        });
    }

    @Transactional
    public CauHinhDoiTra updateConfig(CauHinhDoiTraRequest request) {
        if (request == null) {
            throw new RuntimeException("Thieu du lieu cau hinh");
        }

        CauHinhDoiTra config = get();
        if (request.getPhiXuLyPhanTram() != null) {
            if (request.getPhiXuLyPhanTram().compareTo(BigDecimal.ZERO) < 0
                    || request.getPhiXuLyPhanTram().compareTo(new BigDecimal("100")) > 0) {
                throw new RuntimeException("phiXuLyPhanTram phai trong khoang [0,100]");
            }
            config.setPhiXuLyPhanTram(request.getPhiXuLyPhanTram());
        }

        if (request.getPhiShipHoan() != null) {
            if (request.getPhiShipHoan().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("phiShipHoan khong duoc am");
            }
            config.setPhiShipHoan(request.getPhiShipHoan());
        }

        if (request.getSoNgayChoPhep() != null) {
            if (request.getSoNgayChoPhep() <= 0) {
                throw new RuntimeException("soNgayChoPhep phai > 0");
            }
            config.setSoNgayChoPhep(request.getSoNgayChoPhep());
        }

        CauHinhDoiTra saved = cauHinhDoiTraRepository.save(config);
        doiTraService.reloadCauHinh();
        return saved;
    }
}
