package com.guitar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${file.upload.base-path:/uploads}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + basePath + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);
    }
}
