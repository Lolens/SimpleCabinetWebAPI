package com.gravitlauncher.simplecabinet.web.dto.shop;

import com.gravitlauncher.simplecabinet.web.model.shop.Product;

public class ItemProductDto {
    public final String server;
    public final long id;
    public final double price;
    public final String currency;
    public final String displayName;
    public final String description;
    public final String pictureUrl;
    // Limitations
    public final Product.ProductLimitations limitations;
    public ItemProductDto(long id,String server, double price, String currency, String displayName, String description, String pictureUrl, Product.ProductLimitations limitations) {
        this.id = id;
        this.price = price;
        this.server= server;
        this.currency = currency;
        this.displayName = displayName;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.limitations = limitations;
    }
}
