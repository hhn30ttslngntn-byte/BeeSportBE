package com.example.sport_be.service;

import com.example.sport_be.DTO.NguoiDungRequest;
import com.example.sport_be.DTO.NguoiDungResponse;

import java.util.List;

public interface UserService {

    List<NguoiDungResponse> getAll();

    NguoiDungResponse getById(Integer id);

    NguoiDungResponse create(NguoiDungRequest request);

    NguoiDungResponse update(Integer id, NguoiDungRequest request);

    void delete(Integer id);
}
