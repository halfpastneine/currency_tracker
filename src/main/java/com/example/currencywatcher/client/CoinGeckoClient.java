package com.example.currencywatcher.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CoinGeckoClient implements PriceClient{

    private final RestClient client;
    private final String apiKey;
    private final ClientErrorHandler clientErrorHandler;
    private final String API_NAME = "COIN GECKO";

    public CoinGeckoClient(RestClient.Builder builder,
                           @Value("${app.api.c_g-url}") String url,
                           @Value("${app.api.c-api-key:}") String apiKey, ClientErrorHandler clientErrorHandler) {
        this.client = builder.baseUrl(url).build();
        this.apiKey = apiKey;
        this.clientErrorHandler = clientErrorHandler;
    }

    @Override
    public ApiResponse fetch(String base, String quote) {
        String coinId = base.toLowerCase(Locale.ROOT);
        String quoteId = quote.toLowerCase(Locale.ROOT);
        String query = String.format("{%s}/{%s}", coinId, quoteId);

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

            if (body == null) {
                throw clientErrorHandler.invalidResponse(API_NAME, query);
            }

            Object coinData = body.get(coinId);

            if (!(coinData instanceof Map<?,?> coinMap)) {
                throw clientErrorHandler.clientError(API_NAME, query);
            }

            Object price = coinMap.get(quoteId);

            if (!(price instanceof Number) && !(price instanceof String)) {
                throw clientErrorHandler.invalidResponse(API_NAME, query);
            }

            try {
                return new ApiResponse(API_NAME, new BigDecimal(price.toString()));
            } catch (NumberFormatException e) {
                throw clientErrorHandler.invalidResponse(API_NAME, query);
            }

        } catch (RestClientException re) {
            throw clientErrorHandler.fromRestClientException(API_NAME, query, re);
        }

    }
}
