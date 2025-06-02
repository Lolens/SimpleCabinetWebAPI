package com.gravitlauncher.simplecabinet.web.model.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "item_products")
public class ItemProduct extends Product {
    // Delivery item data
    @Column(name = "item_name")
    private String itemName;
    @Column(name = "item_quantity")
    private int itemQuantity;
    @Column(name = "item_extra")
    private String itemExtra;
    @Column(name = "item_enchants")
    private String itemEnchants;
    @Column(name = "item_nbt")
    private String itemNbt;
    @Column(name = "item_custom")
    private String itemCustom;
    // Delivery server
    private String server;

}
