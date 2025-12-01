package com.marketplace.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Integer clienteId;
    
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    
    @Column(nullable = false, unique = true, length = 20)
    private String dni;
    
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Column(length = 20)
    private String telefono;
    
    private String domicilio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarios_usuario_id", nullable = false)
    private Usuario usuario;
}