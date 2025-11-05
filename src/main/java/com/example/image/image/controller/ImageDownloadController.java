package com.example.image.image.controller;

import com.example.image.image.service.ImageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageDownloadController {

    private final ImageService imageService;

    // List image URLs
    @PostMapping("/list")
    public List<String> listImages(@RequestBody DownloadRequest request) {
        log.info("Fetching image URLs from page: {}", request.getPageUrl());
        return imageService.fetchImageUrlsFromPage(
                request.getPageUrl(),
                request.getUsername(),
                request.getPassword()
        );
    }

    // Download all images to local folder
    @PostMapping("/download")
    public String downloadImages(@RequestBody DownloadRequest request) {
        log.info("Downloading images from page: {}", request.getPageUrl());
        imageService.downloadImagesFromPage(
                request.getPageUrl(),
                request.getUsername(),
                request.getPassword(),
                request.getTargetDir()
        );
        return "Download started!";
    }

    // Request body class
    @Getter
    @Setter
    public static class DownloadRequest {
        private String pageUrl;
        private String username;
        private String password;
        private String targetDir; // Required only for download
    }
}
