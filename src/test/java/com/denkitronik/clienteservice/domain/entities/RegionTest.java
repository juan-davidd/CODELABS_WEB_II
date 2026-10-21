package com.denkitronik.clienteservice.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegionTest {

    @Test
    void gettersYSetters() {
        Region region = new Region();

        region.setId(3L);
        region.setNombre("Europa");

        assertEquals(3L, region.getId());
        assertEquals("Europa", region.getNombre());
    }
}
