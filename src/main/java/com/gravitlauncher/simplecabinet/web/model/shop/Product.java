package com.gravitlauncher.simplecabinet.web.model.shop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
    @SequenceGenerator(name = "product_generator", sequenceName = "product_seq", allocationSize = 1)
    private long id;
    @Setter
    @Column(name = "display_name")
    private String displayName;
    @Setter
    private String description;
    @Setter
    @Column(name = "picture_url")
    private String pictureUrl;
    @Setter
    private String currency;
    @Setter
    private double price;

    // Limitations
    @Setter
    @Column(name = "end_data")
    private LocalDateTime endDate;
    @Setter
    private long count;
    @Setter
    @Column(name = "group_name")
    private String groupName;
    @Setter
    private boolean available;

    public record ProductLimitations(LocalDateTime endDate, long count, String groupName) {
    }

    public ProductLimitations getLimitations() {
        return new ProductLimitations(endDate, count, groupName);
    }
}
