package com.gamehub.repository;

import com.gamehub.entity.Videojuego;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    
    Optional<Videojuego> findBySlug(String slug);
    
    Optional<Videojuego> findByRawgId(Long rawgId);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT v FROM Videojuego v WHERE v.activo = true AND v.destacado = true")
    List<Videojuego> findDestacados(Pageable pageable);
    
    @Query("SELECT v FROM Videojuego v WHERE v.activo = true AND v.esOferta = true")
    List<Videojuego> findEnOferta(Pageable pageable);
    
    @Query("SELECT v FROM Videojuego v WHERE v.activo = true ORDER BY v.fechaLanzamiento DESC")
    List<Videojuego> findRecientes(Pageable pageable);
    
    @Query("SELECT v FROM Videojuego v WHERE v.activo = true AND " +
           "(LOWER(v.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.descripcion) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Videojuego> buscar(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT v FROM Videojuego v WHERE v.activo = true AND " +
           "(:genero IS NULL OR v.genero = :genero) AND " +
           "(:plataforma IS NULL OR v.plataforma = :plataforma) AND " +
           "(:precioMin IS NULL OR v.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR v.precio <= :precioMax)")
    Page<Videojuego> findWithFilters(
        @Param("genero") String genero,
        @Param("plataforma") String plataforma,
        @Param("precioMin") java.math.BigDecimal precioMin,
        @Param("precioMax") java.math.BigDecimal precioMax,
        Pageable pageable
    );
    
    @Query("SELECT DISTINCT v.genero FROM Videojuego v WHERE v.genero IS NOT NULL")
    List<String> findDistinctGeneros();
    
    @Query("SELECT DISTINCT v.plataforma FROM Videojuego v WHERE v.plataforma IS NOT NULL")
    List<String> findDistinctPlataformas();
    
    @Query("SELECT COUNT(v) FROM Videojuego v WHERE v.activo = true")
    long countActivos();
    
    @Query("SELECT v FROM Videojuego v WHERE v.activo = true AND v.id <> :id AND " +
           "(v.genero = :genero OR v.plataforma = :plataforma)")
    List<Videojuego> findRelacionados(
        @Param("id") Long id,
        @Param("genero") String genero,
        @Param("plataforma") String plataforma,
        Pageable pageable
    );
}
