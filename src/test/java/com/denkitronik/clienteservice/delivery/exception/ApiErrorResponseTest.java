package com.denkitronik.clienteservice.delivery.exception;

import java.beans.Introspector;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiErrorResponseTest {

    @Test
    void exponeLaPropiedadMensajeEsperadaPorElJson() throws Exception {
        ApiErrorResponse response = new ApiErrorResponse(
                404,
                "Not Found",
                "Cliente no encontrado con id: 999999"
        );

        String[] propertyNames = Arrays.stream(
                        Introspector.getBeanInfo(ApiErrorResponse.class).getPropertyDescriptors())
                .map(property -> property.getName())
                .toArray(String[]::new);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Cliente no encontrado con id: 999999", response.getMensaje());
        assertTrue(Arrays.asList(propertyNames).contains("mensaje"));
        assertFalse(Arrays.asList(propertyNames).contains("message"));
    }
}
