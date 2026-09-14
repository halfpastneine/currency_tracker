package com.example.currencywatcher.repository;

import com.example.currencywatcher.domain.AlertsEntity;
import com.example.currencywatcher.domain.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AlertsRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private AlertsRepository repository;

    @Test
    void isDBWorking() {
        repository.save(new AlertsEntity(Type.CRYPTO, "COIN GECKO", "bitcoin", "usd",
                true, new BigDecimal("70000"), true, "a@b.com"));
        repository.save(new AlertsEntity(Type.FIAT, "Frankfurter", "eur", "usd",
                true, new BigDecimal("70000"), true, "a@b.com"));
        repository.save(new AlertsEntity(Type.FIAT, "Frankfurter", "eur", "usd",
                true, new BigDecimal("1.3"), false, "c@d.com"));

        var activeAlerts = repository.findByActiveTrueOrderByIdAsc();

        assertThat(activeAlerts).hasSize(2);
        assertThat(activeAlerts.get(0).getBase()).isEqualTo("bitcoin");
        assertThat(activeAlerts.get(1).getBase()).isEqualTo("eur");
    }
}