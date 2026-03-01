package com.example.sport_be.repository;

import com.example.sport_be.model.DoiTra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoiTraRepository extends JpaRepository<DoiTra, Integer> {

    List<DoiTra> findByHoaDon_Id(Integer idHoaDon);

}
