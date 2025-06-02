package com.gravitlauncher.simplecabinet.web.model.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "service_products")
public class ServiceProduct extends Product {
    private ServiceType type;
    private boolean stackable;
    private int days;

    public enum ServiceType {
        CHANGE_PREFIX
    }
}
