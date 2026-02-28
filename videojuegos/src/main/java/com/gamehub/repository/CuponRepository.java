package com.gamehub.repository;

import com.gamehub.entity.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {
    
    Optional<Cupon> findByCodigo(String codigo);
    
    boolean existsByCodigo(String codigo);
}
