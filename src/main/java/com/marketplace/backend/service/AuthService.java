package com.marketplace.backend.service;

import com.marketplace.backend.dto.AuthResponse;
import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.exception.BadRequestException;
import com.marketplace.backend.model.Billetera;
import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.BilleteraRepository;
import com.marketplace.backend.repository.UsuarioRepository;
import com.marketplace.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BilleteraRepository billeteraRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getEmail());
        
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setClaveHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario creado con ID: {}", usuario.getUsuarioId());

        // Crear billetera automáticamente para CLIENTE y ARTISTA
        if (usuario.getRol() == Usuario.Rol.CLIENTE || usuario.getRol() == Usuario.Rol.ARTISTA) {
            Billetera billetera = new Billetera();
            billetera.setUsuario(usuario);
            billetera.setSaldo(BigDecimal.ZERO);
            billeteraRepository.save(billetera);
            log.info("Billetera creada automáticamente para usuario ID: {}", usuario.getUsuarioId());
        }

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getUsuarioId(),
                usuario.getRol().name()
        );

        return new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getUsuarioId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login: {}", request.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getUsuarioId(),
                usuario.getRol().name()
        );

        log.info("Login exitoso para usuario: {}", request.getEmail());

        return new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getUsuarioId()
        );
    }
}