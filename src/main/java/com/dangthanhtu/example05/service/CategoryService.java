package com.dangthanhtu.example05.service;

import com.dangthanhtu.example05.entity.Category;
import com.dangthanhtu.example05.payloads.CategoryDTO;
import com.dangthanhtu.example05.payloads.CategoryResponse;

public interface CategoryService {

    CategoryDTO createCategory(Category category);

    CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO getCategoryById(Long categoryId);

    CategoryDTO updateCategory(Category category, Long categoryId);

    String deleteCategory(Long categoryId);
}