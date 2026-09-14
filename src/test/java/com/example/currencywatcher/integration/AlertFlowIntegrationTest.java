package com.example.currencywatcher.integration;

import com.example.currencywatcher.domain.AlertsEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.repository.AlertsRepository;
import com.example.currencywatcher.repository.CurrentPriceRepository;
import com.example.currencywatcher.service.AlertSchedulerService;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@Testcontainers
@SpringBootTest
class AlertFlowIntegrationTest {

    private static final String EUR_USD_PATH = "/v2/rate/eur/usd";
    private static final String EUR_USD_RESPONSE =
            "{\"date\":\"2026-09-13\",\"base\":\"EUR\",\"quote\":\"USD\",\"rate\":1.30}";
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static HttpServer stubExternalApi;

    @BeforeAll
    static void startStub() throws IOException {
        stubExternalApi = HttpServer.create(new InetSocketAddress(0), 0);
        stubExternalApi.createContext(EUR_USD_PATH, exchange -> {
            byte[] bytes = EUR_USD_RESPONSE.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        stubExternalApi.start();
    }

    @AfterAll
    static void stopStub() {
        stubExternalApi.stop(0);
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        String stubUrl = "http://localhost:" + stubExternalApi.getAddress().getPort();
        registry.add("app.api.ff-url", () -> stubUrl);
        registry.add("app.api.c_g-url", () -> stubUrl);
        registry.add("app.api.c-api-key", () -> "test-key");
        registry.add("app.mail.monitoring_email", () -> "dev@mail.com");
        registry.add("app.mail.from", () -> "test@example.com");
        registry.add("app.alerts", () -> "3600000");
        registry.add("app.alerts.initial-delay", () -> "999999999");
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "3025");
        registry.add("spring.mail.username", () -> "test");
        registry.add("spring.mail.password", () -> "test");
        registry.add("management.health.mail.enabled", () -> "false");
    }

    @MockitoBean private JavaMailSender javaMailSender;

    @Autowired private AlertsRepository alertsRepository;
    @Autowired private CurrentPriceRepository currentPriceRepository;
    @Autowired private AlertSchedulerService scheduler;

    @Test
    void alertIsTriggeredAndDeactivatedWhenPriceCrossesTarget() {
        AlertsEntity alert = alertsRepository.save(new AlertsEntity(
                Type.FIAT, "Frankfurter", "eur", "usd", true, new BigDecimal("1.20"), true, "user@test.com"));

        scheduler.checkAlerts();

        AlertsEntity reloaded = alertsRepository.findById(alert.getId()).orElseThrow();
        assertThat(reloaded.getActive()).isFalse();
        assertThat(currentPriceRepository.findAll()).isNotEmpty();
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}