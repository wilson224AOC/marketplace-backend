package com.marketplace.backend.service;

import com.marketplace.backend.dto.ArtistaDTO;
import com.marketplace.backend.exception.BadRequestException;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.Artista;
import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.ArtistaRepository;
import com.marketplace.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ArtistaDTO> getAllArtistas() {
        return artistaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArtistaDTO getArtistaById(Integer id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + id));
        return convertToDTO(artista);
    }

    @Transactional(readOnly = true)
    public ArtistaDTO getArtistaByUsuarioId(Integer usuarioId) {
        Artista artista = artistaRepository.findByUsuarioUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado para el usuario: " + usuarioId));
        return convertToDTO(artista);
    }

    @Transactional
    public ArtistaDTO createArtista(ArtistaDTO artistaDTO) {
        if (artistaRepository.existsByDni(artistaDTO.getDni())) {
            throw new BadRequestException("Ya existe un artista con este DNI");
        }

        Usuario usuario = usuarioRepository.findById(artistaDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Artista artista = new Artista();
        artista.setNombreCompleto(artistaDTO.getNombreCompleto());
        artista.setDni(artistaDTO.getDni());
        artista.setFechaNacimiento(artistaDTO.getFechaNacimiento());
        artista.setTelefono(artistaDTO.getTelefono());
        artista.setDomicilio(artistaDTO.getDomicilio());
        artista.setEspecialidad(Artista.Especialidad.valueOf(artistaDTO.getEspecialidad()));
        artista.setBiografia(artistaDTO.getBiografia());
        artista.setUsuario(usuario);

        artista = artistaRepository.save(artista);
        return convertToDTO(artista);
    }

    @Transactional
    public ArtistaDTO updateArtista(Integer id, ArtistaDTO artistaDTO) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + id));

        if (!artista.getDni().equals(artistaDTO.getDni()) && 
            artistaRepository.existsByDni(artistaDTO.getDni())) {
            throw new BadRequestException("Ya existe un artista con este DNI");
        }

        artista.setNombreCompleto(artistaDTO.getNombreCompleto());
        artista.setDni(artistaDTO.getDni());
        artista.setFechaNacimiento(artistaDTO.getFechaNacimiento());
        artista.setTelefono(artistaDTO.getTelefono());
        artista.setDomicilio(artistaDTO.getDomicilio());
        artista.setEspecialidad(Artista.Especialidad.valueOf(artistaDTO.getEspecialidad()));
        artista.setBiografia(artistaDTO.getBiografia());

        artista = artistaRepository.save(artista);
        return convertToDTO(artista);
    }

    @Transactional
    public void deleteArtista(Integer id) {
        if (!artistaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artista no encontrado con id: " + id);
        }
        artistaRepository.deleteById(id);
    }

    private ArtistaDTO convertToDTO(Artista artista) {
        ArtistaDTO dto = new ArtistaDTO();
        dto.setArtistaId(artista.getArtistaId());
        dto.setNombreCompleto(artista.getNombreCompleto());
        dto.setDni(artista.getDni());
        dto.setFechaNacimiento(artista.getFechaNacimiento());
        dto.setTelefono(artista.getTelefono());
        dto.setDomicilio(artista.getDomicilio());
        dto.setEspecialidad(artista.getEspecialidad().name());
        dto.setBiografia(artista.getBiografia());
        dto.setUsuarioId(artista.getUsuario().getUsuarioId());
        return dto;
    }
}