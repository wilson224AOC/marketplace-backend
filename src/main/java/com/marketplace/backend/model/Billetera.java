package com.marketplace.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billetera")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billetera {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billetera_id")
    private Integer billeteraId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;
    
    @UpdateTimestamp
    @Column(name = "fecha_update", nullable = false)
    private LocalDateTime fechaUpdate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarios_usuario_id", nullable = false)
    private Usuario usuario;
}