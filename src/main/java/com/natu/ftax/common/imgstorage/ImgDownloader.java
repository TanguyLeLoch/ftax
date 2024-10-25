package com.natu.ftax.common.imgstorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class ImgDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgDownloader.class);

    @Value("${image.storage.path}")
    private String imageStoragePath;

    public void downloadAndSaveImage(String imageUrl, String fileName) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(imageUrl);
                Path targetPath = Paths.get(imageStoragePath, fileName);

                // Ensure the directory structure exists
                Files.createDirectories(targetPath.getParent());

                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetPath);
                }

            } catch (Exception e) {
                LOGGER.info("Failed to download image from url: " + imageUrl, e);
            }
        });

    }
}
