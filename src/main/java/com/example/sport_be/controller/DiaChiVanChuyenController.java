package com.example.sport_be.controller;

import com.example.sport_be.DTO.DiaChiVanChuyenDTO;
import com.example.sport_be.service.DiaChiVanChuyenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dia-chi-van-chuyen")
@RequiredArgsConstructor
public class DiaChiVanChuyenController {

    private final DiaChiVanChuyenService service;

    @PostMapping
    public DiaChiVanChuyenDTO create(@RequestBody DiaChiVanChuyenDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<DiaChiVanChuyenDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DiaChiVanChuyenDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/user/{idNguoiDung}")
    public List<DiaChiVanChuyenDTO> getByUser(@PathVariable Integer idNguoiDung) {
        return service.getByUser(idNguoiDung);
    }

    @PutMapping("/{id}")
    public DiaChiVanChuyenDTO update(@PathVariable Integer id,
                                     @RequestBody DiaChiVanChuyenDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}