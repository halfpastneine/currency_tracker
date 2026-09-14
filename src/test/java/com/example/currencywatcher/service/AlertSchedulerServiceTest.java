package com.example.currencywatcher.service;

import com.example.currencywatcher.client.ClientErrorType;
import com.example.currencywatcher.client.ClientException;
import com.example.currencywatcher.domain.AlertsEntity;
import com.example.currencywatcher.domain.CurrentPriceEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.repository.AlertsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertSchedulerServiceTest {

    @Mock
    private AlertsRepository repository;
    @Mock
    private PriceService priceService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MonitoringService monitoringService;

    private AlertSchedulerService scheduler;

    private AlertsEntity createAlert(BigDecimal target) {
        return new AlertsEntity(Type.CRYPTO,
                "COIN GECKO", "bitcoin",
                "usd", true, target, true, "user@test.com");
    }

    private CurrentPriceEntity price(BigDecimal value) {
        return new CurrentPriceEntity(Type.CRYPTO, "COIN GECKO", "bitcoin", "usd", value, Instant.now());
    }

    @Test
    void alertTrigger() {
        scheduler = new AlertSchedulerService(repository, priceService, notificationService, monitoringService);
        var alert = createAlert(new BigDecimal("60000"));
        when(repository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of(alert));
        when(priceService.save(priceService.fetch(Type.CRYPTO, "bitcoin", "usd")))
                .thenReturn(price(new BigDecimal("61000")));

        scheduler.checkAlerts();

        verify(notificationService).sendAlert(eq(alert), eq(new BigDecimal("61000")));
        assertThat(alert.getActive()).isFalse();
        verify(repository).save(alert);
    }

    @Test
    void alertNotTriggered() {
        scheduler = new AlertSchedulerService(repository, priceService, notificationService, monitoringService);
        var alert = createAlert(new BigDecimal("70000"));
        when(repository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of(alert));
        when(priceService.save(priceService.fetch(any(), any(), any())))
                .thenReturn(price(new BigDecimal("61000")));

        scheduler.checkAlerts();

        verifyNoInteractions(notificationService);
        verify(repository, never()).save(any());
    }

    @Test
    void failure___MyEnglishIsNotEnoughForThisTestName() {
        scheduler = new AlertSchedulerService(repository, priceService, notificationService, monitoringService);
        var failing = createAlert(new BigDecimal("60000"));
        var healthy = createAlert(new BigDecimal("60000"));
        when(repository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of(failing, healthy));

        when(priceService.save(priceService.fetch(eq(Type.CRYPTO), eq("bitcoin"), eq("usd"))))
                .thenThrow(new ClientException("boom", "COIN GECKO", 500, ClientErrorType.SERVER_ERROR, "q"))
                .thenReturn(price(new BigDecimal("61000")));

        scheduler.checkAlerts();

        verify(notificationService, times(1)).sendAlert(eq(healthy), any());
    }
}