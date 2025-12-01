package com.marketplace.backend.repository;

import com.marketplace.backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByCompradorUsuarioId(Integer compradorId);
    List<Venta> findByVendedorArtistaId(Integer vendedorId);
}