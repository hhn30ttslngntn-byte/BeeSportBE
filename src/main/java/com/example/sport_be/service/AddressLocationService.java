package com.example.sport_be.service;

import com.example.sport_be.DTO.HuyenDTO;
import com.example.sport_be.DTO.TinhDTO;
import com.example.sport_be.DTO.XaDTO;

import java.util.List;

public interface AddressLocationService {

    List<TinhDTO> getAllTinh();

    List<HuyenDTO> getHuyenByTinh(Integer tinhId);

    List<XaDTO> getXaByHuyen(Integer huyenId);

}
