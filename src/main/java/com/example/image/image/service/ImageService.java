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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class ImageService {

    // Fetch image URLs from a page with table structure
    public List<String> fetchImageUrlsFromPage(String pageUrl, String username, String password) {
        List<String> imageUrls = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(15000)
                    .header("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString((username + ":" + password).getBytes()))
                    .get();

            // Select all <a> tags inside table cells recursively
            Elements links = doc.select("td a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                if (href.matches("(?i).*\\.(jpg|jpeg|png|gif)$")) {
                    // Encode spaces and special chars
                    href = href.replace(" ", "%20");
                    String fullUrl = href.startsWith("http") ? href :
                            pageUrl + (pageUrl.endsWith("/") ? "" : "/") + href.replaceFirst("^/", "");
                    imageUrls.add(fullUrl);
                }
            }
        } catch (IOException e) {
            log.error("Error fetching image URLs", e);
        }
        return imageUrls;
    }

    // Download list of image URLs
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

    // Fetch and download all images from a page
    public void downloadImagesFromPage(String pageUrl, String username, String password, String targetDir) {
        List<String> urls = fetchImageUrlsFromPage(pageUrl, username, password);
        downloadImages(urls, username, password, targetDir);
    }
}
