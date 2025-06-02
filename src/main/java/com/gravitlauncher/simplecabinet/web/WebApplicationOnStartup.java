package com.gravitlauncher.simplecabinet.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebApplicationOnStartup {
    public static void prepare() throws IOException {
        if (unpack(Path.of("application.properties"), "application.properties")) {
            {
                Path dir = Paths.get("assets");
                if (Files.notExists(dir)) {
                    Files.createDirectories(dir);
                }
            }
            {
                Path dir = Paths.get("templates");
                if (Files.notExists(dir)) {
                    Files.createDirectories(dir);
                }
            }
            unpack(Path.of("templates", "email-passwordreset.html"), "templates/email-passwordreset.html");
            unpack(Path.of("templates", "email-regconfirm.html"), "templates/email-regconfirm.html");
            System.out.println("File 'application.properties' created. Stop...");
            System.exit(0);
        }
    }

    public static boolean unpack(Path path, String resourceUrl) throws IOException {
        if (Files.notExists(path)) {
            URL url = WebApplicationOnStartup.class.getResource("/" + resourceUrl);
            if (url == null) {
                throw new RuntimeException(String.format("Resource '%s' not found", resourceUrl));
            }
            URLConnection c = url.openConnection();
            try (InputStream input = c.getInputStream()) {
                try (OutputStream output = new FileOutputStream(path.toFile())) {
                    input.transferTo(output);
                }
            }
            return true;
        }
        return false;
    }
}
