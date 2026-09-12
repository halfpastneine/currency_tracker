package com.example.currencywatcher.service;

import com.example.currencywatcher.client.ClientErrorType;
import com.example.currencywatcher.client.ClientException;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MonitoringServiceTest {

    @Test
    void sendEmail() {
//        JavaMailSender sender = new JavaMailSenderImpl();
//
//        MonitoringService service = new MonitoringService(sender, "yibep76074@liondapt.com");
//
//        service.reportError(makeException(ClientErrorType.SERVER_ERROR, 500));
//        verify(sender).send(any(SimpleMailMessage.class));
    }

    ClientException makeException(ClientErrorType type, Integer httpCode) {
        return new ClientException(
                "TEST API",
                "COIN GECKO", httpCode, type, "test"
        );
    }
}
