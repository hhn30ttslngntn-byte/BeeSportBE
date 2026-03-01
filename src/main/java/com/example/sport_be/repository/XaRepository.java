package com.example.sport_be.repository;

import com.example.sport_be.model.Xa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XaRepository extends JpaRepository<Xa, Integer> {

    List<Xa> findByHuyen_Id(Integer idHuyen);

}
