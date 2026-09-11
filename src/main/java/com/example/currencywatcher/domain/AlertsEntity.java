package com.example.currencywatcher.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class AlertsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "currency_type", nullable = false, length = 64)
    private String currencyType;

    @Column(name = "target_price", nullable = false, precision = 24, scale = 8)
    private BigDecimal targetPrice;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "last_trigger", nullable = false)
    private Instant lastTrigger;

    public AlertsEntity(Long id, String email, String currencyType, BigDecimal targetPrice, Boolean active, Instant lastTrigger) {
        this.id = id;
        this.email = email;
        this.currencyType = currencyType;
        this.targetPrice = targetPrice;
        this.active = active;
        this.lastTrigger = lastTrigger;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public Boolean isActive() {
        return active;
    }

    public Instant getLastTrigger() {
        return lastTrigger;
    }

    public void setLastTrigger(Instant lastTrigger) {
        this.lastTrigger = lastTrigger;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
