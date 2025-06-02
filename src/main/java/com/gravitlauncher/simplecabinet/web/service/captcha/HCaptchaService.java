package com.gravitlauncher.simplecabinet.web.service.captcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravitlauncher.simplecabinet.web.service.payment.FreekassaPaymentService;
import jakarta.annotation.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Priority(value = 0)
@ConditionalOnProperty(
        value = "captcha.hcaptcha.enable")
public class HCaptchaService implements CaptchaService {
    private final Logger logger = LoggerFactory.getLogger(FreekassaPaymentService.class);
    private final transient HttpClient client = HttpClient.newBuilder().build();
    private final transient ObjectMapper mapper = new ObjectMapper();
    @Value("${captcha.hcaptcha.secret}")
    private String secret;

    @Override
    public boolean verify(String captchaResponse) {
        if (captchaResponse == null) {
            return false;
        }
        var requestBody = String.format("response=%s&secret=%s", URLEncoder.encode(captchaResponse, StandardCharsets.UTF_8),
                URLEncoder.encode(secret, StandardCharsets.UTF_8));
        try {
            var response = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://api.hcaptcha.com/siteverify"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                var result = mapper.readValue(response.body(), HCaptchaResponse.class);
                return result.success;
            } else {
                logger.error("HCaptcha error {}", response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("HCaptcha error", e);
        }
        return false;
    }

    public record HCaptchaResponse(boolean success, String challenge_ts, String hostname, boolean credit) {

    }
}
