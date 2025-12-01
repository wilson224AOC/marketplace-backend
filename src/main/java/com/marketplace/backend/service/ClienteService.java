package com.marketplace.backend.service;

import com.marketplace.backend.dto.ClienteDTO;
import com.marketplace.backend.exception.BadRequestException;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.Cliente;
import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.ClienteRepository;
import com.marketplace.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteDTO getClienteByUsuarioId(Integer usuarioId) {
        Cliente cliente = clienteRepository.findByUsuarioUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado para el usuario: " + usuarioId));
        return convertToDTO(cliente);
    }

    @Transactional
    public ClienteDTO createCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByDni(clienteDTO.getDni())) {
            throw new BadRequestException("Ya existe un cliente con este DNI");
        }

        Usuario usuario = usuarioRepository.findById(clienteDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cliente cliente = new Cliente();
        cliente.setNombreCompleto(clienteDTO.getNombreCompleto());
        cliente.setDni(clienteDTO.getDni());
        cliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setDomicilio(clienteDTO.getDomicilio());
        cliente.setUsuario(usuario);

        cliente = clienteRepository.save(cliente);
        return convertToDTO(cliente);
    }

    @Transactional
    public ClienteDTO updateCliente(Integer usuarioId, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findByUsuarioUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (!cliente.getDni().equals(clienteDTO.getDni()) && 
            clienteRepository.existsByDni(clienteDTO.getDni())) {
            throw new BadRequestException("Ya existe un cliente con este DNI");
        }

        cliente.setNombreCompleto(clienteDTO.getNombreCompleto());
        cliente.setDni(clienteDTO.getDni());
        cliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setDomicilio(clienteDTO.getDomicilio());

        cliente = clienteRepository.save(cliente);
        return convertToDTO(cliente);
    }

    @Transactional
    public void deleteCliente(Integer clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        clienteRepository.deleteById(clienteId);
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setClienteId(cliente.getClienteId());
        dto.setNombreCompleto(cliente.getNombreCompleto());
        dto.setDni(cliente.getDni());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setTelefono(cliente.getTelefono());
        dto.setDomicilio(cliente.getDomicilio());
        dto.setUsuarioId(cliente.getUsuario().getUsuarioId());
        return dto;
    }
}