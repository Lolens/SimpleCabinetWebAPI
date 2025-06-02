package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "balance")
public class UserBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_generator")
    @SequenceGenerator(name = "balance_generator", sequenceName = "balance_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    private double balance;
    @Setter
    private String currency;

}
