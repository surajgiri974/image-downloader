package com.example.image.image.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
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

    /**
     * Download images from a list of URLs protected by Basic Auth
     *
     * @param urls       List of image URLs
     * @param username   Basic Auth username
     * @param password   Basic Auth password
     * @param targetDir  Local directory to save images
     */
    public void downloadProtectedImages(List<String> urls, String username, String password, String targetDir) {
        File dir = new File(targetDir);
        if (!dir.exists()) dir.mkdirs();

        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(null, -1),  // apply to all hosts/ports
                new UsernamePasswordCredentials(username, password.toCharArray())
        );

        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build()) {

            for (String imageUrl : urls) {
                try {
                    log.info("Downloading: {}", imageUrl);

                    HttpGet request = new HttpGet(imageUrl);
                    client.execute(request, response -> {
                        int status = response.getCode();
                        if (status == 200) {
                            byte[] data = EntityUtils.toByteArray(response.getEntity());
                            String fileName = new File(new URL(imageUrl).getPath()).getName();
                            File outputFile = new File(dir, fileName);

                            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                fos.write(data);
                            }
                            log.info("Saved: {}", outputFile.getAbsolutePath());
                        } else {
                            log.warn("Failed to download {} → HTTP {}", imageUrl, status);
                        }
                        return null;
                    });

                } catch (Exception e) {
                    log.error("Error downloading image {}", imageUrl, e);
                }
            }

        } catch (IOException e) {
            log.error("Error initializing HTTP client", e);
        }
    }
}
