package com.dangthanhtu.example05.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dangthanhtu.example05.entity.Brand;
import com.dangthanhtu.example05.entity.Category;
import com.dangthanhtu.example05.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    // Page<Product> findByProductNameLike(String keyword, Pageable pageDetails);
     @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByProductNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageDetails);
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByBrand(Brand brand, Pageable pageable);
    Page<Product> findByCategoryAndBrand(Category category, Brand brand, Pageable pageable);
}