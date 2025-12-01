package com.marketplace.backend.repository;

import com.marketplace.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Buscar por ID del artista
    List<Producto> findByArtistaArtistaId(Integer artistaId);
    
    // Buscar por categoría - CORREGIDO con @Query
    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId")
    List<Producto> findByCategoriaId(@Param("categoriaId") Integer categoriaId);
    
    // Buscar por estado
    List<Producto> findByEstado(Producto.Estado estado);
}