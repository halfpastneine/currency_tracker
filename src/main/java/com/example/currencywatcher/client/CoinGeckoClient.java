package com.example.currencywatcher.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CoinGeckoClient implements PriceClient{

    private final RestClient client;
    private final String apiKey;

    public CoinGeckoClient(RestClient.Builder builder,
                           @Value("${app.api.c_g-url}") String url,
                           @Value("${app.api.c-api-key:}") String apiKey) {
        this.client = builder.baseUrl(url).build();
        this.apiKey = apiKey;
    }

    @Override
    public BigDecimal fetch(String base, String quote) {
        String coinId = base.toLowerCase(Locale.ROOT);
        String quoteId = quote.toLowerCase(Locale.ROOT);

        try {
            Map<?, ?> body = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/simple/price")
                            .queryParam("ids", coinId)
                            .queryParam("vs_currencies", quoteId)
                            .build())
                    .header("x-cg-pro-api-key", apiKey)
                    .retrieve()
                    .body(Map.class);

            Object coinData = body.get(coinId);

            if (!(coinData instanceof Map<?,?> coinMap)) {
                throw new IllegalArgumentException();
            }

            Object rawPrice = coinMap.get(quoteId);

            return new BigDecimal(rawPrice.toString());



        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return new BigDecimal(-1);
    }
}
