package com.gravitlauncher.simplecabinet.web.configuration.properties.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "storage.s3")
public class S3StorageConfig {

    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucket;

    private String region;

    public S3StorageConfig() {
    }

}
