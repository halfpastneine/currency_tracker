package com.example.currencywatcher.controllers;

import com.example.currencywatcher.controllers.dto.CreateAlertRequest;
import com.example.currencywatcher.domain.AlertsEntity;
import com.example.currencywatcher.repository.AlertsRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    private final AlertsRepository repository;

    public AlertController(AlertsRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertsEntity create(@Valid @RequestBody CreateAlertRequest request) {
        AlertsEntity alert = new AlertsEntity(
                request.type(),
                resolveApiName(request.type()),
                request.base(),
                request.quote(),
                request.up(),
                request.targetPrice(),
                true,
                request.email()
        );

        AlertsEntity saved = repository.save(alert);

        log.info("Created alert id = {}, base = {}, quote = {}, target_price = {}",
                saved.getId(), saved.getBase(), saved.getQuote(), saved.getTargetPrice());

        return saved;
    }

    @GetMapping
    public List<AlertsEntity> listActive() {
        return repository.findByActiveTrueOrderByIdAsc();
    }


    private String resolveApiName(com.example.currencywatcher.domain.Type type) {
        return switch (type) {
            case CRYPTO -> "CoinGecko";
            case FIAT -> "Frankfurter";
        };
    }
}
