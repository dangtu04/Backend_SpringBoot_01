package com.dangthanhtu.example05.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangthanhtu.example05.entity.Brand;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Long> {
}