package com.example.currencywatcher.client;

import com.example.currencywatcher.service.MonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class FrankfurterClientTest {

    private static final String BASE_URL = "https://api.frankfurter.dev";

    private MockRestServiceServer server;

    private FrankfurterClient buildClient() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        MonitoringService monitoringService = mock(MonitoringService.class);
        return new FrankfurterClient(builder, BASE_URL, new ClientErrorHandler(monitoringService));
    }

    private String buildURL(String base, String quote) {
        return String.format("%s/v2/rate/%s/%s", BASE_URL, base, quote);
    }

    @Test
    void fetchFFWorks() {
        FrankfurterClient client = buildClient();
        server.expect(requestTo(buildURL("eur", "usd")))
                .andRespond(withSuccess(
                        "{\"date\":\"2026-09-01\",\"base\":\"EUR\",\"quote\":\"USD\",\"rate\":1.08}",
                        MediaType.APPLICATION_JSON));

        ApiResponse response = client.fetch("EUR", "USD");

        assertThat(response.apiName()).isEqualTo("Frankfurter");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("1.08"));
    }


    @Test
    void fetchFFThrowsClientError() {
        FrankfurterClient client = buildClient();
        server.expect(requestTo(buildURL("xxx", "usd")))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("{\"message\":\"invalid currency: XXX\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("xxx", "usd"))
                .isInstanceOf(ClientException.class)
                .extracting(e -> ((ClientException) e).getClientErrorType())
                .isEqualTo(ClientErrorType.CLIENT_ERROR);
    }

    @Test
    void fetchFFThrowsInvalidResponse() {
        FrankfurterClient client = buildClient();
        server.expect(requestTo(buildURL("eur", "usd")))
                .andRespond(withSuccess("{\"date\":\"2026-09-01\",\"base\":\"EUR\",\"quote\":\"USD\"}",
                        MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("EUR", "USD"))
                .isInstanceOf(ClientException.class)
                .extracting(e -> ((ClientException) e).getClientErrorType())
                .isEqualTo(ClientErrorType.INVALID_RESPONSE);
    }
}