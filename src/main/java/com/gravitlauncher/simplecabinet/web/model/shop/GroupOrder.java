package com.gravitlauncher.simplecabinet.web.model.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "group_orders")
public class GroupOrder extends Order<GroupProduct> {
    private String server;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private GroupProduct product;

}
