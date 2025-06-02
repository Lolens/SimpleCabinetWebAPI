package com.gravitlauncher.simplecabinet.web.model.shop;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "item_delivery")
public class ItemDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_delivery_generator")
    @SequenceGenerator(name = "item_delivery_generator", sequenceName = "item_delivery_seq", allocationSize = 1)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "item_name")
    private String itemName;
    @Column(name = "item_extra")
    private String itemExtra;
    @Column(name = "item_enchants")
    private String itemEnchants;
    @Column(name = "item_nbt")
    private String itemNbt;
    private long part;
    private boolean completed;

}
