package com.marketplace.backend.service;

import com.marketplace.backend.dto.ProductoAdquiridoDTO;
import com.marketplace.backend.exception.BadRequestException;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.Producto;
import com.marketplace.backend.model.ProductoAdquirido;
import com.marketplace.backend.model.Usuario;
import com.marketplace.backend.repository.ProductoAdquiridoRepository;
import com.marketplace.backend.repository.ProductoRepository;
import com.marketplace.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoAdquiridoService {

    private final ProductoAdquiridoRepository productoAdquiridoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Value("${app.upload.dir:uploads/productos/}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public List<ProductoAdquiridoDTO> getProductosAdquiridosByUsuario(Integer usuarioId) {
        log.info("Obteniendo productos adquiridos del usuario: {}", usuarioId);
        
        return productoAdquiridoRepository.findByUsuarioUsuarioId(usuarioId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource descargarProducto(Integer productoId, Authentication authentication) {
        log.info("═══════════════════════════════════════");
        log.info("=== INICIO PROCESO DE DESCARGA ===");
        log.info("═══════════════════════════════════════");
        log.info("Producto ID solicitado: {}", productoId);
        
        // Obtener el producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> {
                    log.error("❌ Producto no encontrado con ID: {}", productoId);
                    return new ResourceNotFoundException("Producto no encontrado con ID: " + productoId);
                });
        
        log.info("✅ Producto encontrado: {}", producto.getTitulo());
        log.info("📁 Ruta del archivo en BD: {}", producto.getRutaArchivo());
        log.info("📂 Directorio base: {}", uploadDir);
        
        // Obtener email del usuario autenticado
        String email = authentication.getName();
        log.info("👤 Usuario autenticado: {}", email);
        
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("❌ Usuario no encontrado con email: {}", email);
                    return new BadRequestException("Usuario no encontrado");
                });
        
        Integer usuarioId = usuario.getUsuarioId();
        log.info("👤 Usuario ID: {}", usuarioId);
        
        // Verificar que el usuario haya comprado el producto o sea el artista
        boolean esArtista = producto.getArtista().getUsuario().getUsuarioId().equals(usuarioId);
        boolean haComprado = productoAdquiridoRepository
                .existsByUsuarioUsuarioIdAndProductoProductoId(usuarioId, productoId);
        
        log.info("🎨 Es artista del producto: {}", esArtista);
        log.info("🛒 Ha comprado el producto: {}", haComprado);
        
        if (!esArtista && !haComprado) {
            log.error("❌ PERMISO DENEGADO: Usuario {} no tiene permiso para descargar producto {}", 
                     usuarioId, productoId);
            throw new BadRequestException("No tienes permiso para descargar este producto. Debes comprarlo primero.");
        }
        
        log.info("✅ Permisos verificados correctamente");
        
        try {
            // Construir ruta completa del archivo
            Path filePath = Paths.get(uploadDir).resolve(producto.getRutaArchivo()).normalize();
            Path absolutePath = filePath.toAbsolutePath();
            
            log.info("📍 Ruta relativa: {}", filePath);
            log.info("📍 Ruta absoluta: {}", absolutePath);
            
            // Verificar si el archivo existe
            boolean exists = Files.exists(filePath);
            log.info("📦 Archivo existe: {}", exists);
            
            if (!exists) {
                log.error("❌ ERROR: El archivo NO existe en la ruta especificada");
                log.error("❌ Ruta buscada: {}", absolutePath);
                
                // Intentar listar el contenido del directorio para debug
                try {
                    Path parentDir = filePath.getParent();
                    if (parentDir != null && Files.exists(parentDir)) {
                        log.info("📁 Contenido del directorio padre:");
                        Files.list(parentDir).forEach(p -> log.info("   - {}", p.getFileName()));
                    } else {
                        log.error("❌ El directorio padre tampoco existe: {}", parentDir);
                    }
                } catch (IOException e) {
                    log.error("❌ Error al listar directorio: {}", e.getMessage());
                }
                
                throw new ResourceNotFoundException(
                    "Archivo no encontrado en el servidor. Ruta: " + producto.getRutaArchivo()
                );
            }
            
            // Verificar si el archivo es legible
            boolean isReadable = Files.isReadable(filePath);
            log.info("📖 Archivo legible: {}", isReadable);
            
            if (!isReadable) {
                log.error("❌ ERROR: El archivo existe pero no es legible");
                throw new BadRequestException("El archivo no tiene permisos de lectura");
            }
            
            // Obtener tamaño del archivo
            long fileSize = Files.size(filePath);
            log.info("📊 Tamaño del archivo: {} bytes ({} KB)", fileSize, fileSize / 1024);
            
            if (fileSize == 0) {
                log.error("❌ ERROR: El archivo está vacío (0 bytes)");
                throw new BadRequestException("El archivo está vacío");
            }
            
            // Crear el resource
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                log.info("✅ Resource creado exitosamente");
                log.info("✅ Nombre del archivo: {}", resource.getFilename());
                log.info("═══════════════════════════════════════");
                log.info("=== DESCARGA EXITOSA ===");
                log.info("═══════════════════════════════════════");
                return resource;
            } else {
                log.error("❌ ERROR: Resource no válido");
                throw new ResourceNotFoundException("No se pudo crear el recurso para la descarga");
            }
            
        } catch (MalformedURLException e) {
            log.error("❌ ERROR DE URL: {}", e.getMessage(), e);
            throw new RuntimeException("Error al construir la URL del archivo: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("❌ ERROR DE IO: {}", e.getMessage(), e);
            throw new RuntimeException("Error al acceder al archivo: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ ERROR INESPERADO: {}", e.getMessage(), e);
            throw new RuntimeException("Error inesperado al procesar la descarga: " + e.getMessage(), e);
        }
    }

    private ProductoAdquiridoDTO convertToDTO(ProductoAdquirido productoAdquirido) {
        ProductoAdquiridoDTO dto = new ProductoAdquiridoDTO();
        
        dto.setLicenciaId(productoAdquirido.getLicenciaId());
        dto.setUsuarioId(productoAdquirido.getUsuario().getUsuarioId());
        dto.setEmailUsuario(productoAdquirido.getUsuario().getEmail());
        
        Producto producto = productoAdquirido.getProducto();
        dto.setProductoId(producto.getProductoId());
        dto.setTituloProducto(producto.getTitulo());
        dto.setDescripcionProducto(producto.getDescripcion());
        dto.setPrecioProducto(producto.getPrecio());
        dto.setUrlMiniatura(producto.getUrlMiniatura());
        dto.setUrlVistaPrevia(producto.getUrlVistaPrevia());
        dto.setRutaArchivo(producto.getRutaArchivo());
        dto.setTipoArchivo(producto.getTipoArchivo());
        dto.setNombreCategoria(producto.getCategoria().getNombre());
        dto.setArtistaId(producto.getArtista().getArtistaId());
        dto.setNombreArtista(producto.getArtista().getNombreCompleto());
        
        dto.setFechaAdquisicion(productoAdquirido.getFechaAdquisicion());
        dto.setVentaId(productoAdquirido.getVenta().getVentaId());
        dto.setPrecioVenta(productoAdquirido.getVenta().getPrecioVenta());
        
        return dto;
    }
}