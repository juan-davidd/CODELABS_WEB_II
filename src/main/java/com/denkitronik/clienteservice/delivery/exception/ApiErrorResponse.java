package com.denkitronik.clienteservice.delivery.exception;

import java.time.Instant;

public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String mensaje;
    private final String timestamp;

    public ApiErrorResponse(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = Instant.now().toString();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getTimestamp() {
        return timestamp;
    }
}