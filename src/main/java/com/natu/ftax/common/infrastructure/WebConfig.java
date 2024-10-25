package com.natu.ftax.common.infrastructure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Value("${image.storage.path}")
    private String imageStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = imageStoragePath;
        if (!path.endsWith("/")) {
            path += "/";
        }
        log.info("Configuring image resource handler with path: {}", path);
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + path)
                .setCachePeriod(3600)  // Optional: add caching
                .resourceChain(true);  // Enable resource chain optimization
    }

}