package com.denkitronik.clienteservice.delivery.exception;

import com.denkitronik.clienteservice.domain.exception.ClienteNotFoundException;
import com.denkitronik.clienteservice.domain.exception.ClienteServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejaClienteNoEncontrado() {
        var response = handler.manejarNotFound(new ClienteNotFoundException(17L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiErrorResponse body = response.getBody();
        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("Cliente no encontrado con id: 17", body.getMensaje());
    }

    @Test
    void manejaErrorDelServicioSinExponerDetallesInternos() {
        var response = handler.manejarServiceException(
                new ClienteServiceException("internal detail", new IllegalStateException()));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiErrorResponse body = response.getBody();
        assertEquals("Internal Server Error", body.getError());
        assertEquals("Error interno del servidor. Por favor intenta más tarde.",
                body.getMensaje());
    }

    @Test
    void devuelveErroresDeValidacionPorCampo() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "cliente");
        bindingResult.addError(new FieldError("cliente", "nombre", "El nombre es obligatorio"));
        bindingResult.addError(new FieldError("cliente", "email", "El email no es válido"));

        var response = handler.manejarValidacion(
                new MethodArgumentNotValidException(null, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El nombre es obligatorio", response.getBody().get("nombre"));
        assertEquals("El email no es válido", response.getBody().get("email"));
    }

    @Test
    void manejaExcepcionNoControladaConMensajeGenerico() {
        var response = handler.manejarExcepcionGenerica(
                new IllegalStateException("internal detail"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiErrorResponse body = assertInstanceOf(ApiErrorResponse.class, response.getBody());
        assertEquals("Error inesperado. Por favor contacta al administrador.",
                body.getMensaje());
    }
}
