package com.example.currencywatcher.client;

import com.example.currencywatcher.domain.Type;

import java.math.BigDecimal;

public interface PriceClient {

    ApiResponse fetch(String base, String quote);
    Type is();
    String getApiName();
}
