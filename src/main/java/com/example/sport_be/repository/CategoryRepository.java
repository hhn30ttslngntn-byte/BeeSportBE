package com.example.sport_be.repository;

import com.example.sport_be.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    @Query("SELECT new map(c.id as id, c.name as name, c.status as status) FROM Category c")
    List<Map<String, Object>> getAllCategoriesCustom();

    @Query("SELECT new map(c.id as id, c.name as name, c.status as status) FROM Category c WHERE c.id = :id")
    Map<String, Object> getCategoryByIdCustom(@Param("id") Integer id);
}
