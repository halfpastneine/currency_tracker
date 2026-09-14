package com.example.currencywatcher.service;

import com.example.currencywatcher.domain.AlertsEntity;
import com.example.currencywatcher.domain.Type;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private AlertsEntity alert() {
        return new AlertsEntity(Type.FIAT, "Frankfurter", "eur", "usd",
                true, new BigDecimal("1.2"), true, "user@test.com");
    }

    @Test
    void alertWorks() {
        JavaMailSender sender = mock(JavaMailSender.class);
        NotificationService service = new NotificationService(sender);

        service.sendAlert(alert(), new BigDecimal("1.25"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        assertThat(captor.getValue().getTo()).containsExactly("user@test.com");
    }
}