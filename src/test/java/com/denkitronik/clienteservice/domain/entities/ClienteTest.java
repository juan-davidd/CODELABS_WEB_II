package com.denkitronik.clienteservice.domain.entities;

import java.util.Date;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ClienteTest {

    @Test
    void gettersSettersYCallbackDePersistencia() {
        Cliente cliente = new Cliente();
        Date createAt = new Date(1_000L);
        Region region = new Region();

        cliente.setId(4L);
        cliente.setNombre("Ada");
        cliente.setApellido("Lovelace");
        cliente.setEmail("ada@example.com");
        cliente.setCreateAt(createAt);
        cliente.setFoto("ada.png");
        cliente.setRegion(region);

        assertEquals(4L, cliente.getId());
        assertEquals("Ada", cliente.getNombre());
        assertEquals("Lovelace", cliente.getApellido());
        assertEquals("ada@example.com", cliente.getEmail());
        assertSame(createAt, cliente.getCreateAt());
        assertEquals("ada.png", cliente.getFoto());
        assertSame(region, cliente.getRegion());

        cliente.prePersist();

        assertNotNull(cliente.getCreateAt());
        assertEquals(Date.class, cliente.getCreateAt().getClass());
    }
}
