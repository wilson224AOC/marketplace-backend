package com.marketplace.backend.repository;

import com.marketplace.backend.model.Billetera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, Integer> {
    Optional<Billetera> findByUsuarioUsuarioId(Integer usuarioId);
}