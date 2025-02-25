package com.dangthanhtu.example05.service;

import com.dangthanhtu.example05.payloads.BannerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface BannerService {
    BannerDTO createBanner(BannerDTO bannerDTO);
    BannerDTO updateBanner(Long bannerId, BannerDTO bannerDTO);
    String uploadBannerImage(Long bannerId, MultipartFile file);
    BannerDTO updateBannerIsActive(Long bannerId, Boolean isActive);
    List<BannerDTO> getBanners();
    List<BannerDTO> getActiveBanners();
    BannerDTO getBannerById(Long bannerId);
    InputStream getBannerImage(String fileName);
    void deleteBanner(Long bannerId);
}
