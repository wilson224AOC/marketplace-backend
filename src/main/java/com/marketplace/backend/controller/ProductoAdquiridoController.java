package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ProductoAdquiridoDTO;
import com.marketplace.backend.service.ProductoAdquiridoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos-adquiridos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ProductoAdquiridoController {

    private final ProductoAdquiridoService productoAdquiridoService;

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<ProductoAdquiridoDTO>> getProductosAdquiridosByUsuario(
            @PathVariable Integer usuarioId) {
        log.info("Obteniendo productos adquiridos del usuario: {}", usuarioId);
        return ResponseEntity.ok(productoAdquiridoService.getProductosAdquiridosByUsuario(usuarioId));
    }

    @GetMapping("/descargar/{productoId}")
@PreAuthorize("hasAnyRole('CLIENTE', 'ARTISTA', 'ADMIN')")
public ResponseEntity<Resource> descargarProducto(
        @PathVariable Integer productoId,
        Authentication authentication) {
    
    log.info("=== INICIO DESCARGA ===");
    log.info("Producto ID: {}", productoId);
    log.info("Usuario: {}", authentication.getName());
    
    try {
        Resource archivo = productoAdquiridoService.descargarProducto(productoId, authentication);
        
        String fileName = archivo.getFilename() != null ? archivo.getFilename() : "producto";
        log.info("Archivo encontrado: {}", fileName);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + fileName + "\"")
                .body(archivo);
                
    } catch (Exception e) {
        log.error("ERROR al descargar producto: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}