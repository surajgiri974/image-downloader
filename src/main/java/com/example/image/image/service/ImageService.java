package com.example.image.image.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ImageService {

    /**
     * Download images given a list of URLs
     */
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

    /**
     * Fetch all image URLs from a folder URL (if directory listing is enabled)
     */
    public List<String> fetchImageUrlsFromFolder(String folderUrl, String username, String password) {
        List<String> imageUrls = new ArrayList<>();
        try {
            // Jsoup connection with basic auth
            Document doc = Jsoup.connect(folderUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                            .encodeToString((username + ":" + password).getBytes()))
                    .get();

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                if (href.endsWith(".jpg") || href.endsWith(".png") || href.endsWith(".jpeg") || href.endsWith(".gif")) {
                    String fullUrl = folderUrl + (folderUrl.endsWith("/") ? "" : "/") + href;
                    imageUrls.add(fullUrl);
                }
            }
        } catch (IOException e) {
            log.error("Error fetching image URLs from folder", e);
        }
        return imageUrls;
    }

    /**
     * Convenience method: download all images from a folder URL
     */
    public void downloadImagesFromFolder(String folderUrl, String username, String password, String targetDir) {
        List<String> urls = fetchImageUrlsFromFolder(folderUrl, username, password);
        downloadImages(urls, username, password, targetDir);
    }
}
