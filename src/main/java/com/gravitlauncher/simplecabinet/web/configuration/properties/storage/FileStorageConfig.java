package com.gravitlauncher.simplecabinet.web.configuration.properties.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "storage.file")
public class FileStorageConfig {
    private String remoteUrl;
    private String localPath;

    public FileStorageConfig() {
    }

}
