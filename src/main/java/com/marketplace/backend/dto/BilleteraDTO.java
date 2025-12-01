package com.marketplace.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BilleteraDTO {
    private Integer billeteraId;
    private BigDecimal saldo;
    private LocalDateTime fechaUpdate;
    private Integer usuarioId;
}