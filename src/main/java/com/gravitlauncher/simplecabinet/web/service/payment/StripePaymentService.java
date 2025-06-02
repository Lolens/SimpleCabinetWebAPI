package com.gravitlauncher.simplecabinet.web.service.payment;

import com.gravitlauncher.simplecabinet.web.configuration.properties.payment.StripePaymentConfig;
import com.gravitlauncher.simplecabinet.web.exception.BalanceException;
import com.gravitlauncher.simplecabinet.web.exception.PaymentException;
import com.gravitlauncher.simplecabinet.web.model.shop.Payment;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.shop.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService implements BasicPaymentService {
    @Autowired
    private StripePaymentConfig config;
    @Autowired
    private PaymentService paymentService;

    @Override
    public boolean isEnabled() {
        return config.isEnable();
    }

    @Override
    public PaymentService.PaymentCreationInfo createBalancePayment(User user, double sum, String ip) throws Exception {
        if (!config.isEnable()) {
            throw new PaymentException("This payment method is disabled", 6);
        }
        var realSum = Math.round(sum);
        var payment = paymentService.createBasic(user, realSum);
        payment.setSystem("Stripe");
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setReturnUrl(config.getRedirectUrl())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(realSum)
                                        .setPrice(config.getPriceId())
                                        .build())
                        .build();
        Session session = Session.create(params, RequestOptions.builder()
                .setApiKey(config.getApiKey())
                .build());
        payment.setSystemPaymentId(String.valueOf(session.getPaymentIntent()));
        paymentService.save(payment);
        return new PaymentService.PaymentCreationInfo(new PaymentService.PaymentRedirectInfo(session.getUrl()), payment);
    }

    public void complete(String body, String signature) throws BalanceException {
        Event event = null;

        try {
            event = Webhook.constructEvent(
                    body, signature, config.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            // Invalid signature
            throw new BalanceException("Invalid signature");
        }

        PaymentIntent intent = (PaymentIntent) event
                .getDataObjectDeserializer()
                .getObject()
                .get();
        var payment = paymentService.findUserPaymentBySystemId("Stripe", intent.getId()).orElseThrow();
        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            return;
        }
        switch (event.getType()) {
            case "payment_intent.succeeded":
                var oldStatus = payment.getStatus();
                completePayment(payment, Payment.PaymentStatus.SUCCESS);
                if (oldStatus != Payment.PaymentStatus.SUCCESS) {
                    paymentService.deliveryPayment(payment);
                }
                break;
            // Fulfil the customer's purchase

            case "payment_intent.payment_failed":
                System.out.println("Failed: " + intent.getId());
                break;
            // Notify the customer that payment failed

            default:
                // Handle other event types
                break;
        }
    }

    private void completePayment(Payment payment, Payment.PaymentStatus status) {
        payment.setStatus(status);
        paymentService.save(payment);
    }
}
