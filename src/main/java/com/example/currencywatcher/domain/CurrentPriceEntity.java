package com.example.currencywatcher.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "current_price")
public class CurrentPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_type", nullable = false, length = 64)
    private String currencyType;

    @Column(name = "current_price", nullable = false, precision = 24, scale = 8)
    private BigDecimal currentPrice;

    @Column(name = "time_fetched", nullable = false)
    private Instant timeFetched;

    public CurrentPriceEntity(Long id, String currencyType, BigDecimal currentPrice, Instant timeFetched) {
        this.id = id;
        this.currencyType = currencyType;
        this.currentPrice = currentPrice;
        this.timeFetched = timeFetched;
    }

    public Long getId() {
        return id;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public Instant getTimeFetched() {
        return timeFetched;
    }
}
