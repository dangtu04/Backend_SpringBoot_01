package com.dangthanhtu.example05.service;

import com.dangthanhtu.example05.payloads.BrandDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface BrandService {
    BrandDTO createBrand(BrandDTO brandDTO);

    BrandDTO updateBrand(Long brandId, BrandDTO brandDTO);

    String uploadBrandImage(Long brandId, MultipartFile file);

    List<BrandDTO> getBrands();

    BrandDTO getBrandById(Long brandId);

    InputStream getBrandImage(String fileName);

    void deleteBrand(Long brandId);
}
