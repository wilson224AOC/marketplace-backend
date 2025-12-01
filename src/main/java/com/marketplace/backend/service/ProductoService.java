package com.marketplace.backend.service;

import com.marketplace.backend.dto.ProductoDTO;
import com.marketplace.backend.exception.ResourceNotFoundException;
import com.marketplace.backend.model.Artista;
import com.marketplace.backend.model.Categoria;
import com.marketplace.backend.model.Producto;
import com.marketplace.backend.repository.ArtistaRepository;
import com.marketplace.backend.repository.CategoriaRepository;
import com.marketplace.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ArtistaRepository artistaRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProductoDTO> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosPublicados() {
        return productoRepository.findByEstado(Producto.Estado.PUBLICADO).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosByArtista(Integer artistaId) {
        return productoRepository.findByArtistaArtistaId(artistaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosByCategoria(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO getProductoById(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return convertToDTO(producto);
    }

    @Transactional
    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        Artista artista = artistaRepository.findById(productoDTO.getArtistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado"));

        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Producto producto = new Producto();
        producto.setArtista(artista);
        producto.setCategoria(categoria);
        producto.setTitulo(productoDTO.getTitulo());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setRutaArchivo(productoDTO.getRutaArchivo());
        producto.setTipoArchivo(productoDTO.getTipoArchivo());
        producto.setUrlMiniatura(productoDTO.getUrlMiniatura());
        producto.setUrlVistaPrevia(productoDTO.getUrlVistaPrevia());
        producto.setEstado(Producto.Estado.PENDIENTE);

        producto = productoRepository.save(producto);
        return convertToDTO(producto);
    }

    @Transactional
    public ProductoDTO updateProducto(Integer id, ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        if (productoDTO.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        producto.setTitulo(productoDTO.getTitulo());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        
        if (productoDTO.getRutaArchivo() != null) {
            producto.setRutaArchivo(productoDTO.getRutaArchivo());
        }
        if (productoDTO.getTipoArchivo() != null) {
            producto.setTipoArchivo(productoDTO.getTipoArchivo());
        }
        if (productoDTO.getUrlMiniatura() != null) {
            producto.setUrlMiniatura(productoDTO.getUrlMiniatura());
        }
        if (productoDTO.getUrlVistaPrevia() != null) {
            producto.setUrlVistaPrevia(productoDTO.getUrlVistaPrevia());
        }

        producto = productoRepository.save(producto);
        return convertToDTO(producto);
    }

    @Transactional
    public ProductoDTO cambiarEstadoProducto(Integer id, String nuevoEstado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        producto.setEstado(Producto.Estado.valueOf(nuevoEstado.toUpperCase()));
        producto = productoRepository.save(producto);
        return convertToDTO(producto);
    }

    @Transactional
    public void deleteProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setProductoId(producto.getProductoId());
        dto.setArtistaId(producto.getArtista().getArtistaId());
        dto.setNombreArtista(producto.getArtista().getNombreCompleto());
        dto.setCategoriaId(producto.getCategoria().getCategoriaId());
        dto.setNombreCategoria(producto.getCategoria().getNombre());
        dto.setTitulo(producto.getTitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setRutaArchivo(producto.getRutaArchivo());
        dto.setTipoArchivo(producto.getTipoArchivo());
        dto.setUrlMiniatura(producto.getUrlMiniatura());
        dto.setUrlVistaPrevia(producto.getUrlVistaPrevia());
        dto.setEstado(producto.getEstado().name());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setFechaUpdate(producto.getFechaUpdate());
        return dto;
    }
}