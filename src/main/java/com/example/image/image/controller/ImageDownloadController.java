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

    @PostMapping("/download")
    public String downloadImages(@RequestBody DownloadRequest request) {
        log.info("Starting download for {} images", request.getUrls().size());

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
