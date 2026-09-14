package com.example.currencywatcher.service;

import com.example.currencywatcher.client.ClientErrorType;
import com.example.currencywatcher.client.ClientException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MonitoringServiceTest {

    private ClientException error(ClientErrorType type) {
        return new ClientException("msg", "API", 500, type, "q");
    }

    private JavaMailSender createSender(ClientErrorType type) {
        JavaMailSender sender = mock(JavaMailSender.class);
        MonitoringService service = new MonitoringService(sender, "dev@example.com");

        service.reportError(error(type));
        return sender;
    }

    @Test
    void serverErrorSendEmail() {
        verify(createSender(ClientErrorType.SERVER_ERROR), times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void clientDoesNotSendMail() {
        verifyNoInteractions(createSender(ClientErrorType.CLIENT_ERROR));
    }

    @Test
    void doesNotThrowIfMailFails() {
        JavaMailSender sender = mock(JavaMailSender.class);
        doThrow(new MailSendException("ERROR!!!!")).when(sender).send(any(SimpleMailMessage.class));
        MonitoringService service = new MonitoringService(sender, "dev@example.com");

        Assertions.assertDoesNotThrow(() -> service.reportError(error(ClientErrorType.SERVER_ERROR)));
    }
}