package com.example.currencywatcher.controllers;

import com.example.currencywatcher.domain.AlertsEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.repository.AlertsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AlertsRepository repository;

    private String alertBody(String targetPrice, String email) {
        return """
                {"type":"CRYPTO","base":"bitcoin","quote":"usd","up":true,"targetPrice":%s,"email":"%s"}
                """.formatted(targetPrice, email);
    }

    private ResultActions postAlert(String body) throws Exception {
        return mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private AlertsEntity fiatAlert(String base) {
        return new AlertsEntity(Type.FIAT, "Frankfurter", base, "usd", true,
                new BigDecimal("1.2"), true, "a@b.com");
    }

    @Test
    void createWorks() throws Exception {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        postAlert(alertBody("70000", "a@b.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.base").value("bitcoin"))
                .andExpect(jsonPath("$.apiName").value("COIN GECKO"));
    }

    @Test
    void invalidEmailRejected() throws Exception {
        postAlert(alertBody("70000", "not-an-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativePriceRejected() throws Exception {
        postAlert(alertBody("-5", "a@b.com"))
                .andExpect(status().isBadRequest());
    }
}