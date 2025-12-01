package com.marketplace.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResenaDTO {
    private Integer resenaId;
    
    @NotNull(message = "El producto es obligatorio")
    private Integer productoId;
    
    private Integer usuarioId;
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    
    private String comentario;
    private LocalDateTime fecha;
    
    // Para respuestas
    private String nombreUsuario;
    private String tituloProducto;
}