package com.denkitronik.clienteservice.delivery.rest;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import com.denkitronik.clienteservice.domain.services.IClienteService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/${api.version}/cliente-service")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;

    // GET /clientes
    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    // GET /clientes/page/{page}
    @GetMapping("/clientes/page/{page}")
    public ResponseEntity<Page<Cliente>> listarClientesPaginado(@PathVariable Integer page) {
        Page<Cliente> clientes = clienteService.findAll(PageRequest.of(page, 4));
        return ResponseEntity.ok(clientes);
    }

    // GET /clientes/{id}
    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    // POST /clientes
    @PostMapping("/clientes")
    public ResponseEntity<?> crearCliente(
            @Valid @RequestBody Cliente cliente,
            BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage()));
            return respuestaValidacion(errores);
        }

        try {
            Cliente nuevo = clienteService.save(cliente);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevo);

        } catch (ConstraintViolationException ex) {
            Map<String, String> errores = new HashMap<>();
            ex.getConstraintViolations().forEach(error -> {
                errores.put(
                        error.getPropertyPath().toString(),
                        error.getMessage()
                );
            });

            return respuestaValidacion(errores);
        }
    }

    // PUT /clientes/{id}
    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente,
            BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage()));
            return respuestaValidacion(errores);
        }

        Cliente actualizado = clienteService.update(id, cliente);

        return ResponseEntity.ok(actualizado);
    }

    // DELETE /clientes/{id}
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // GET /clientes/regiones
    @GetMapping("/clientes/regiones")
    public ResponseEntity<List<Region>> listarRegiones() {
        List<Region> regiones = clienteService.findAllRegiones();

        return ResponseEntity.ok(regiones);
    }

    private ResponseEntity<Map<String, Object>> respuestaValidacion(Map<String, String> errores) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", 400);
        respuesta.put("error", "Bad Request");
        respuesta.put("message", "Error de validación");
        respuesta.put("errors", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }
}