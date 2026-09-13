package com.example.currencywatcher.controllers;

import com.example.currencywatcher.domain.CurrentPriceEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.service.PriceService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }


    @GetMapping("/price")
    public CurrentPriceEntity getPrice(
            @RequestParam Type type,
            @RequestParam @NotBlank String base,
            @RequestParam @NotBlank String quote
    ) {
        return priceService.save(priceService.fetch(type, base, quote));
    }


}
