package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ResenaDTO;
import com.marketplace.backend.security.JwtUtil;
import com.marketplace.backend.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resenas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ResenaController {

    private final ResenaService resenaService;
    private final JwtUtil jwtUtil;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaDTO>> getResenasByProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(resenaService.getResenasByProducto(productoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaDTO>> getResenasByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(resenaService.getResenasByUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<ResenaDTO> createResena(
            @Valid @RequestBody ResenaDTO resenaDTO,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);
        Integer usuarioId = ((Number) jwtUtil.extractClaim(jwt, claims -> claims.get("usuarioId"))).intValue();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resenaService.createResena(resenaDTO, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResena(@PathVariable Integer id) {
        resenaService.deleteResena(id);
        return ResponseEntity.noContent().build();
    }
}