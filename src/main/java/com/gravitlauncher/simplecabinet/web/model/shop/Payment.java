package com.gravitlauncher.simplecabinet.web.model.shop;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_generator")
    @SequenceGenerator(name = "payments_generator", sequenceName = "payments_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    private double sum;
    @Setter
    private String system;
    @Setter
    @Column(name = "system_payment_id")
    private String systemPaymentId;
    @Setter
    private PaymentStatus status;

    public enum PaymentStatus {
        INITIATED, SUCCESS, CANCELED, ERROR
    }
}
