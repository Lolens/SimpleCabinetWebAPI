package com.gravitlauncher.simplecabinet.web.service.mail;

import com.gravitlauncher.simplecabinet.web.configuration.properties.MailConfig;
import jakarta.annotation.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Priority(value = 1)
public class MailConsoleService implements MailService {
    private transient final Logger logger = LoggerFactory.getLogger(MailConsoleService.class);
    @Autowired
    private MailConfig config;

    @Override
    public void sendSimpleEmail(String toAddress, String message) {
        logger.info("Mail {}: {}", toAddress, message);
    }

    @Override
    public void sendTemplateEmail(String toAddress, String templateName, String... params) {
        try {
            String template = Files.readString(Path.of(config.getTemplatesDirectory(), templateName));
            for (int i = 0; i < params.length; i += 2) {
                template = template.replace(params[i], params[i + 1]);
            }
            sendSimpleEmail(toAddress, template);
        } catch (IOException e) {
            logger.error("Failed to read template", e);
        }
    }
}
