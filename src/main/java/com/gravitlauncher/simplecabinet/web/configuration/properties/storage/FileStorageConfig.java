package com.gravitlauncher.simplecabinet.web.configuration.properties.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storage.file")
public class FileStorageConfig implements WebMvcConfigurer {

    private String localPath;
    private String remoteUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:" + localPath + "/")
                .setCachePeriod(86400);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/assets/**")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .maxAge(86400);
    }
}