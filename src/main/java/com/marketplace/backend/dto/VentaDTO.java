package com.marketplace.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VentaDTO {
    private Integer ventaId;
    private Integer productoId;
    private String tituloProducto;
    private Integer compradorId;
    private String emailComprador;
    private Integer vendedorId;
    private String nombreVendedor;
    private BigDecimal precioVenta;
    private BigDecimal comisionMarketplace;
    private LocalDateTime fechaVenta;
    private String idTransaccionGateway;
}