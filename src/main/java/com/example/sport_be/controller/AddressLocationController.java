package com.example.sport_be.controller;

import com.example.sport_be.DTO.HuyenDTO;
import com.example.sport_be.DTO.TinhDTO;
import com.example.sport_be.DTO.XaDTO;
import com.example.sport_be.service.AddressLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class AddressLocationController {

    private final AddressLocationService addressLocationService;

    @GetMapping("/provinces")
    public List<TinhDTO> getProvinces() {
        return addressLocationService.getAllTinh();
    }

    @GetMapping("/districts/{provinceId}")
    public List<HuyenDTO> getDistricts(@PathVariable Integer provinceId) {
        return addressLocationService.getHuyenByTinh(provinceId);
    }

    @GetMapping("/wards/{districtId}")
    public List<XaDTO> getWards(@PathVariable Integer districtId) {
        return addressLocationService.getXaByHuyen(districtId);
    }
}
