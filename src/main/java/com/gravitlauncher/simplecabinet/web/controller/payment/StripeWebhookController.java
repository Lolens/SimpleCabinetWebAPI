package com.gravitlauncher.simplecabinet.web.controller.payment;

import com.gravitlauncher.simplecabinet.web.service.payment.StripePaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {
    @Autowired
    private StripePaymentService service;

    @PostMapping("/payment")
    public void payment(@RequestBody String body, HttpServletRequest httpServletRequest) {
        String signature = httpServletRequest.getHeader("Stripe-Signature");
        service.complete(body, signature);
    }
}
