package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ArtistaDTO;
import com.marketplace.backend.service.ArtistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artistas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ArtistaController {

    private final ArtistaService artistaService;

    @GetMapping
    public ResponseEntity<List<ArtistaDTO>> getAllArtistas() {
        return ResponseEntity.ok(artistaService.getAllArtistas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaDTO> getArtistaById(@PathVariable Integer id) {
        return ResponseEntity.ok(artistaService.getArtistaById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ArtistaDTO> getArtistaByUsuarioId(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(artistaService.getArtistaByUsuarioId(usuarioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<ArtistaDTO> createArtista(@Valid @RequestBody ArtistaDTO artistaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artistaService.createArtista(artistaDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<ArtistaDTO> updateArtista(
            @PathVariable Integer id,
            @Valid @RequestBody ArtistaDTO artistaDTO) {
        return ResponseEntity.ok(artistaService.updateArtista(id, artistaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtista(@PathVariable Integer id) {
        artistaService.deleteArtista(id);
        return ResponseEntity.noContent().build();
    }
}