package com.marketplace.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "artista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artista_id")
    private Integer artistaId;
    
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    
    @Column(nullable = false, unique = true, length = 20)
    private String dni;
    
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Column(length = 20)
    private String telefono;
    
    private String domicilio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Especialidad especialidad;
    
    @Column(columnDefinition = "TEXT")
    private String biografia;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarios_usuario_id", nullable = false)
    private Usuario usuario;
    
    public enum Especialidad {
        MUSICO, DEV, DISENADOR, OTRO
    }
}