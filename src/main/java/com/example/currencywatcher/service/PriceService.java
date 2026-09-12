package com.example.currencywatcher.service;


import com.example.currencywatcher.client.ApiResponse;
import com.example.currencywatcher.client.PriceClient;
import com.example.currencywatcher.domain.CurrentPriceEntity;
import com.example.currencywatcher.domain.Type;
import com.example.currencywatcher.repository.CurrentPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {

    private final Map<Type, PriceClient> clients;
    private final CurrentPriceRepository currentPriceRepository;

    public PriceService(List<PriceClient> clients, CurrentPriceRepository currentPriceRepository) {
        this.clients = new HashMap<>();
        for (var client : clients) {
            this.clients.put(client.is(), client);
        }
        this.currentPriceRepository = currentPriceRepository;
    }

    @Transactional
    public CurrentPriceEntity requestAndSave(Type type, String base, String quote) {
        var client = clients.get(type);

        if (client == null) {
            throw new IllegalArgumentException("Unsupported client for type: " + type);
        }

        ApiResponse res = client.fetch(base, quote);

        CurrentPriceEntity currentPriceEntity = new CurrentPriceEntity(
                type,
                client.getApiName(),
                base,
                quote,
                res.price(),
                Instant.now());

        return currentPriceRepository.save(currentPriceEntity);
    }
}
