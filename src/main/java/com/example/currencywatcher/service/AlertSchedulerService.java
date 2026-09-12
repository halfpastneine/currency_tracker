package com.example.currencywatcher.service;


import com.example.currencywatcher.client.ClientException;
import com.example.currencywatcher.domain.CurrentPriceEntity;
import com.example.currencywatcher.repository.AlertsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AlertSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(AlertSchedulerService.class);

    private final AlertsRepository repository;
    private final PriceService priceService;
    private final NotificationService notificationService;
    private final MonitoringService monitoringService;

    public AlertSchedulerService(AlertsRepository repository, PriceService priceService, NotificationService notificationService, MonitoringService monitoringService) {
        this.repository = repository;
        this.priceService = priceService;
        this.notificationService = notificationService;
        this.monitoringService = monitoringService;
    }


    @Scheduled(fixedDelayString = "${app.alerts:60000}")
    public void checkAlerts() {
        var alerts = repository.findByActiveTrueOrderByIdAsc();
        if (alerts.isEmpty()) return;

        log.info("Checking current alerts");

        for (var alert : alerts) {
            try {
                CurrentPriceEntity curPrice = priceService.requestAndSave(
                        alert.getType(), alert.getBase(), alert.getQuote()
                );
                if (!alert.getActive()) {
                    continue;
                }

                if ((alert.isUp() && alert.getTargetPrice().compareTo(curPrice.getCurrentPrice()) <= 0)
                        || (!alert.isUp() && alert.getTargetPrice().compareTo(curPrice.getCurrentPrice()) >= 0)) {
                    notificationService.sendAlert(alert, curPrice.getCurrentPrice());
                    alert.setActive(false);
                    log.info("Alert was triggered, id = {}, base = {}, quote = {}, price = {}",
                            alert.getId(), alert.getBase(), alert.getQuote(), alert.getTargetPrice()
                    );
                }
            } catch (ClientException e) {
                log.error("Couldn't check alert id = {}, {}", alert.getId(), e.getMessage());
            } catch (RuntimeException e) {
                // TODO!()
            }
        }

    }


}
