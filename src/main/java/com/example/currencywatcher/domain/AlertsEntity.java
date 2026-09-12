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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Type type;

    @Column(name = "api_name", nullable = false, length = 64)
    private String apiName;

    @Column(nullable = false, length = 64)
    private String base;

    @Column(nullable = false, length = 64)
    private String quote;

    @Column(nullable = false)
    private boolean up;

    @Column(name = "target_price", nullable = false, precision = 24, scale = 8)
    private BigDecimal targetPrice;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, length = 255)
    private String email;



    protected AlertsEntity() {}

    public AlertsEntity(Type type, String apiName, String base, String quote, boolean up, BigDecimal targetPrice, boolean active, String email) {
        this.type = type;
        this.apiName = apiName;
        this.base = base;
        this.quote = quote;
        this.up = up;
        this.targetPrice = targetPrice;
        this.active = active;
        this.email = email;
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

    public boolean isUp() {
        return up;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public String getEmail() {
        return email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
