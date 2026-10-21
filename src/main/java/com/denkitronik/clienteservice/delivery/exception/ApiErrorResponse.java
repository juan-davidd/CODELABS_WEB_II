package com.denkitronik.clienteservice.delivery.exception;

public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String mensaje;

    public ApiErrorResponse(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
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
}