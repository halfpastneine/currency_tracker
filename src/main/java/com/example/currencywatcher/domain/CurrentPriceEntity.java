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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Type type;

    @Column(name = "api_name", nullable = false, length = 64)
    private String apiName;

    @Column(nullable = false, length = 64)
    private String base;

    @Column(nullable = false, length = 64)
    private String quote;

    @Column(name = "current_price", nullable = false, precision = 24, scale = 8)
    private BigDecimal currentPrice;

    @Column(name = "time_fetched", nullable = false)
    private Instant timeFetched;

    protected CurrentPriceEntity() {}

    public CurrentPriceEntity(Type type, String apiName, String base, String quote, BigDecimal currentPrice, Instant timeFetched) {
        this.type = type;
        this.apiName = apiName;
        this.base = base;
        this.quote = quote;
        this.currentPrice = currentPrice;
        this.timeFetched = timeFetched;
    }

    public Long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getApiName() {
        return apiName;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public Instant getTimeFetched() {
        return timeFetched;
    }
}
