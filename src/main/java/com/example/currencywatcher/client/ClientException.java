package com.example.currencywatcher.client;

public class ClientException extends RuntimeException {

    private final String apiName;
    private final Integer httpCode;
    private final ClientErrorType clientErrorType;
    private final String query;

    public ClientException(String message, String apiName, Integer httpCode, ClientErrorType clientErrorType, String query) {
        super(message);
        this.apiName = apiName;
        this.httpCode = httpCode;
        this.clientErrorType = clientErrorType;
        this.query = query;
    }

    public ClientException(String message, Throwable cause, String apiName, Integer httpCode, ClientErrorType clientErrorType, String query) {
        super(message, cause);
        this.apiName = apiName;
        this.httpCode = httpCode;
        this.clientErrorType = clientErrorType;
        this.query = query;
    }

    public String getApiName() {
        return apiName;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public ClientErrorType getClientErrorType() {
        return clientErrorType;
    }

    public String getQuery() {
        return query;
    }
}
