package com.gravitlauncher.simplecabinet.web.service.storage;

import com.gravitlauncher.simplecabinet.web.configuration.properties.storage.FileStorageConfig;
import jakarta.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Service
@Priority(value = 1)
public class
FileStorageService implements StorageService {

    @Autowired
    private FileStorageConfig config;

    @Override
    public URL put(String name, byte[] bytes) throws StorageException {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(Path.of(config.getLocalPath()).resolve(name).toFile()))) {
            try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
                input.transferTo(output);
            }
            return getUrl(name);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public URL put(String name, InputStream input, long length) throws StorageException {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(Path.of(config.getLocalPath()).resolve(name).toFile()))) {
            input.transferTo(output);
            return getUrl(name);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public URL getUrl(String name) {
        try {
            return new URI(config.getRemoteUrl().concat(URLEncoder.encode(name, StandardCharsets.UTF_8))).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
