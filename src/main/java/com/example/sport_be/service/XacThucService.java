package com.example.sport_be.service;

import com.example.sport_be.DTO.XacThucRequest;
import com.example.sport_be.DTO.XacThucResponse;
import com.example.sport_be.model.XacThuc;
import com.example.sport_be.repository.NguoiDungRepository;
import com.example.sport_be.repository.XacThucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XacThucService {
    private final XacThucRepository repository;
    private final NguoiDungRepository nguoiDungRepository;

    public XacThucResponse create(XacThucRequest request) {

        XacThuc entity = new XacThuc();
        entity.setLoaiXacThuc(request.getLoaiXacThuc());
        entity.setTrangThai(true);
        entity.setNguoiDung(
                nguoiDungRepository.findById(request.getIdNguoiDung())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );

        return mapToResponse(repository.save(entity));
    }

    private XacThucResponse mapToResponse(XacThuc entity) {
        XacThucResponse res = new XacThucResponse();
        res.setId(entity.getId());
        res.setLoaiXacThuc(entity.getLoaiXacThuc());
        res.setTrangThai(entity.getTrangThai());
        res.setIdNguoiDung(entity.getNguoiDung().getId());
        return res;
    }
}
