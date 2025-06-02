package com.gravitlauncher.simplecabinet.web.model;

import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserBalance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "BalanceTransaction")
@Table(name = "balance_transactions")
public class BalanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_transactions_generator")
    @SequenceGenerator(name = "balance_transactions_generator", sequenceName = "balance_transactions_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    @ManyToOne
    @JoinColumn(name = "from_id")
    private UserBalance from;
    @Setter
    @ManyToOne
    @JoinColumn(name = "to_id")
    private UserBalance to;
    @Setter
    @Column(name = "from_count")
    private double fromCount;
    @Setter
    @Column(name = "to_count")
    private double toCount;
    @Setter
    private boolean multicurrency;
    @Setter
    private String comment;
    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
