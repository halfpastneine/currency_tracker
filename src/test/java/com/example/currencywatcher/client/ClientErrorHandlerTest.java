package com.example.currencywatcher.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClientErrorHandlerTest {

    @Test
    void classifiesHttp500AsServerError() {
        ClientErrorHandler handler = new ClientErrorHandler();

        var cause = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8);

        ClientException error = handler.fromRestClientException(
                "CoinGecko", "BTC/USD", cause);

        System.err.println(error.getMessage());

        assertThat(error.getClientErrorType()).isEqualTo(ClientErrorType.SERVER_ERROR);
        assertThat(error.getHttpCode()).isEqualTo(500);
    }
}
