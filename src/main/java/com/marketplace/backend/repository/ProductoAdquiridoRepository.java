package com.marketplace.backend.repository;

import com.marketplace.backend.model.ProductoAdquirido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoAdquiridoRepository extends JpaRepository<ProductoAdquirido, Integer> {
    List<ProductoAdquirido> findByUsuarioUsuarioId(Integer usuarioId);
    boolean existsByUsuarioUsuarioIdAndProductoProductoId(Integer usuarioId, Integer productoId);
}