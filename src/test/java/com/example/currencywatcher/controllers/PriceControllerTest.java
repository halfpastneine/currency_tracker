package com.example.currencywatcher.controllers;

import com.example.currencywatcher.client.ClientErrorType;
import com.example.currencywatcher.client.ClientException;
import com.example.currencywatcher.domain.CurrentPriceEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.service.PriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private PriceService priceService;

    private ResultActions getPrice(String type, String base, String quote) throws Exception {
        return mockMvc.perform(get("/api/price")
                .param("type", type)
                .param("base", base)
                .param("quote", quote));
    }

    @Test
    void returnsIfValid() throws Exception {
        when(priceService.save(priceService.fetch(Type.CRYPTO, "bitcoin", "usd")))
                .thenReturn(new CurrentPriceEntity(Type.CRYPTO, "COIN GECKO", "bitcoin", "usd",
                        new BigDecimal("65000"), Instant.now()));

        getPrice("CRYPTO", "bitcoin", "usd")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPrice").value(65000));
    }

    @Test
    void clientError() throws Exception {
        when(priceService.save(priceService.fetch(Type.FIAT, "eur", "xyz"))).thenThrow(
                new ClientException("bad currency", "Frankfurter", 422, ClientErrorType.UNKNOWN, "eur/xyz"));

        getPrice("FIAT", "eur", "xyz")
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.api").value("Frankfurter"));
    }
}