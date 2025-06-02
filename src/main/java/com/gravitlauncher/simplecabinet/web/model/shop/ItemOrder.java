package com.gravitlauncher.simplecabinet.web.model.shop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_orders")
public class ItemOrder extends Order<ItemProduct> {
    private long quantity;
    @Setter
    @Getter
    @Column(name = "custom_params")
    private String customParams;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ItemProduct product;

    @Override
    public long getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

}
