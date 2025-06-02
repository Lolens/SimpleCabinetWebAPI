package com.gravitlauncher.simplecabinet.web.dto.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.Payment;

public class PaymentDto {
    public final long id;
    public final String system;
    public final String systemPaymentId;
    public final double sum;

    public PaymentDto(Payment entity) {
        this.id = entity.getId();
        this.system = entity.getSystem();
        this.systemPaymentId = entity.getSystemPaymentId();
        this.sum = entity.getSum();
    }
}
