package com.gravitlauncher.simplecabinet.web.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravitlauncher.simplecabinet.web.configuration.properties.payment.FreekassaPaymentConfig;
import com.gravitlauncher.simplecabinet.web.exception.PaymentException;
import com.gravitlauncher.simplecabinet.web.model.shop.Payment;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.shop.PaymentService;
import com.gravitlauncher.simplecabinet.web.utils.SecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class FreekassaPaymentService implements BasicPaymentService {
    private final transient HttpClient client = HttpClient.newBuilder().build();
    private final transient ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(FreekassaPaymentService.class);
    @Autowired
    private FreekassaPaymentConfig config;
    @Autowired
    private PaymentService paymentService;

    @Override
    public PaymentService.PaymentCreationInfo createBalancePayment(User user, double sum, String ip) throws Exception {
        if (!config.isEnable()) {
            throw new PaymentException("This payment method is disabled", 6);
        }
        var payment = paymentService.createBasic(user, sum);
        payment.setSystem("Freekassa");
        try {
            var order = request("https://api.freekassa.ru/v1/orders/create", createMap(
                    "shopId", config.getShopId(),
                    "nonce", String.valueOf(System.currentTimeMillis()),
                    "paymentId", String.valueOf(payment.getId()),
                    "i", config.getPaymentSystemId(),
                    "email", user.getEmail(),
                    "amount", String.valueOf(sum),
                    "ip", ip,
                    "currency", "RUB"), CreateOrderResponse.class);
            payment.setSystemPaymentId(String.valueOf(order.orderId()));
            paymentService.save(payment);
            return new PaymentService.PaymentCreationInfo(new PaymentService.PaymentRedirectInfo(order.location()), payment);
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.CANCELED);
            paymentService.save(payment);
            if (e instanceof PaymentException) {
                throw e;
            } else {
                logger.error("Error when create order", e);
                throw new PaymentException("Error when processing request", 4);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return config.isEnable();
    }

    @Transactional
    public void complete(WebhookResponse webhookResponse) {
        var sign = DigestUtils.md5Hex(String.format("%s:%s:%s:%s:%s", config.getShopId(), webhookResponse.AMOUNT(), config.getSecret(), "RUB", webhookResponse.MERCHANT_ORDER_ID()));
        if (!sign.equalsIgnoreCase(webhookResponse.SIGN())) {
            throw new SecurityException("Invalid signature");
        }
        var payment = paymentService.findUserPaymentBySystemId("Freekassa", webhookResponse.intid()).orElseThrow();
        var oldStatus = payment.getStatus();
        completePayment(payment, Payment.PaymentStatus.SUCCESS);
        if (oldStatus != Payment.PaymentStatus.SUCCESS) {
            paymentService.deliveryPayment(payment);
        }
    }

    public <T> T request(String url, Map<String, String> parameters, Class<T> clazz) throws IOException, InterruptedException, NoSuchAlgorithmException {
        List<String> keys = new ArrayList<>(parameters.keySet());
        Collections.sort(keys);
        StringBuilder signData = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            if (i != 0) {
                signData.append("|");
            }
            signData.append(parameters.get(keys.get(i)));
        }
        parameters.put("signature", SecurityUtils.hmacSha256(signData.toString(), config.getApiKey() == null ? "TEST" : config.getApiKey()));
        String json = mapper.writeValueAsString(parameters);
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url))
                .build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), clazz);
        } else {
            JsonNode node = mapper.readTree(response.body());
            throw new PaymentException(node.findValue("msg").asText(), 9);
        }
    }

    private void completePayment(Payment payment, Payment.PaymentStatus status) {
        payment.setStatus(status);
        paymentService.save(payment);
    }

    private Map<String, String> createMap(String... strs) {
        if (strs.length % 2 != 0) {
            throw new IllegalArgumentException(String.format("Wrong arguments. %d not divided by 2", strs.length));
        }
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < strs.length; i += 2) {
            String key = strs[i];
            String value = strs[i + 1];
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    public record CreateOrderResponse(String type, long orderId, String orderHash, String location) {

    }

    public record WebhookResponse(String MERCHANT_ID, String AMOUNT, String intid, String MERCHANT_ORDER_ID,
                                  String CUR_ID, String SIGN) {

    }
}
