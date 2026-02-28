package com.gamehub.repository;

import com.gamehub.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    
    List<WishlistItem> findByUsuarioId(Long usuarioId);
    
    Optional<WishlistItem> findByUsuarioIdAndVideojuegoId(Long usuarioId, Long videoJuegoId);
    
    boolean existsByUsuarioIdAndVideojuegoId(Long usuarioId, Long videoJuegoId);
    
    void deleteByUsuarioIdAndVideojuegoId(Long usuarioId, Long videoJuegoId);
    
    long countByUsuarioId(Long usuarioId);
}
