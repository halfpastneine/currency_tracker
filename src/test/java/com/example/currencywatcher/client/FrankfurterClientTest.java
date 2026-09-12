package com.example.currencywatcher.client;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class FrankfurterClientTest {


    @Test
    void fetchReturnsRate() {
        ClientErrorHandler clientErrorHandler = new ClientErrorHandler();
        FrankfurterClient client = new FrankfurterClient(RestClient.builder(), "https://api.frankfurter.dev", clientErrorHandler);
        var result = client.fetch("eur", "rub");
        System.out.println("======" + result + "======");
        assertThat(result.price()).isPositive();
    }
}
