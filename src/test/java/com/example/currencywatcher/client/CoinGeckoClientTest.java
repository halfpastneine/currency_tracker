package com.example.currencywatcher.client;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class CoinGeckoClientTest {


    @Test
    void fetchReturnsRate() {
        CoinGeckoClient client = new CoinGeckoClient(RestClient.builder(), "https://api.coingecko.com", "CG-QbjpP9hofdGZKrNvzxdcpP4U");
        var result = client.fetch("bitcoin", "usd");
        System.out.println("======" + result + "======");
        assertThat(result).isPositive();
    }
}
