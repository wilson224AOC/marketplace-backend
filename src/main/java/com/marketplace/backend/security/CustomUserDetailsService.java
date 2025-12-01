package com.marketplace.backend.security;

import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // IMPORTANTE: Agregar el prefijo ROLE_ si no lo tiene
        String role = usuario.getRol().name();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        
        log.info("Usuario cargado: {} con rol: {}", email, role);

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getClaveHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }
}