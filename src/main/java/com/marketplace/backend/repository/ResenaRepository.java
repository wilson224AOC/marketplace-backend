package com.marketplace.backend.repository;

import com.marketplace.backend.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer> {
    List<Resena> findByProductoProductoId(Integer productoId);
    List<Resena> findByUsuarioUsuarioId(Integer usuarioId);
}