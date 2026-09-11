package com.example.currencywatcher.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;

@Component
public class FrankfurterClient implements PriceClient {

    private record FrankfurterResponse(String date, String base, String quote, BigDecimal rate) {}

    private final RestClient client;

    public FrankfurterClient(RestClient.Builder builder, @Value("${app.api.ff-url}") String url) {
        this.client = builder.baseUrl(url).build();
    }

    @Override
    public BigDecimal fetch(String base, String quote) {
        base = base.toLowerCase(Locale.ROOT);
        quote = quote.toLowerCase(Locale.ROOT);

        try {
            FrankfurterResponse response = client.get()
                    .uri("/v2/rate/{base}/{quote}", base, quote)
                    .retrieve()
                    .body(FrankfurterResponse.class);



            return response.rate;
        } catch (Exception e) {


        }

        return new BigDecimal(-1);

    }
}
