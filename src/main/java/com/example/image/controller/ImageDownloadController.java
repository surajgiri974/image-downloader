package com.example.image.controller;
import com.example.image.downloader.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageDownloadController {

    private final ImageService downloadService;

    @PostMapping("/download")
    public String downloadImages(
            @RequestBody ImageDownloadRequest request
    ) {
        downloadService.downloadImages(
                request.getUrls(),
                request.getUsername(),
                request.getPassword(),
                request.getTargetDir()
        );
        return "Download started!";
    }

    public static class ImageDownloadRequest {
        private List<String> urls;
        private String username;
        private String password;
        private String targetDir;

        public List<String> getUrls() { return urls; }
        public void setUrls(List<String> urls) { this.urls = urls; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getTargetDir() { return targetDir; }
        public void setTargetDir(String targetDir) { this.targetDir = targetDir; }
    }
}
