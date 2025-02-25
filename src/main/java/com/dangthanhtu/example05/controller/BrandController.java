package com.dangthanhtu.example05.controller;

import com.dangthanhtu.example05.payloads.BrandDTO;
import com.dangthanhtu.example05.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/admin/brands")
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return new ResponseEntity<>(createdBrand, HttpStatus.OK);
    }

    @PutMapping("/admin/brands/{brandId}")
    public ResponseEntity<BrandDTO> updateBrand(@RequestBody BrandDTO brandDTO, @PathVariable Long brandId) {
        BrandDTO updatedBrand = brandService.updateBrand(brandId, brandDTO);
        return new ResponseEntity<>(updatedBrand, HttpStatus.OK);
    }

    @PutMapping("/admin/brands/{brandId}/image")
    public ResponseEntity<String> uploadBrandImage(@PathVariable Long brandId, @RequestParam("image") MultipartFile file) {
        String imagePath = brandService.uploadBrandImage(brandId, file);
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }

    @GetMapping("/public/brands")
    public ResponseEntity<List<BrandDTO>> getBrands() {
        List<BrandDTO> brands = brandService.getBrands();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    @GetMapping("/public/brands/{brandId}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long brandId) {
        BrandDTO brand = brandService.getBrandById(brandId);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }

    @GetMapping("/public/brands/image/{fileName}")
    public ResponseEntity<InputStreamResource> getBrandImage(@PathVariable String fileName) throws FileNotFoundException {
        InputStream imageStream = brandService.getBrandImage(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("inline", fileName);
        return new ResponseEntity<>(new InputStreamResource(imageStream), headers, HttpStatus.OK);
    }

    @DeleteMapping("/admin/brands/{brandId}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long brandId) {
        brandService.deleteBrand(brandId);
        return new ResponseEntity<>("Brand deleted successfully", HttpStatus.OK);
    }
}
