package com.example.sport_be.controller;

import com.example.sport_be.DTO.NguoiDungRequest;
import com.example.sport_be.DTO.NguoiDungResponse;
import com.example.sport_be.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public List<NguoiDungResponse> getAllStaff() {
        return staffService.getAllStaff();
    }

    @PostMapping
    public NguoiDungResponse createStaff(@Valid @RequestBody NguoiDungRequest request) {
        return staffService.createStaff(request);
    }

    @PutMapping("/{id}")
    public NguoiDungResponse updateStaff(@PathVariable Integer id,
                                         @Valid @RequestBody NguoiDungRequest request) {
        return staffService.updateStaff(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
    }
}
