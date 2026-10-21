package com.denkitronik.clienteservice.domain.services;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import com.denkitronik.clienteservice.domain.exception.ClienteNotFoundException;
import com.denkitronik.clienteservice.domain.exception.ClienteServiceException;
import com.denkitronik.clienteservice.domain.repositories.IClienteDao;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private IClienteDao clienteDao;

    @InjectMocks
    private ClienteServiceImpl service;

    @Test
    void delegaListadoPaginadoAlRepositorio() {
        PageRequest pageable = PageRequest.of(1, 4);
        Page<Cliente> expected = new PageImpl<>(List.of(cliente()));
        when(clienteDao.findAll(pageable)).thenReturn(expected);

        assertSame(expected, service.findAll(pageable));
    }

    @Test
    void delegaListadoCompletoAlRepositorio() {
        List<Cliente> expected = List.of(cliente());
        when(clienteDao.findAll()).thenReturn(expected);

        assertSame(expected, service.findAll());
    }

    @Test
    void encuentraClienteExistente() {
        Cliente expected = cliente();
        when(clienteDao.findById(7L)).thenReturn(Optional.of(expected));

        assertSame(expected, service.findById(7L));
    }

    @Test
    void lanzaNotFoundAlBuscarClienteInexistente() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> service.findById(99L));

        assertEquals(99L, exception.getId());
    }

    @Test
    void guardaCliente() {
        Cliente cliente = cliente();
        when(clienteDao.save(cliente)).thenReturn(cliente);

        assertSame(cliente, service.save(cliente));
    }

    @Test
    void convierteViolacionDeIntegridadAlGuardar() {
        Cliente cliente = cliente();
        DataIntegrityViolationException cause =
                new DataIntegrityViolationException("duplicate");
        when(clienteDao.save(cliente)).thenThrow(cause);

        ClienteServiceException exception = assertThrows(
                ClienteServiceException.class,
                () -> service.save(cliente));

        assertEquals(
                "Error al guardar el cliente: datos duplicados o restricción violada",
                exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void actualizaCamposYPreservaFechaDeCreacion() {
        Cliente actual = cliente();
        Date createAt = new Date(1_000L);
        actual.setCreateAt(createAt);
        Cliente cambios = cliente();
        cambios.setNombre("Grace");
        cambios.setApellido("Hopper");
        cambios.setEmail("grace@example.com");
        cambios.setFoto("grace.png");
        Region region = new Region();
        cambios.setRegion(region);
        when(clienteDao.findById(7L)).thenReturn(Optional.of(actual));
        when(clienteDao.save(actual)).thenReturn(actual);

        Cliente updated = service.update(7L, cambios);

        assertSame(actual, updated);
        assertEquals("Grace", actual.getNombre());
        assertEquals("Hopper", actual.getApellido());
        assertEquals("grace@example.com", actual.getEmail());
        assertEquals("grace.png", actual.getFoto());
        assertSame(region, actual.getRegion());
        assertSame(createAt, actual.getCreateAt());
    }

    @Test
    void noActualizaClienteInexistente() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class,
                () -> service.update(99L, cliente()));
        verify(clienteDao, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void eliminaClienteExistente() {
        when(clienteDao.existsById(7L)).thenReturn(true);

        service.delete(7L);

        verify(clienteDao).deleteById(7L);
    }

    @Test
    void noEliminaClienteInexistente() {
        when(clienteDao.existsById(99L)).thenReturn(false);

        assertThrows(ClienteNotFoundException.class, () -> service.delete(99L));
        verify(clienteDao, never()).deleteById(99L);
    }

    @Test
    void devuelveTodasLasRegiones() {
        List<Region> expected = List.of(new Region());
        when(clienteDao.findAllRegiones()).thenReturn(expected);

        assertSame(expected, service.findAllRegiones());
    }

    private static Cliente cliente() {
        Cliente cliente = new Cliente();
        cliente.setId(7L);
        cliente.setNombre("Ada");
        cliente.setApellido("Lovelace");
        cliente.setEmail("ada@example.com");
        cliente.setFoto("ada.png");
        cliente.setRegion(new Region());
        return cliente;
    }
}
