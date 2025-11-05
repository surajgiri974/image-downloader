package com.example.image.controller;

import com.example.image.downloader.service.ImageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageDownloadController {

    private final ImageService imageService;

    @PostMapping("/download")
    public String downloadImages(@RequestBody DownloadRequest request) {
        imageService.downloadImages(
                request.getUrls(),
                request.getUsername(),
                request.getPassword(),
                request.getTargetDir()
        );
        return "Download started!";
    }

    /**
     * Request body class
     */
    @Getter
    @Setter
    public static class DownloadRequest {
        // Getters and Setters
        private List<String> urls;
        private String username;
        private String password;
        private String targetDir;

    }
}
