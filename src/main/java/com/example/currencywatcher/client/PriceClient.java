package com.example.currencywatcher.client;

import java.math.BigDecimal;

public interface PriceClient {

    ApiResponse fetch(String base, String quote);
}
