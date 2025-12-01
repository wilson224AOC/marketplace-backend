package com.marketplace.backend.service;

import com.marketplace.backend.dto.ResenaDTO;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.Producto;
import com.marketplace.backend.model.Resena;
import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.ProductoRepository;
import com.marketplace.backend.repository.ResenaRepository;
import com.marketplace.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ResenaDTO> getResenasByProducto(Integer productoId) {
        return resenaRepository.findByProductoProductoId(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResenaDTO> getResenasByUsuario(Integer usuarioId) {
        return resenaRepository.findByUsuarioUsuarioId(usuarioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResenaDTO createResena(ResenaDTO resenaDTO, Integer usuarioId) {
        Producto producto = productoRepository.findById(resenaDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Resena resena = new Resena();
        resena.setProducto(producto);
        resena.setUsuario(usuario);
        resena.setCalificacion(resenaDTO.getCalificacion());
        resena.setComentario(resenaDTO.getComentario());

        resena = resenaRepository.save(resena);
        return convertToDTO(resena);
    }

    @Transactional
    public void deleteResena(Integer id) {
        if (!resenaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reseña no encontrada con id: " + id);
        }
        resenaRepository.deleteById(id);
    }

    private ResenaDTO convertToDTO(Resena resena) {
        ResenaDTO dto = new ResenaDTO();
        dto.setResenaId(resena.getResenaId());
        dto.setProductoId(resena.getProducto().getProductoId());
        dto.setTituloProducto(resena.getProducto().getTitulo());
        dto.setUsuarioId(resena.getUsuario().getUsuarioId());
        dto.setNombreUsuario(resena.getUsuario().getEmail());
        dto.setCalificacion(resena.getCalificacion());
        dto.setComentario(resena.getComentario());
        dto.setFecha(resena.getFecha());
        return dto;
    }
}