package com.example.currencywatcher.service;


import com.example.currencywatcher.domain.AlertsEntity;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

import java.math.BigDecimal;

@Service
public class NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender sender;

    public NotificationService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendAlert(AlertsEntity alert, BigDecimal price) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(alert.getEmail());
            msg.setSubject("ALERT");
            msg.setText(buildMessage(alert));
            sender.send(msg);
            logger.info("Email sent to={}", alert.getEmail());
        } catch (RuntimeException e) {
            logger.error("Couldn't send message to={}", alert.getEmail());
        }
    }

    private String buildMessage(AlertsEntity alert) {

        return """
                ALERT!!!
                base: %s
                quote: %s
                
                price: %s
                """.formatted(
                alert.getBase(),
                alert.getQuote(),
                alert.getTargetPrice()
        );
    }

}
