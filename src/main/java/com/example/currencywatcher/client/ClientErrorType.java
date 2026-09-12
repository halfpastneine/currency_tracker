package com.example.currencywatcher.client;

public enum ClientErrorType {
    API_CONFIGURATION_ERROR(true),
    RATE_LIMIT(true),
    CLIENT_ERROR(false),
    SERVER_ERROR(true),
    TIMEOUT_ERROR(true),
    INVALID_RESPONSE(true),
    UNKNOWN(true);


    private final boolean sendEmailToDeveloper;

    ClientErrorType(boolean sendEmailToDeveloper) {
        this.sendEmailToDeveloper = sendEmailToDeveloper;
    }

    public boolean sendEmailToDeveloper() {
        return sendEmailToDeveloper;
    }
}
