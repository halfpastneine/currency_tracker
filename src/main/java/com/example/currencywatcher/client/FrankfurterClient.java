package com.example.currencywatcher.client;


import com.example.currencywatcher.domain.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Locale;

@Component
public class FrankfurterClient implements PriceClient {

    private record FrankfurterResponse(String date, String base, String quote, BigDecimal rate) {}

    private final RestClient client;
    private final ClientErrorHandler clientErrorHandler;
    private final String API_NAME = "Frankfurter";

    public FrankfurterClient(RestClient.Builder builder, @Value("${app.api.ff-url}") String url, ClientErrorHandler clientErrorHandler) {
        this.client = builder.baseUrl(url).build();
        this.clientErrorHandler = clientErrorHandler;
    }

    @Override
    public ApiResponse fetch(String base, String quote) {
        base = base.toLowerCase(Locale.ROOT);
        quote = quote.toLowerCase(Locale.ROOT);
        String query = String.format("{%s}/{%s}", base, quote);

        try {
            FrankfurterResponse response = client.get()
                    .uri("/v2/rate/{base}/{quote}", base, quote)
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (response == null) {
                throw clientErrorHandler.invalidResponse(API_NAME, query);
            }

            if (response.rate() == null) {
                throw clientErrorHandler.invalidResponse(API_NAME, query);
            }

            return new ApiResponse(API_NAME, response.rate());
        } catch (RestClientException re) {
            throw clientErrorHandler.fromRestClientException(API_NAME, query, re);
        }
    }

    @Override
    public Type is() {
        return Type.FIAT;
    }

    @Override
    public String getApiName() {
        return API_NAME;
    }
}
