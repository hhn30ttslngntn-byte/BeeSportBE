package com.example.sport_be.service;

import com.example.sport_be.DTO.NguoiDungRequest;
import com.example.sport_be.DTO.NguoiDungResponse;

import java.util.List;

public interface StaffService {

    List<NguoiDungResponse> getAllStaff();

    NguoiDungResponse createStaff(NguoiDungRequest request);

    NguoiDungResponse updateStaff(Integer id, NguoiDungRequest request);

    void deleteStaff(Integer id);
}
