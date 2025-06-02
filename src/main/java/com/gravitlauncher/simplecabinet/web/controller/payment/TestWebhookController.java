package com.gravitlauncher.simplecabinet.web.controller.payment;

import com.gravitlauncher.simplecabinet.web.service.payment.TestPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/test")
public class TestWebhookController {
    @Autowired
    private TestPaymentService service;

    @PostMapping("/payment")
    public String payment(@RequestBody TestPaymentService.WebhookResponse webhookResponse, HttpServletRequest request) {
        service.complete(webhookResponse);
        return "YES";
    }
}
