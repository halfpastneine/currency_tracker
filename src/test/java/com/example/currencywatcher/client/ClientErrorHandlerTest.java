package com.example.currencywatcher.client;

import com.example.currencywatcher.service.MonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ClientErrorHandlerTest {

    MonitoringService monitoringService = mock(MonitoringService.class);
    private final ClientErrorHandler handler = new ClientErrorHandler(monitoringService);

    private HttpClientErrorException clientError(HttpStatus status) {
        return HttpClientErrorException.create(status, status.getReasonPhrase(),
                HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);
    }

    @Test
    void http400IsClientError() {
        assertThat(handler.fromRestClientException("TEST", "test", clientError(HttpStatus.BAD_REQUEST))
                .getClientErrorType()).isEqualTo(ClientErrorType.CLIENT_ERROR);
    }

    @Test
    void http401And403AreConfigurationErrors() {
        assertThat(handler.fromRestClientException("TEST", "test", clientError(HttpStatus.UNAUTHORIZED))
                .getClientErrorType()).isEqualTo(ClientErrorType.API_CONFIGURATION_ERROR);
        assertThat(handler.fromRestClientException("TEST", "test", clientError(HttpStatus.FORBIDDEN))
                .getClientErrorType()).isEqualTo(ClientErrorType.API_CONFIGURATION_ERROR);
    }

    @Test
    void http429IsRateLimit() {
        assertThat(handler.fromRestClientException("TEST", "test", clientError(HttpStatus.TOO_MANY_REQUESTS))
                .getClientErrorType()).isEqualTo(ClientErrorType.RATE_LIMIT);
    }

    @Test
    void http500IsServerError() {
        var cause = HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR,
                "err", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);
        var e = handler.fromRestClientException("TEST", "test", cause);
        assertThat(e.getClientErrorType()).isEqualTo(ClientErrorType.SERVER_ERROR);
    }

    @Test
    void http422IsClientError() {
        var e = handler.fromRestClientException("Frankfurter", "test",
                clientError(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(e.getClientErrorType()).isEqualTo(ClientErrorType.CLIENT_ERROR);
    }

    @Test
    void timeoutError() {
        var timeout = new ResourceAccessException("timeout", new SocketTimeoutException("read timed out"));
        var e = handler.fromRestClientException("TEST", "test", timeout);
        assertThat(e.getClientErrorType()).isEqualTo(ClientErrorType.TIMEOUT_ERROR);
    }


    @Test
    void invalidAPIError() {
        var e = handler.invalidApi("TEST", "test");
        assertThat(e.getClientErrorType()).isEqualTo(ClientErrorType.API_CONFIGURATION_ERROR);
        assertThat(e.getMessage()).contains("TEST").contains("test");
    }
}