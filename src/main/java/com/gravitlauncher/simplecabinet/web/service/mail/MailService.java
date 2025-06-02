package com.gravitlauncher.simplecabinet.web.service.mail;

public interface MailService {
    void sendSimpleEmail(String toAddress, String message);

    void sendTemplateEmail(String toAddress, String templateName, String... params);
}
