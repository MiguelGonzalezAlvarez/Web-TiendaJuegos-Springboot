package com.gamehub.repository;

import com.gamehub.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findByUsuarioId(Long usuarioId);
    
    Optional<CarritoItem> findByUsuarioIdAndVideojuegoId(Long usuarioId, Long videoJuegoId);
    
    @Modifying
    @Query("DELETE FROM CarritoItem c WHERE c.usuario.id = :usuarioId")
    void deleteAllByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT SUM(c.cantidad) FROM CarritoItem c WHERE c.usuario.id = :usuarioId")
    Integer getTotalItemsByUsuarioId(@Param("usuarioId") Long usuarioId);
}
