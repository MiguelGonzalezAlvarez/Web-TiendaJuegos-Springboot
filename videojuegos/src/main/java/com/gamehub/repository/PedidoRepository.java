package com.gamehub.repository;

import com.gamehub.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    Page<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId, Pageable pageable);
    
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    
    Optional<Pedido> findByStripePaymentIntentId(String paymentIntentId);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaCreacion BETWEEN :start AND :end")
    List<Pedido> findByFechaCreacionBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    long countByEstado(@Param("estado") Pedido.EstadoPedido estado);
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = 'ENTREGADO' AND p.fechaCreacion BETWEEN :start AND :end")
    java.math.BigDecimal sumTotalEntregado(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    List<Object[]> countByEstadoGroupBy();
}
