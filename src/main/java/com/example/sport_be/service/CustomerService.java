package com.example.sport_be.service;

import com.example.sport_be.DTO.NguoiDungResponse;

import java.util.List;

public interface CustomerService {

    List<NguoiDungResponse> getAllCustomers();

    NguoiDungResponse getCustomerById(Integer id);
}
