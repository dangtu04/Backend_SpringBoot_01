package com.dangthanhtu.example05.service.impl;

import com.dangthanhtu.example05.entity.Banner;
import com.dangthanhtu.example05.payloads.BannerDTO;
import com.dangthanhtu.example05.repository.BannerRepo;
import com.dangthanhtu.example05.service.BannerService;
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
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepo bannerRepository;

    @Autowired
    private FileService fileService;

    @Override
    public BannerDTO createBanner(BannerDTO bannerDTO) {
        Banner banner = new Banner();
        banner.setTitle(bannerDTO.getTitle());
        banner.setDescription(bannerDTO.getDescription());
        banner.setIsActive(bannerDTO.getIsActive());
        banner = bannerRepository.save(banner);
        return new BannerDTO(banner.getId(), banner.getImage(), banner.getTitle(), banner.getDescription(), banner.getIsActive());
    }

    @Override
    public BannerDTO updateBanner(Long bannerId, BannerDTO bannerDTO) {
        Optional<Banner> optionalBanner = bannerRepository.findById(bannerId);
        if (optionalBanner.isPresent()) {
            Banner banner = optionalBanner.get();
            banner.setTitle(bannerDTO.getTitle());
            banner.setDescription(bannerDTO.getDescription());
            banner.setIsActive(bannerDTO.getIsActive());
            banner = bannerRepository.save(banner);
            return new BannerDTO(banner.getId(), banner.getImage(), banner.getTitle(), banner.getDescription(), banner.getIsActive());
        }
        return null;
    }

    @Override
    public String uploadBannerImage(Long bannerId, MultipartFile file) {
        Optional<Banner> optionalBanner = bannerRepository.findById(bannerId);
        if (optionalBanner.isPresent()) {
            try {
                Banner banner = optionalBanner.get();
                String imagePath = fileService.uploadImage("images", file);
                banner.setImage(imagePath);
                bannerRepository.save(banner);
                return imagePath;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public BannerDTO updateBannerIsActive(Long bannerId, Boolean isActive) {
        Optional<Banner> optionalBanner = bannerRepository.findById(bannerId);
        if (optionalBanner.isPresent()) {
            Banner banner = optionalBanner.get();
            banner.setIsActive(isActive);
            banner = bannerRepository.save(banner);
            return new BannerDTO(banner.getId(), banner.getImage(), banner.getTitle(), banner.getDescription(), banner.getIsActive());
        }
        return null;
    }

    @Override
    public List<BannerDTO> getBanners() {
        List<Banner> banners = bannerRepository.findAll();
        return banners.stream().map(banner -> new BannerDTO(
                banner.getId(),
                banner.getImage(),
                banner.getTitle(),
                banner.getDescription(),
                banner.getIsActive()
        )).collect(Collectors.toList());
    }

    @Override
    public List<BannerDTO> getActiveBanners() {
        List<Banner> banners = bannerRepository.findByIsActiveTrue();
        return banners.stream().map(banner -> new BannerDTO(
                banner.getId(),
                banner.getImage(),
                banner.getTitle(),
                banner.getDescription(),
                banner.getIsActive()
        )).collect(Collectors.toList());
    }

    @Override
    public BannerDTO getBannerById(Long bannerId) {
        Optional<Banner> optionalBanner = bannerRepository.findById(bannerId);
        return optionalBanner.map(banner -> new BannerDTO(
                banner.getId(),
                banner.getImage(),
                banner.getTitle(),
                banner.getDescription(),
                banner.getIsActive()
        )).orElse(null);
    }

    @Override
    public InputStream getBannerImage(String fileName) {
        try {
            return fileService.getResource("images", fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteBanner(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }
}
