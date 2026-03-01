package com.example.sport_be.repository;

import com.example.sport_be.model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    List<DanhMuc> findByTrangThai(Boolean trangThai);

}
