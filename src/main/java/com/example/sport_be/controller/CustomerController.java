package com.example.sport_be.controller;

import com.example.sport_be.DTO.NguoiDungResponse;
import com.example.sport_be.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<NguoiDungResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public NguoiDungResponse getCustomer(@PathVariable Integer id) {
        return customerService.getCustomerById(id);
    }
}
