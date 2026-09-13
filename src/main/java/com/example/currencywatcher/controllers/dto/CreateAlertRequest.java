package com.example.currencywatcher.controllers.dto;

import com.example.currencywatcher.domain.Type;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateAlertRequest (

        @NotNull
        Type type,

        @NotBlank @Size(max = 64)
        String base,

        @NotBlank
        @Size(max = 64)
        String quote,

        boolean up,

        @NotNull
        @Positive
        BigDecimal targetPrice,

        @NotBlank
        @Email
        @Size(max = 255)
        String email
){}
