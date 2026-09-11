package com.example.currencywatcher.client;

import java.math.BigDecimal;

public interface PriceClient {

    BigDecimal fetch(String base, String quote);
}
