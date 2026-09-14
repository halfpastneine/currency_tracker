package com.example.currencywatcher.client;

import com.example.currencywatcher.service.MonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class CoinGeckoClientTest {

    private static final String BASE_URL = "https://api.coingecko.com";
    private static final String API_HEADER = "x-cg-demo-api-key";
    private static final String API_MOCK_KEY = "test-key";

    private MockRestServiceServer server;

    private CoinGeckoClient buildClient() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        MonitoringService monitoringService = mock(MonitoringService.class);
        return new CoinGeckoClient(builder, BASE_URL, API_MOCK_KEY, new ClientErrorHandler(monitoringService));
    }

    private String buildURL(String base, String quote) {
        return String.format(
                "%s/api/v3/simple/price?ids=%s&vs_currencies=%s",
                BASE_URL,
                base,
                quote
        );
    }

    @Test
    void fetchCGWorks() {
        CoinGeckoClient client = buildClient();
        server.expect(requestTo(buildURL("bitcoin", "usd")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(API_HEADER, API_MOCK_KEY))
                .andRespond(withSuccess("{\"bitcoin\":{\"usd\":65000.5}}", MediaType.APPLICATION_JSON));

        ApiResponse response = client.fetch("bitcoin", "usd");

        assertThat(response.apiName()).isEqualTo("COIN GECKO");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("65000.5"));
        server.verify();
    }

    @Test
    void fetchCGThrowsClientError() {
        CoinGeckoClient client = buildClient();
        server.expect(requestTo(buildURL("not_exist", "usd")))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("not_exist", "usd"))
                .isInstanceOf(ClientException.class)
                .extracting(e -> ((ClientException) e).getClientErrorType())
                .isEqualTo(ClientErrorType.CLIENT_ERROR);
    }

    @Test
    void fetchCGThrowsServerError() {
        CoinGeckoClient client = buildClient();
        server.expect(requestTo(buildURL("bitcoin", "usd")))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.fetch("bitcoin", "usd"))
                .isInstanceOf(ClientException.class)
                .extracting(e -> ((ClientException) e).getClientErrorType())
                .isEqualTo(ClientErrorType.SERVER_ERROR);
    }

    @Test
    void fetchCGThrowsInvalidResponse() {
        CoinGeckoClient client = buildClient();
        server.expect(requestTo(buildURL("bitcoin", "usd")))
                .andRespond(withSuccess("{\"bitcoin\":{}}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("bitcoin", "usd"))
                .isInstanceOf(ClientException.class)
                .extracting(e -> ((ClientException) e).getClientErrorType())
                .isEqualTo(ClientErrorType.INVALID_RESPONSE);
    }
}