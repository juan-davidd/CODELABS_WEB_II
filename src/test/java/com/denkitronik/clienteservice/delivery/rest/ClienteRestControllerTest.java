package com.denkitronik.clienteservice.delivery.rest;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import com.denkitronik.clienteservice.domain.services.IClienteService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteRestControllerTest {

    @Mock
    private IClienteService clienteService;

    @InjectMocks
    private ClienteRestController controller;

    @Test
    void listaClientes() {
        List<Cliente> clientes = List.of(cliente());
        when(clienteService.findAll()).thenReturn(clientes);

        var response = controller.listarClientes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(clientes, response.getBody());
    }

    @Test
    void listaClientesPaginadosConTamanoCuatro() {
        Page<Cliente> clientes = new PageImpl<>(List.of(cliente()));
        when(clienteService.findAll(PageRequest.of(2, 4))).thenReturn(clientes);

        var response = controller.listarClientesPaginado(2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(clientes, response.getBody());
        verify(clienteService).findAll(PageRequest.of(2, 4));
    }

    @Test
    void buscaClientePorId() {
        Cliente cliente = cliente();
        when(clienteService.findById(7L)).thenReturn(cliente);

        var response = controller.buscarCliente(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(cliente, response.getBody());
    }

    @Test
    void creaClienteValido() {
        Cliente cliente = cliente();
        when(clienteService.save(cliente)).thenReturn(cliente);

        var response = controller.crearCliente(cliente, noErrors());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(cliente, response.getBody());
    }

    @Test
    void devuelveErroresDeCamposAlCrearClienteInvalido() {
        BindingResult result = errors("nombre", "El nombre es obligatorio");

        var response = controller.crearCliente(cliente(), result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = assertInstanceOf(java.util.Map.class, response.getBody());
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("El nombre es obligatorio",
                assertInstanceOf(java.util.Map.class, body.get("errors")).get("nombre"));
    }

    @Test
    void convierteViolacionesDePersistenciaAlCrearCliente() {
        ConstraintViolation<Object> violation = org.mockito.Mockito.mock(ConstraintViolation.class);
        Path path = org.mockito.Mockito.mock(Path.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("email");
        when(violation.getMessage()).thenReturn("El email ya existe");

        var exception = new ConstraintViolationException(Set.of(violation));
        Cliente cliente = cliente();
        when(clienteService.save(cliente)).thenThrow(exception);

        var response = controller.crearCliente(cliente, noErrors());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = assertInstanceOf(java.util.Map.class, response.getBody());
        assertEquals("El email ya existe",
                assertInstanceOf(java.util.Map.class, body.get("errors")).get("email"));
    }

    @Test
    void devuelveErroresDeCamposAlActualizarClienteInvalido() {
        var response = controller.actualizarCliente(
                7L, cliente(), errors("email", "El email no es válido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = assertInstanceOf(java.util.Map.class, response.getBody());
        assertEquals("El email no es válido",
                assertInstanceOf(java.util.Map.class, body.get("errors")).get("email"));
    }

    @Test
    void actualizaClienteValido() {
        Cliente cliente = cliente();
        when(clienteService.update(7L, cliente)).thenReturn(cliente);

        var response = controller.actualizarCliente(7L, cliente, noErrors());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(cliente, response.getBody());
    }

    @Test
    void eliminaClienteYDevuelveNoContent() {
        var response = controller.eliminarCliente(7L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clienteService).delete(7L);
    }

    @Test
    void listaRegiones() {
        List<Region> regiones = List.of(new Region());
        when(clienteService.findAllRegiones()).thenReturn(regiones);

        var response = controller.listarRegiones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(regiones, response.getBody());
    }

    private static BindingResult noErrors() {
        BindingResult result = org.mockito.Mockito.mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        return result;
    }

    private static BindingResult errors(String field, String message) {
        BindingResult result = org.mockito.Mockito.mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(List.of(
                new FieldError("cliente", field, message)
        ));
        return result;
    }

    private static Cliente cliente() {
        Cliente cliente = new Cliente();
        cliente.setId(7L);
        cliente.setNombre("Ada");
        cliente.setApellido("Lovelace");
        cliente.setEmail("ada@example.com");
        cliente.setRegion(new Region());
        return cliente;
    }
}
