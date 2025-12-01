package com.marketplace.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ArtistaDTO {
    private Integer artistaId;
    
    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;
    
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
    
    private String telefono;
    private String domicilio;
    
    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;
    
    private String biografia;
    private Integer usuarioId;
}