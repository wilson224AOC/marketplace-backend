package com.marketplace.backend.service;

import com.marketplace.backend.dto.VentaDTO;
import com.marketplace.backend.exception.BadRequestException;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.*;
import com.marketplace.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoAdquiridoRepository productoAdquiridoRepository;
    private final BilleteraService billeteraService;
    
    private static final BigDecimal COMISION_PORCENTAJE = new BigDecimal("0.10"); // 10%

    @Transactional(readOnly = true)
    public List<VentaDTO> getAllVentas() {
        return ventaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VentaDTO> getVentasByComprador(Integer compradorId) {
        return ventaRepository.findByCompradorUsuarioId(compradorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VentaDTO> getVentasByVendedor(Integer vendedorId) {
        return ventaRepository.findByVendedorArtistaId(vendedorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VentaDTO createVenta(Integer productoId, Integer compradorId) {
        log.info("Procesando venta - Producto: {}, Comprador: {}", productoId, compradorId);
        
        // Validar producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (!producto.getEstado().equals(Producto.Estado.PUBLICADO)) {
            throw new BadRequestException("El producto no está disponible para venta");
        }

        // Validar comprador
        Usuario comprador = usuarioRepository.findById(compradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));

        // Validar que no sea el mismo artista
        if (producto.getArtista().getUsuario().getUsuarioId().equals(compradorId)) {
            throw new BadRequestException("No puedes comprar tu propio producto");
        }

        // Validar que no haya comprado antes
        if (productoAdquiridoRepository.existsByUsuarioUsuarioIdAndProductoProductoId(compradorId, productoId)) {
            throw new BadRequestException("Ya has adquirido este producto");
        }

        // ⭐ DESCONTAR SALDO DE LA BILLETERA ⭐
        try {
            billeteraService.descontarSaldo(compradorId, producto.getPrecio());
            log.info("Saldo descontado exitosamente de la billetera del usuario {}", compradorId);
        } catch (IllegalArgumentException e) {
            log.error("Error al descontar saldo: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        } catch (ResourceNotFoundException e) {
            log.error("Billetera no encontrada: {}", e.getMessage());
            throw new BadRequestException("No tienes una billetera activa. Por favor contacta al administrador.");
        }

        // Calcular comisión
        BigDecimal comision = producto.getPrecio().multiply(COMISION_PORCENTAJE);

        // Crear venta
        Venta venta = new Venta();
        venta.setProducto(producto);
        venta.setComprador(comprador);
        venta.setVendedor(producto.getArtista());
        venta.setPrecioVenta(producto.getPrecio());
        venta.setComisionMarketplace(comision);

        venta = ventaRepository.save(venta);
        log.info("Venta creada con ID: {}", venta.getVentaId());

        // Crear producto adquirido
        ProductoAdquirido productoAdquirido = new ProductoAdquirido();
        productoAdquirido.setUsuario(comprador);
        productoAdquirido.setProducto(producto);
        productoAdquirido.setVenta(venta);
        productoAdquiridoRepository.save(productoAdquirido);
        log.info("Producto adquirido registrado - Licencia ID: {}", productoAdquirido.getLicenciaId());

        return convertToDTO(venta);
    }

    private VentaDTO convertToDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setVentaId(venta.getVentaId());
        dto.setProductoId(venta.getProducto().getProductoId());
        dto.setTituloProducto(venta.getProducto().getTitulo());
        dto.setCompradorId(venta.getComprador().getUsuarioId());
        dto.setEmailComprador(venta.getComprador().getEmail());
        dto.setVendedorId(venta.getVendedor().getArtistaId());
        dto.setNombreVendedor(venta.getVendedor().getNombreCompleto());
        dto.setPrecioVenta(venta.getPrecioVenta());
        dto.setComisionMarketplace(venta.getComisionMarketplace());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setIdTransaccionGateway(venta.getIdTransaccionGateway());
        return dto;
    }
}