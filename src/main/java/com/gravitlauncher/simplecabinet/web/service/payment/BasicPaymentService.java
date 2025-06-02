package com.gravitlauncher.simplecabinet.web.service.payment;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.service.shop.PaymentService;

public interface BasicPaymentService {
    boolean isEnabled();

    PaymentService.PaymentCreationInfo createBalancePayment(User user, double sum, String ip) throws Exception;
}
