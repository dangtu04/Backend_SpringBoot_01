package com.dangthanhtu.example05.controller;

import com.dangthanhtu.example05.payloads.BannerDTO;
import com.dangthanhtu.example05.service.BannerService;
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
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @PostMapping("/admin/banners")
    public ResponseEntity<BannerDTO> createBanner(@RequestBody BannerDTO bannerDTO) {
        BannerDTO createdBanner = bannerService.createBanner(bannerDTO);
        return new ResponseEntity<>(createdBanner, HttpStatus.OK);
    }

    @PutMapping("/admin/banners/{bannerId}")
    public ResponseEntity<BannerDTO> updateBanner(@RequestBody BannerDTO bannerDTO, @PathVariable Long bannerId) {
        BannerDTO updatedBanner = bannerService.updateBanner(bannerId, bannerDTO);
        return new ResponseEntity<>(updatedBanner, HttpStatus.OK);
    }

    @PutMapping("/admin/banners/{bannerId}/image")
    public ResponseEntity<String> uploadBannerImage(@PathVariable Long bannerId, @RequestParam("image") MultipartFile file) {
        String imagePath = bannerService.uploadBannerImage(bannerId, file);
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }

    @PatchMapping("/admin/banners/{bannerId}/is-active")
    public ResponseEntity<BannerDTO> updateBannerIsActive(@PathVariable Long bannerId, @RequestParam Boolean isActive) {
        BannerDTO updatedBanner = bannerService.updateBannerIsActive(bannerId, isActive);
        return new ResponseEntity<>(updatedBanner, HttpStatus.OK);
    }

    @GetMapping("/admin/banners")
    public ResponseEntity<List<BannerDTO>> getBanners() {
        List<BannerDTO> banners = bannerService.getBanners();
        return new ResponseEntity<>(banners, HttpStatus.OK);
    }

    @GetMapping("/public/banners/{bannerId}")
    public ResponseEntity<BannerDTO> getBannerById(@PathVariable Long bannerId) {
        BannerDTO banner = bannerService.getBannerById(bannerId);
        return new ResponseEntity<>(banner, HttpStatus.OK);
    }

    @GetMapping("/public/banners/active")
    public ResponseEntity<List<BannerDTO>> getActiveBanners() {
        List<BannerDTO> banners = bannerService.getActiveBanners();
        return new ResponseEntity<>(banners, HttpStatus.OK);
    }

    @GetMapping("/public/banners/image/{fileName}")
    public ResponseEntity<InputStreamResource> getBannerImage(@PathVariable String fileName) throws FileNotFoundException {
        InputStream imageStream = bannerService.getBannerImage(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("inline", fileName);
        return new ResponseEntity<>(new InputStreamResource(imageStream), headers, HttpStatus.OK);
    }

    @DeleteMapping("/admin/banners/{bannerId}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long bannerId) {
        bannerService.deleteBanner(bannerId);
        return new ResponseEntity<>("Banner deleted successfully", HttpStatus.OK);
    }
}
