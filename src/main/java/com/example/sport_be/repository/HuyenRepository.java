package com.example.sport_be.repository;

import com.example.sport_be.model.Huyen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HuyenRepository extends JpaRepository<Huyen, Integer> {

    List<Huyen> findByTinh_Id(Integer idTinh);

}
