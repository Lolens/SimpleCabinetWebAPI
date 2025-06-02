package com.gravitlauncher.simplecabinet.web.service.mail;

import com.gravitlauncher.simplecabinet.web.configuration.properties.MailConfig;
import jakarta.annotation.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Priority(value = 0)
@ConditionalOnProperty(
        value = "mail.enabled")
public class MailSTMPService implements MailService {
    private transient final Logger logger = LoggerFactory.getLogger(MailSTMPService.class);
    @Autowired
    private MailConfig config;
    @Autowired
    public JavaMailSender emailSender;

    public void sendSimpleEmail(String toAddress, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(text);
            message.setSubject(config.getSubject());
            message.setText(text);
            emailSender.send(message);
        } catch (Throwable e) {
            logger.error("Failed to send email", e);
        }
    }

    @Override
    public void sendTemplateEmail(String toAddress, String templateName, String... params) {
        try {
            String template = Files.readString(Path.of(config.getTemplatesDirectory(), "email-passwordreset.html"));
            for (int i = 0; i < params.length; i += 2) {
                template = template.replace(params[i], params[i + 1]);
            }
            sendSimpleEmail(toAddress, template);
        } catch (IOException e) {
            logger.error("Failed to read template", e);
        }
    }
}
