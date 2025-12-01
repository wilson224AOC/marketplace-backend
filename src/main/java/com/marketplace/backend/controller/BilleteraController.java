package com.marketplace.backend.controller;

import com.marketplace.backend.dto.BilleteraDTO;
import com.marketplace.backend.service.BilleteraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/billeteras")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class BilleteraController {

    private final BilleteraService billeteraService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ARTISTA', 'ADMIN')")
    public ResponseEntity<BilleteraDTO> getBilleteraById(@PathVariable Integer id) {
        log.info("Obteniendo billetera por ID: {}", id);
        return ResponseEntity.ok(billeteraService.getBilleteraById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ARTISTA', 'ADMIN')")
    public ResponseEntity<BilleteraDTO> getBilleteraByUsuarioId(@PathVariable Integer usuarioId) {
        log.info("Obteniendo billetera del usuario ID: {}", usuarioId);
        return ResponseEntity.ok(billeteraService.getBilleteraByUsuarioId(usuarioId));
    }

    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BilleteraDTO> createBilletera(@PathVariable Integer usuarioId) {
        log.info("Creando billetera para usuario ID: {}", usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(billeteraService.createBilletera(usuarioId));
    }

    @PostMapping("/usuario/{usuarioId}/cargar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ARTISTA')")
    public ResponseEntity<BilleteraDTO> cargarSaldo(
            @PathVariable Integer usuarioId,
            @RequestBody Map<String, BigDecimal> body) {
        
        log.info("Cargando saldo (simulado) al usuario ID: {}", usuarioId);
        log.info("Body recibido: {}", body);
        
        BigDecimal monto = body.get("monto");
        
        if (monto == null) {
            log.error("Monto vacío o nulo");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            BilleteraDTO resultado = billeteraService.cargarSaldo(usuarioId, monto);
            log.info("Saldo cargado exitosamente. Nuevo saldo: {}", resultado.getSaldo());
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al cargar saldo: {}", e.getMessage(), e);
            throw e;
        }
    }
}