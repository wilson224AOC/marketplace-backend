package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ProductoDTO;
import com.marketplace.backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        log.info("Obteniendo todos los productos (Admin)");
        return ResponseEntity.ok(productoService.getAllProductos());
    }

    @GetMapping("/publicos")
    public ResponseEntity<List<ProductoDTO>> getProductosPublicados() {
        log.info("Obteniendo productos publicados");
        return ResponseEntity.ok(productoService.getProductosPublicados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Integer id) {
        log.info("Obteniendo producto por ID: {}", id);
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<ProductoDTO>> getProductosByArtista(@PathVariable Integer artistaId) {
        log.info("Obteniendo productos del artista: {}", artistaId);
        return ResponseEntity.ok(productoService.getProductosByArtista(artistaId));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> getProductosByCategoria(@PathVariable Integer categoriaId) {
        log.info("Obteniendo productos de la categoría: {}", categoriaId);
        return ResponseEntity.ok(productoService.getProductosByCategoria(categoriaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<ProductoDTO> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Creando nuevo producto: {}", productoDTO.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.createProducto(productoDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<ProductoDTO> updateProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Actualizando producto: {}", id);
        return ResponseEntity.ok(productoService.updateProducto(id, productoDTO));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDTO> cambiarEstadoProducto(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        
        // Log detallado para debugging
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("=== CAMBIAR ESTADO PRODUCTO ===");
        log.info("Producto ID: {}", id);
        log.info("Usuario autenticado: {}", auth != null ? auth.getName() : "null");
        log.info("Authorities: {}", auth != null ? auth.getAuthorities() : "null");
        log.info("Body recibido: {}", body);
        
        String nuevoEstado = body.get("estado");
        log.info("Nuevo estado: {}", nuevoEstado);
        
        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            log.error("Estado vacío o nulo");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            ProductoDTO resultado = productoService.cambiarEstadoProducto(id, nuevoEstado);
            log.info("Estado cambiado exitosamente a: {}", nuevoEstado);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al cambiar estado: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        log.info("Eliminando producto: {}", id);
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}