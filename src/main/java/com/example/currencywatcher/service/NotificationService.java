package com.example.currencywatcher.service;


import com.example.currencywatcher.domain.AlertsEntity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${app.mail.from}")
    private String from;

    public void sendAlert(AlertsEntity alert, BigDecimal price) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(alert.getEmail());
        msg.setFrom(from);
        msg.setSubject("ALERT");
        msg.setText(buildMessage(alert, price));
        sender.send(msg);
        logger.info("Email sent to={}", alert.getEmail());
    }

    private String buildMessage(AlertsEntity alert, BigDecimal price) {

        return """
                ALERT!!!
                base: %s
                quote: %s
                
                target_price: %s is reached, cur_price: %s
                """.formatted(
                alert.getBase(),
                alert.getQuote(),
                alert.getTargetPrice(),
                price
        );
    }

}
