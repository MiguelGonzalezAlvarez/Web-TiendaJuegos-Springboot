package com.gamehub.service;

import com.gamehub.entity.Opinion;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.repository.OpinionRepository;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.repository.VideojuegoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideojuegoRepository videogameRepository;

    @Transactional
    public Opinion agregarOpinion(Long usuarioId, Long videogameId, Integer calificacion, 
                                   String titulo, String contenido) {
        log.info("Agregando opinion - usuarioId: {}, videogameId: {}, calificacion: {}", 
                usuarioId, videogameId, calificacion);
        
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        
        if (opinionRepository.existsByUsuarioIdAndVideojuegoId(usuarioId, videogameId)) {
            throw new IllegalArgumentException("Ya has valorado este juego anteriormente");
        }
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Videojuego videogame = videogameRepository.findById(videogameId)
                .orElseThrow(() -> new IllegalArgumentException("Videoguego no encontrado"));
        
        Opinion opinion = Opinion.builder()
                .usuario(usuario)
                .videoJuego(videogame)
                .calificacion(calificacion)
                .titulo(titulo)
                .contenido(contenido)
                .build();
        
        Opinion opinionGuardada = opinionRepository.save(opinion);
        
        actualizarRatingVideojuego(videogameId);
        
        log.info("Opinion guardada con ID: {}", opinionGuardada.getId());
        return opinionGuardada;
    }

    @Transactional(readOnly = true)
    public Page<Opinion> obtenerOpinionesVideojuego(Long videogameId, int pagina, int tamaño) {
        log.debug("Obteniendo opiniones del videogame: {}", videogameId);
        Pageable pageable = Pageable.ofSize(tamaño).withPage(pagina);
        return opinionRepository.findByVideojuegoIdOrderByFechaCreacionDesc(videogameId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Opinion> obtenerOpinionUsuario(Long usuarioId, Long videogameId) {
        log.debug("Obteniendo opinion del usuario {} para el videogame {}", usuarioId, videogameId);
        return opinionRepository.findByUsuarioIdAndVideojuegoId(usuarioId, videogameId);
    }

    @Transactional
    public Opinion actualizarOpinion(Long opinionId, Integer calificacion, String titulo, String contenido) {
        log.info("Actualizando opinion ID: {}", opinionId);
        
        if (calificacion != null && (calificacion < 1 || calificacion > 5)) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        
        Opinion opinion = opinionRepository.findById(opinionId)
                .orElseThrow(() -> new IllegalArgumentException("Opinion no encontrada"));
        
        if (calificacion != null) {
            opinion.setCalificacion(calificacion);
        }
        if (titulo != null) {
            opinion.setTitulo(titulo);
        }
        if (contenido != null) {
            opinion.setContenido(contenido);
        }
        
        Opinion opinionActualizada = opinionRepository.save(opinion);
        
        actualizarRatingVideojuego(opinion.getVideoJuego().getId());
        
        log.info("Opinion actualizada: {}", opinionId);
        return opinionActualizada;
    }

    @Transactional
    public void eliminarOpinion(Long opinionId) {
        log.info("Eliminando opinion ID: {}", opinionId);
        
        Opinion opinion = opinionRepository.findById(opinionId)
                .orElseThrow(() -> new IllegalArgumentException("Opinion no encontrada"));
        
        Long videogameId = opinion.getVideoJuego().getId();
        
        opinionRepository.delete(opinion);
        
        actualizarRatingVideojuego(videogameId);
        
        log.info("Opinion eliminada: {}", opinionId);
    }

    private void actualizarRatingVideojuego(Long videogameId) {
        Double ratingPromedio = opinionRepository.getCalificacionPromedioByVideojuegoId(videogameId);
        long count = opinionRepository.countByVideojuegoId(videogameId);
        
        videogameRepository.findById(videogameId).ifPresent(videogame -> {
            videogame.setRating(ratingPromedio != null ? ratingPromedio : 0.0);
            videogame.setValoracionesCount((int) count);
            videogameRepository.save(videogame);
        });
    }
}
