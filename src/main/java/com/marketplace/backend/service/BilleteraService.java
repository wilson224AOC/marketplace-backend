package com.marketplace.backend.service;

import com.marketplace.backend.dto.BilleteraDTO;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.Billetera;
import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.BilleteraRepository;
import com.marketplace.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BilleteraService {

    private final BilleteraRepository billeteraRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public BilleteraDTO getBilleteraByUsuarioId(Integer usuarioId) {
        log.info("Obteniendo billetera del usuario ID: {}", usuarioId);
        
        Billetera billetera = billeteraRepository.findByUsuarioUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billetera no encontrada para el usuario ID: " + usuarioId));
        
        return convertToDTO(billetera);
    }

    @Transactional(readOnly = true)
    public BilleteraDTO getBilleteraById(Integer billeteraId) {
        log.info("Obteniendo billetera por ID: {}", billeteraId);
        
        Billetera billetera = billeteraRepository.findById(billeteraId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billetera no encontrada con ID: " + billeteraId));
        
        return convertToDTO(billetera);
    }

    @Transactional
    public BilleteraDTO createBilletera(Integer usuarioId) {
        log.info("Creando billetera para usuario ID: {}", usuarioId);
        
        // Verificar que no exista ya una billetera para ese usuario
        if (billeteraRepository.findByUsuarioUsuarioId(usuarioId).isPresent()) {
            throw new IllegalStateException("El usuario ya tiene una billetera");
        }
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + usuarioId));
        
        Billetera billetera = new Billetera();
        billetera.setUsuario(usuario);
        billetera.setSaldo(BigDecimal.ZERO);
        
        Billetera savedBilletera = billeteraRepository.save(billetera);
        log.info("Billetera creada exitosamente con ID: {}", savedBilletera.getBilleteraId());
        
        return convertToDTO(savedBilletera);
    }

    @Transactional
    public BilleteraDTO cargarSaldo(Integer usuarioId, BigDecimal monto) {
        log.info("Cargando saldo (simulado) al usuario ID: {} - Monto: {}", usuarioId, monto);
        
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        
        // Validar que el monto no sea excesivo (opcional)
        if (monto.compareTo(new BigDecimal("10000")) > 0) {
            throw new IllegalArgumentException("El monto máximo a cargar es S/ 10,000");
        }
        
        Billetera billetera = billeteraRepository.findByUsuarioUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billetera no encontrada para el usuario ID: " + usuarioId));
        
        BigDecimal nuevoSaldo = billetera.getSaldo().add(monto);
        billetera.setSaldo(nuevoSaldo);
        
        Billetera updatedBilletera = billeteraRepository.save(billetera);
        log.info("Saldo cargado exitosamente. Nuevo saldo: {}", nuevoSaldo);
        
        return convertToDTO(updatedBilletera);
    }

    @Transactional
    public BilleteraDTO descontarSaldo(Integer usuarioId, BigDecimal monto) {
        log.info("Descontando saldo del usuario ID: {} - Monto: {}", usuarioId, monto);
        
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        
        Billetera billetera = billeteraRepository.findByUsuarioUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billetera no encontrada para el usuario ID: " + usuarioId));
        
        BigDecimal nuevoSaldo = billetera.getSaldo().subtract(monto);
        
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Saldo insuficiente. Saldo actual: S/ " + billetera.getSaldo() + 
                ", Se requiere: S/ " + monto
            );
        }
        
        billetera.setSaldo(nuevoSaldo);
        Billetera updatedBilletera = billeteraRepository.save(billetera);
        
        log.info("Saldo descontado exitosamente. Nuevo saldo: {}", nuevoSaldo);
        
        return convertToDTO(updatedBilletera);
    }

    private BilleteraDTO convertToDTO(Billetera billetera) {
        BilleteraDTO dto = new BilleteraDTO();
        dto.setBilleteraId(billetera.getBilleteraId());
        dto.setSaldo(billetera.getSaldo());
        dto.setFechaUpdate(billetera.getFechaUpdate());
        dto.setUsuarioId(billetera.getUsuario().getUsuarioId());
        return dto;
    }
}