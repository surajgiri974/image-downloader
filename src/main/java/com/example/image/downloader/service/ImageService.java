package com.example.image.downloader.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
public class ImageService {

    public void downloadImages(List<String> urls, String username, String password, String targetDir) {
        try {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(null, new UsernamePasswordCredentials(username, password.toCharArray()));

            try (CloseableHttpClient client = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .build()) {

                for (String imageUrl : urls) {
                    HttpGet request = new HttpGet(imageUrl);
                    client.execute(request, response -> {
                        byte[] data = EntityUtils.toByteArray(response.getEntity());

                        // Extract filename from URL
                        String fileName = new File(new URL(imageUrl).getPath()).getName();
                        File outputFile = new File(targetDir, fileName);

                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            fos.write(data);
                            log.info("Downloaded: {}", fileName);
                        }
                        return null;
                    });
                }
            }
        } catch (IOException e) {
            log.error("Error downloading images", e);
        }
    }
}
