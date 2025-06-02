package com.gravitlauncher.simplecabinet.web.model.shop;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Order<T extends Product> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_generator")
    @SequenceGenerator(name = "orders_generator", sequenceName = "orders_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    private long quantity;
    @Setter
    private OrderStatus status;
    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    public abstract T getProduct();

    public enum OrderStatus {
        INITIATED, DELIVERY
    }
}
