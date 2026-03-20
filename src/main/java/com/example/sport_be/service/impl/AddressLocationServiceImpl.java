package com.example.sport_be.service.impl;

import com.example.sport_be.DTO.HuyenDTO;
import com.example.sport_be.DTO.TinhDTO;
import com.example.sport_be.DTO.XaDTO;
import com.example.sport_be.repository.DistrictRepository;
import com.example.sport_be.repository.ProvinceRepository;
import com.example.sport_be.repository.WardRepository;
import com.example.sport_be.service.AddressLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressLocationServiceImpl implements AddressLocationService {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @Override
    public List<TinhDTO> getAllTinh() {

        return provinceRepository.findAll()
                .stream()
                .map(t -> new TinhDTO(
                        t.getId(),
                        t.getTenTinh(),
                        t.getTrangThai()
                ))
                .toList();
    }

    @Override
    public List<HuyenDTO> getHuyenByTinh(Integer tinhId) {

        return districtRepository.findByTinh_Id(tinhId)
                .stream()
                .map(h -> new HuyenDTO(
                        h.getId(),
                        h.getTinh().getId(),
                        h.getTenHuyen(),
                        h.getTrangThai()
                ))
                .toList();
    }

    @Override
    public List<XaDTO> getXaByHuyen(Integer huyenId) {

        return wardRepository.findByHuyen_Id(huyenId)
                .stream()
                .map(x -> new XaDTO(
                        x.getId(),
                        x.getHuyen().getId(),
                        x.getTenXa(),
                        x.getTrangThai()
                ))
                .toList();
    }
}
