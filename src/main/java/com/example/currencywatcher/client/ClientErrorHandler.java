package com.example.currencywatcher.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;

@Component
public class ClientErrorHandler {


    public ClientException fromRestClientException(String apiName, String query, RestClientException e) {
        Integer httpCode = null;
        ClientErrorType errorType;

        if (e instanceof RestClientResponseException re) {
            httpCode = re.getStatusCode().value();
            errorType = getHttpError(httpCode);
        } else if (e instanceof ResourceAccessException) {
            errorType = getNetError(e);
        } else {
            errorType = ClientErrorType.UNKNOWN;
        }

        String errorMessage = buildMessage(apiName, httpCode, errorType, query);

        return new ClientException(
                errorMessage, e, apiName, httpCode, errorType, query
        );

    }

    private String buildMessage(String apiName, Integer httpCode, ClientErrorType errorType, String query) {
        String httpError = httpCode == null ? "" : String.format("(HTTP %s)", httpCode);

        return String.format(
                "API: %s, catch error: \"%s\" on query %s %s",
                apiName, errorType, query, httpError
        );
    }

    public ClientException invalidApi(String apiName, String query) {
        return new ClientException(
                buildMessage(apiName, null, ClientErrorType.API_CONFIGURATION_ERROR, query),
                apiName, null, ClientErrorType.API_CONFIGURATION_ERROR, query
        );
    }

    public ClientException invalidResponse(String apiName, String query) {
        return new ClientException(
                buildMessage(apiName, null, ClientErrorType.INVALID_RESPONSE, query),
                apiName, null, ClientErrorType.INVALID_RESPONSE, query
        );
    }

    public ClientException clientError(String apiName, String query) {
        return new ClientException(
                buildMessage(apiName, null, ClientErrorType.CLIENT_ERROR, query),
                apiName, null, ClientErrorType.CLIENT_ERROR, query
        );
    }



    private ClientErrorType getHttpError(Integer httpCode) {
        switch (httpCode) {
            case 400 -> {
                return ClientErrorType.CLIENT_ERROR;
            }
            case 401, 403 -> {
                return ClientErrorType.API_CONFIGURATION_ERROR;
            }
            case 408 -> {
                return ClientErrorType.TIMEOUT_ERROR;
            }
            case 429 -> {
                return ClientErrorType.RATE_LIMIT;
            }
            case 500 -> {
                return ClientErrorType.SERVER_ERROR;
            }
            default -> {
                return ClientErrorType.UNKNOWN;
            }
        }
    }

    private ClientErrorType getNetError(Throwable re) {
        if (hasCause(re, SocketTimeoutException.class)
                || hasCause(re, HttpTimeoutException.class)
                || hasCause(re, HttpConnectTimeoutException.class)
        ) {
            return ClientErrorType.TIMEOUT_ERROR;
        }

        return ClientErrorType.UNKNOWN;
    }

    private boolean hasCause(Throwable re, Class<?> errorClass) {
        Throwable throwable = re;
        do {
            if (errorClass.isInstance(throwable)) return true;
            throwable = throwable.getCause();

        } while (throwable != null);

        return false;
    }


}
