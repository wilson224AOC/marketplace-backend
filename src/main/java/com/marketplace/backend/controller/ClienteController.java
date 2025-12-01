package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ClienteDTO;
import com.marketplace.backend.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ClienteDTO> getClienteByUsuarioId(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(clienteService.getClienteByUsuarioId(usuarioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.createCliente(clienteDTO));
    }

    @PutMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<ClienteDTO> updateCliente(
            @PathVariable Integer usuarioId,
            @Valid @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(clienteService.updateCliente(usuarioId, clienteDTO));
    }

    @DeleteMapping("/{clienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCliente(@PathVariable Integer clienteId) {
        clienteService.deleteCliente(clienteId);
        return ResponseEntity.noContent().build();
    }
}