package com.marketplace.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "productos_adquiridos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAdquirido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "licencia_id")
    private Integer licenciaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @CreationTimestamp
    @Column(name = "fecha_adquisicion", nullable = false, updatable = false)
    private LocalDateTime fechaAdquisicion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_venta_id", nullable = false)
    private Venta venta;
}