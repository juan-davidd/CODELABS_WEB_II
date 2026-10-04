package com.denkitronik.clienteservice.domain.exception;

public class ClienteNotFoundException extends RuntimeException {

    private final Long id;

    public ClienteNotFoundException(Long id) {
        super("Cliente no encontrado con id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}