package com.gravitlauncher.simplecabinet.web.service.storage;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface StorageService {

    URL put(String name, byte[] bytes) throws StorageException;

    URL put(String name, InputStream stream, long length) throws StorageException;

    URL getUrl(String name);

    Optional<byte[]> get(String name);

    boolean delete(String name) throws StorageException;

    boolean exists(String name);

    List<String> listAllFiles() throws StorageException;

    class StorageException extends Exception {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(Exception exception) {
            super(exception);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}