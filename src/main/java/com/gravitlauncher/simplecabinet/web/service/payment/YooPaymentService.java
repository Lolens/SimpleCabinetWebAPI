package com.gravitlauncher.simplecabinet.web.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravitlauncher.simplecabinet.web.configuration.properties.payment.YooPaymentConfig;
import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.exception.PaymentException;
import com.gravitlauncher.simplecabinet.web.model.shop.Payment;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.shop.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class YooPaymentService implements BasicPaymentService {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private YooPaymentConfig config;
    @Autowired
    private ObjectMapper objectMapper;
    private final transient HttpClient client = HttpClient.newBuilder().build();

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @Override
    public boolean isEnabled() {
        return config.isEnable();
    }

    public PaymentService.PaymentCreationInfo createBalancePayment(User user, double sum, String ip) throws IOException, InterruptedException, URISyntaxException {
        if (!config.isEnable()) {
            throw new PaymentException("This payment method is disabled", 6);
        }
        var payment = paymentService.createBasic(user, sum);
        payment.setSystem("Yoo");
        var request = new YooPaymentRequest(YooPaymentAmount.ofRub(sum), true, YooPaymentConfirmation.ofRedirect(config.redirectUrl), "Balance");
        var idempotenceKey = UUID.randomUUID().toString();
        var httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.yookassa.ru/v3/payments"))
                .method("POST", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                .header("Authorization", basicAuth(config.merchantId, config.secretKey))
                .header("Content-Type", "application/json")
                .header("Idempotence-Key", idempotenceKey)
                .build();
        var result = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (result.statusCode() >= 200 && result.statusCode() < 300) {
            var response = objectMapper.readValue(result.body(), YooPaymentResponse.class);
            payment.setSystemPaymentId(response.id);
            paymentService.save(payment);
            return new PaymentService.PaymentCreationInfo(response.confirmation.toRedirectInfo(), payment);
        } else {
            payment.setStatus(Payment.PaymentStatus.CANCELED);
            paymentService.save(payment);
            var error = objectMapper.readValue(result.body(), YooError.class);
            String errorText;
            if (error == null) {
                errorText = String.format("Http Code: %d", result.statusCode());
            } else if (error.description != null) {
                errorText = error.description;
            } else if (error.code != null) {
                errorText = error.code;
            } else {
                errorText = String.format("Http Code: %d", result.statusCode());
            }
            throw new PaymentException(errorText, 4);
        }
    }

    public void complete(YooPaymentNotification notification) throws BalanceException {
        if (!"notification".equals(notification.type)) {
            return;
        }
        if ("payment.succeeded".equals(notification.event)) {
            String id = notification.object.id;
            var payment = paymentService.findUserPaymentBySystemId("Yoo", id).orElseThrow();
            var oldStatus = payment.getStatus();
            completePayment(payment, Payment.PaymentStatus.SUCCESS);
            if (oldStatus != Payment.PaymentStatus.SUCCESS) {
                paymentService.deliveryPayment(payment);
            }
        } else if ("payment.cancelled".equals(notification.event)) {
            String id = notification.object.id;
            var payment = paymentService.findUserPaymentBySystemId("Yoo", id).orElseThrow();
            completePayment(payment, Payment.PaymentStatus.CANCELED);
        }
    }

    private void completePayment(Payment payment, Payment.PaymentStatus status) {
        payment.setStatus(status);
        paymentService.save(payment);
    }

    public record YooError(String type, String id, String code, String description) {

    }

    public record YooPaymentAmount(String value, String currency) {
        public static YooPaymentAmount ofRub(double sum) {
            return new YooPaymentAmount(String.valueOf(sum), "RUB");
        }

        @Override
        public String toString() {
            return "YooPaymentAmount{" +
                    "value='" + value + '\'' +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }

    public record YooPaymentConfirmation(String type, String return_url) {
        public static YooPaymentConfirmation ofRedirect(String url) {
            return new YooPaymentConfirmation("redirect", url);
        }

        @Override
        public String toString() {
            return "YooPaymentConfirmation{" +
                    "type='" + type + '\'' +
                    ", return_url='" + return_url + '\'' +
                    '}';
        }
    }

    public record YooPaymentConfirmationResponse(String type, String confirmation_url) {

        public PaymentService.PaymentRedirectInfo toRedirectInfo() {
            return new PaymentService.PaymentRedirectInfo(confirmation_url);
        }

        @Override
        public String toString() {
            return "YooPaymentConfirmation{" +
                    "type='" + type + '\'' +
                    ", confirmation_url='" + confirmation_url + '\'' +
                    '}';
        }
    }

    public record YooPaymentRequest(YooPaymentAmount amount, boolean capture,
                                    YooPaymentConfirmation confirmation, String description) {
        @Override
        public String toString() {
            return "YooPaymentRequest{" +
                    "amount=" + amount +
                    ", capture=" + capture +
                    ", confirmation=" + confirmation +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public static class YooPaymentResponse {
        public String id;
        public String status;
        public boolean paid;
        public YooPaymentAmount amount;
        public YooPaymentConfirmationResponse confirmation;
        public LocalDateTime createdAt;
        public String description;
        public Map<String, String> metadata;
        public Map<String, String> recipient;
        public boolean refundable;
        public boolean test;

        @Override
        public String toString() {
            return "YooPaymentResponse{" +
                    "id='" + id + '\'' +
                    ", status='" + status + '\'' +
                    ", paid=" + paid +
                    ", amount=" + amount +
                    ", confirmation=" + confirmation +
                    ", createdAt=" + createdAt +
                    ", description='" + description + '\'' +
                    ", metadata=" + metadata +
                    ", recipient=" + recipient +
                    ", refundable=" + refundable +
                    ", test=" + test +
                    '}';
        }
    }

    public static class YooPaymentNotification {
        public String type;
        public String event;
        public YooPaymentNotificationObject object;

        public static class YooPaymentNotificationObject {
            public String id;
            public String status;
            public boolean paid;
        }
    }
}
