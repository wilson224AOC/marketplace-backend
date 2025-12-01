package com.marketplace.backend.controller;

import com.marketplace.backend.dto.VentaDTO;
import com.marketplace.backend.security.JwtUtil;
import com.marketplace.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class VentaController {

    private final VentaService ventaService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VentaDTO>> getAllVentas() {
        log.info("Obteniendo todas las ventas (Admin)");
        return ResponseEntity.ok(ventaService.getAllVentas());
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<VentaDTO>> getVentasByComprador(@PathVariable Integer compradorId) {
        log.info("Obteniendo ventas del comprador: {}", compradorId);
        return ResponseEntity.ok(ventaService.getVentasByComprador(compradorId));
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<VentaDTO>> getVentasByVendedor(@PathVariable Integer vendedorId) {
        log.info("Obteniendo ventas del vendedor: {}", vendedorId);
        return ResponseEntity.ok(ventaService.getVentasByVendedor(vendedorId));
    }

    @PostMapping
    public ResponseEntity<VentaDTO> createVenta(
            @RequestBody Map<String, Integer> body,
            @RequestHeader("Authorization") String token) {
        
        log.info("=== CREAR VENTA ===");
        
        try {
            String jwt = token.substring(7);
            Integer usuarioId = ((Number) jwtUtil.extractClaim(jwt, claims -> claims.get("usuarioId"))).intValue();
            Integer productoId = body.get("productoId");
            
            log.info("Usuario ID: {}, Producto ID: {}", usuarioId, productoId);
            
            VentaDTO venta = ventaService.createVenta(productoId, usuarioId);
            log.info("Venta creada exitosamente: {}", venta.getVentaId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(venta);
        } catch (Exception e) {
            log.error("Error al crear venta: {}", e.getMessage(), e);
            throw e;
        }
    }
}