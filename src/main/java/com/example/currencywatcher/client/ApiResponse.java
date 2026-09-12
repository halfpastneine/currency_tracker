package com.example.currencywatcher.client;

import java.math.BigDecimal;

public record ApiResponse(String apiName, BigDecimal price) {}
