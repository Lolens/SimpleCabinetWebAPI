package com.gravitlauncher.simplecabinet.web.service.storage;

import com.gravitlauncher.simplecabinet.web.configuration.properties.storage.FileStorageConfig;
import jakarta.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Priority(value = 1)
public class FileStorageService implements StorageService {

    @Autowired
    private FileStorageConfig config;

    @Override
    public URL put(String name, byte[] bytes) throws StorageException {
        try {
            Path filePath = Path.of(config.getLocalPath()).resolve(name);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, bytes);
            return getUrl(name);
        } catch (Exception e) {
            throw new StorageException("Failed to save file: " + name, e);
        }
    }

    @Override
    public URL put(String name, InputStream input, long length) throws StorageException {
        try {
            Path filePath = Path.of(config.getLocalPath()).resolve(name);
            Files.createDirectories(filePath.getParent());
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            return getUrl(name);
        } catch (Exception e) {
            throw new StorageException("Failed to save file from stream: " + name, e);
        }
    }

    @Override
    public URL getUrl(String name) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String url = config.getRemoteUrl() + encodedName;
            return new URI(url).toURL();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create URL for: " + name, e);
        }
    }

    @Override
    public Optional<byte[]> get(String name) {
        try {
            Path filePath = Path.of(config.getLocalPath()).resolve(name);
            if (!Files.exists(filePath)) {
                return Optional.empty();
            }
            return Optional.of(Files.readAllBytes(filePath));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String name) throws StorageException {
        try {
            Path filePath = Path.of(config.getLocalPath()).resolve(name);
            return Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new StorageException("Failed to delete file: " + name, e);
        }
    }

    @Override
    public boolean exists(String name) {
        Path filePath = Path.of(config.getLocalPath()).resolve(name);
        return Files.exists(filePath);
    }

    @Override
    public List<String> listAllFiles() throws StorageException {
        Path rootPath = Path.of(config.getLocalPath());

        if (!Files.exists(rootPath)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.walk(rootPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(rootPath::relativize)
                    .map(Path::toString)
                    .map(path -> path.replace(File.separator, "/"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new StorageException("Failed to list files in storage", e);
        }
    }
}