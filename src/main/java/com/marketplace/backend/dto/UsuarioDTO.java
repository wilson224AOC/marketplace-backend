package com.marketplace.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Integer usuarioId;
    private String email;
    private String rol;
    private LocalDateTime fechaCreacion;
}