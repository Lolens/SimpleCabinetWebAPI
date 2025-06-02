package com.gravitlauncher.simplecabinet.web.service.storage;

import java.io.InputStream;
import java.net.URL;

public interface StorageService {
    URL put(String name, byte[] bytes) throws StorageException;

    URL put(String name, InputStream stream, long length) throws StorageException;

    URL getUrl(String name);

    class StorageException extends Exception {
        public StorageException() {
        }

        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }

        public StorageException(Throwable cause) {
            super(cause);
        }
    }
}
