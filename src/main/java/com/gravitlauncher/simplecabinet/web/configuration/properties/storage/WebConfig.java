package com.gravit.simplecabinet.web.configuration.properties.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.gravit.simplecabinet.web.configuration.properties.storage.FileStorageConfig;


@Getter
@Setter
@ConfigurationProperties(prefix = "webconfig")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    String frontendUrl;
    final FileStorageConfig storageConfig;

    public WebConfig(FileStorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:" + storageConfig.getLocalPath() + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/assets/**")
                .allowedOrigins(frontendUrl)
                .allowedMethods("GET")
                .allowCredentials(true);
    }
}


