package com.marketplace.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductoDTO {
    private Integer productoId;
    
    @NotNull(message = "El artista es obligatorio")
    private Integer artistaId;
    
    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;
    
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    private String rutaArchivo;
    private String tipoArchivo;
    private String urlMiniatura;
    private String urlVistaPrevia;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUpdate;
    
    // Para respuestas
    private String nombreArtista;
    private String nombreCategoria;
}