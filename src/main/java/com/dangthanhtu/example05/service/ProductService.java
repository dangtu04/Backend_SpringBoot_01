package com.dangthanhtu.example05.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.dangthanhtu.example05.entity.Product;
import com.dangthanhtu.example05.payloads.ProductDTO;
import com.dangthanhtu.example05.payloads.ProductResponse;

public interface ProductService {
        ProductDTO addProduct(Long categoryId, Long brandId, Product product);

        ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

        ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder);

        ProductDTO updateProduct(Long productId, Product product);

        ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

        public InputStream getProductImage(String fileName) throws FileNotFoundException;

        ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder);

        String deleteProduct(Long productId);

        ProductDTO getProductById(Long productId);

        ProductResponse searchByBrand(Long brandId, Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder);

        ProductResponse searchByCategoryAndBrand(Long categoryId, Long brandId, Integer pageNumber, Integer pageSize,
                        String sortBy, String sortOrder);

}