package com.marketplace.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAdquiridoDTO {
    
    private Integer licenciaId;
    private Integer usuarioId;
    private String emailUsuario;
    private Integer productoId;
    private String tituloProducto;
    private String descripcionProducto;
    private BigDecimal precioProducto;
    private String urlMiniatura;
    private String urlVistaPrevia;
    private String rutaArchivo;
    private String tipoArchivo;
    private String nombreCategoria;
    private Integer artistaId;
    private String nombreArtista;
    private LocalDateTime fechaAdquisicion;
    private Integer ventaId;
    private BigDecimal precioVenta;
}