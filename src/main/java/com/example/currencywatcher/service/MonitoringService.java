package com.example.currencywatcher.service;

import com.example.currencywatcher.client.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class MonitoringService {
    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);

    private final JavaMailSender sender;
    private final String sendTo;
    private final Duration timeout;
    private final Map<String, Instant> lastSendMail = new HashMap<>();

    public MonitoringService(JavaMailSender sender,
                             @Value("${app.mail.monitoring_email}") String sendTo) {
        this.sender = sender;
        this.sendTo = sendTo;
        this.timeout = Duration.ofMinutes(10);;
    }

    public void reportError(ClientException error) {
        String logMessage = "External API error: api = {}, type = {}, query = {}, message = {}";
        String httpCode = error.getHttpCode() == null ? "-" : error.getHttpCode().toString();

        if (error.getClientErrorType().sendEmailToDeveloper()) {
            log.error(
                    logMessage,
                    error.getApiName(), error.getClientErrorType(), error.getQuery(), error.getMessage(), error
            );

            String problem = String.format("%s, %s. %s", error.getApiName(), error.getClientErrorType(), httpCode);

            String message =
                    """
                    ERROR!!!
                    
                    API: %s
                    Error: %s
                    HTTP code: %s
                    Query: %s
                    Message: %s
                    Time: %s
                    """.formatted(
                            error.getApiName(),
                            error.getClientErrorType(),
                            httpCode,
                            error.getQuery(),
                            error.getMessage(),
                            Instant.now()
                    );

            sendEmail(problem, message);

        } else {
            log.warn(
                    logMessage,
                    error.getApiName(), error.getClientErrorType(), error.getQuery(), error.getMessage(), error
            );
        }
    }


    private void sendEmail(String problem, String message) {

        Instant now = Instant.now();
        Instant lastSendMsg = lastSendMail.get(problem);

        if (lastSendMsg != null && lastSendMsg.plus(timeout).isAfter(now)) {
            log.warn("Error was already sent, wait until timout ends");
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(sendTo);
            msg.setSubject(problem);
            msg.setText(message);
            sender.send(msg);
            log.info("Monitoring email was sent to={}, problem={}", sendTo, problem);
        } catch (RuntimeException e) {
            log.error("Couldn't send monitoring message to={}, problem={}", sendTo, problem);
        }
    }
}
