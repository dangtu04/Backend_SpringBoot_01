package com.dangthanhtu.example05.service.impl;

import com.dangthanhtu.example05.entity.Brand;
import com.dangthanhtu.example05.payloads.BrandDTO;
import com.dangthanhtu.example05.repository.BrandRepo;
import com.dangthanhtu.example05.service.BrandService;
import com.dangthanhtu.example05.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepo brandRepository;

    @Autowired
    private FileService fileService;

    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand brand = new Brand();
        brand.setBrandName(brandDTO.getBrandName());
        brand = brandRepository.save(brand);
        return new BrandDTO(brand.getBrandId(), brand.getImage(), brand.getBrandName());
    }

    @Override
    public BrandDTO updateBrand(Long brandId, BrandDTO brandDTO) {
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);
        if (optionalBrand.isPresent()) {
            Brand brand = optionalBrand.get();
            brand.setBrandName(brandDTO.getBrandName());
            brand = brandRepository.save(brand);
            return new BrandDTO(brand.getBrandId(), brand.getImage(), brand.getBrandName());
        }
        return null;
    }

    @Override
    public String uploadBrandImage(Long brandId, MultipartFile file) {
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);
        if (optionalBrand.isPresent()) {
            try {
                Brand brand = optionalBrand.get();
                String imagePath = fileService.uploadImage("images", file);
                brand.setImage(imagePath);
                brandRepository.save(brand);
                return imagePath;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<BrandDTO> getBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brands.stream().map(brand -> new BrandDTO(
                brand.getBrandId(),
                brand.getImage(),
                brand.getBrandName()
        )).collect(Collectors.toList());
    }

    @Override
    public BrandDTO getBrandById(Long brandId) {
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);
        return optionalBrand.map(brand -> new BrandDTO(
                brand.getBrandId(),
                brand.getImage(),
                brand.getBrandName()
        )).orElse(null);
    }

    @Override
    public InputStream getBrandImage(String fileName) {
        try {
            return fileService.getResource("images", fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteBrand(Long brandId) {
        brandRepository.deleteById(brandId);
    }
}
