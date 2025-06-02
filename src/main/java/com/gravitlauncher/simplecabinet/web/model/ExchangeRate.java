package com.gravitlauncher.simplecabinet.web.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_rates_generator")
    @SequenceGenerator(name = "exchange_rates_generator", sequenceName = "exchange_rates_seq", allocationSize = 1)
    private long id;
    @Setter
    private String fromCurrency;
    @Setter
    private String toCurrency;
    @Setter
    private double value;

}
