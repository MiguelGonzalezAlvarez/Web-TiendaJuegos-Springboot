package com.gamehub.repository;

import com.gamehub.entity.Opinion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    
    Page<Opinion> findByVideojuegoIdOrderByFechaCreacionDesc(Long videoJuegoId, Pageable pageable);
    
    Optional<Opinion> findByUsuarioIdAndVideojuegoId(Long usuarioId, Long videoJuegoId);
    
    boolean existsByUsuarioIdAndVideojuegoId(Long usuarioId, Long videoJuegoId);
    
    @Query("SELECT AVG(o.calificacion) FROM Opinion o WHERE o.videoJuego.id = :videoJuegoId")
    Double getCalificacionPromedioByVideojuegoId(@Param("videoJuegoId") Long videoJuegoId);
    
    @Query("SELECT COUNT(o) FROM Opinion o WHERE o.videoJuego.id = :videoJuegoId")
    long countByVideojuegoId(@Param("videoJuegoId") Long videoJuegoId);
}
