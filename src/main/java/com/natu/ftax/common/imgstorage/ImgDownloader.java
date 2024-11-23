package com.natu.ftax.common.imgstorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

                // Check if file exists and handle accordingly
                if (Files.exists(targetPath)) {
                    // Option 1: Skip if file exists
                    LOGGER.info("File already exists: " + targetPath);
                    return;

                    // Option 2: Override existing file (uncomment if needed)
                    // Files.delete(targetPath);
                }

                try (InputStream in = url.openStream()) {
                    // Use REPLACE_EXISTING to handle race conditions
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (MalformedURLException e) {
                LOGGER.error("Invalid URL: " + imageUrl, e);
            } catch (IOException e) {
                LOGGER.error("Failed to download/save image from url: " + imageUrl, e);
            } catch (Exception e) {
                LOGGER.error("Unexpected error while processing image from url: " + imageUrl, e);
            }
        }).exceptionally(throwable -> {
            LOGGER.error("Async operation failed for url: " + imageUrl, throwable);
            return null;
        });
    }
}
