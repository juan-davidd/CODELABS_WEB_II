package com.denkitronik.clienteservice.domain.services;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {

    /**
     * Lista todos los clientes con paginación y ordenamiento.
     */
    Page<Cliente> findAll(Pageable pageable);

    /**
     * Devuelve todos los clientes (sin paginación — útil para exportar o procesar todos).
     */
    List<Cliente> findAll();

    /**
     * Busca un cliente por su ID.
     */
    Cliente findById(Long id);

    /**
     * Persiste un nuevo cliente.
     */
    Cliente save(Cliente cliente);

    /**
     * Actualiza los campos de un cliente existente preservando createAt.
     */
    Cliente update(Long id, Cliente cliente);

    /**
     * Elimina un cliente por su ID.
     */
    void delete(Long id);

    /**
     * Devuelve todas las regiones disponibles.
     */
    List<Region> findAllRegiones();
}