package com.gamehub.service;

import com.gamehub.entity.Videojuego;
import com.gamehub.repository.VideojuegoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideojuegoService {

    private final VideojuegoRepository videojuegoRepository;

    @Transactional
    public Videojuego guardar(Videojuego videojuego) {
        log.info("Guardando videoguego: {}", videogame.getTitulo());
        
        if (videojuego.getSlug() == null || videogame.getSlug().isEmpty()) {
            videogame.setSlug(generarSlug(videojuego.getTitulo()));
        }
        
        Videojuego guardado = videogameRepository.save(videojuego);
        log.info("Videoguego guardado con ID: {}", guardado.getId());
        return guardado;
    }

    @Transactional(readOnly = true)
    public Optional<Videojuego> buscarPorId(Long id) {
        log.debug("Buscando videoguego por ID: {}", id);
        return videogameRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Videojuego> buscarPorSlug(String slug) {
        log.debug("Buscando videoguego por slug: {}", slug);
        return videogameRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public List<Videojuego> listarTodos(int pagina, int tamaño, String ordenarPor) {
        log.debug("Listando todos los videojuegos - pagina: {}, tamaño: {}", pagina, tamaño);
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by(ordenarPor).descending());
        return videogameRepository.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Page<Videojuego> buscarConFiltros(
            String genero,
            String plataforma,
            BigDecimal precioMin,
            BigDecimal precioMax,
            int pagina,
            int tamaño) {
        log.debug("Buscando videojuegos con filtros - genero: {}, plataforma: {}, precioMin: {}, precioMax: {}",
                genero, plataforma, precioMin, precioMax);
        
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("fechaCreacion").descending());
        return videogameRepository.findWithFilters(genero, plataforma, precioMin, precioMax, pageable);
    }

    @Transactional(readOnly = true)
    public List<Videojuego> buscarDestacados(int cantidad) {
        log.debug("Buscando videojuegos destacados - cantidad: {}", cantidad);
        Pageable pageable = PageRequest.of(0, cantidad);
        return videogameRepository.findDestacados(pageable);
    }

    @Transactional(readOnly = true)
    public List<Videojuego> buscarEnOferta(int cantidad) {
        log.debug("Buscando videojuegos en oferta - cantidad: {}", cantidad);
        Pageable pageable = PageRequest.of(0, cantidad);
        return videogameRepository.findEnOferta(pageable);
    }

    @Transactional(readOnly = true)
    public List<Videojuego> buscarRelacionados(Long id, int cantidad) {
        log.debug("Buscando videojuegos relacionados - ID: {}, cantidad: {}", id, cantidad);
        
        Optional<Videojuego> videogameOpt = videogameRepository.findById(id);
        if (videogameOpt.isEmpty()) {
            return List.of();
        }
        
        Videojuego videogame = videogameOpt.get();
        Pageable pageable = PageRequest.of(0, cantidad);
        return videogameRepository.findRelacionados(id, videogame.getGenero(), videogame.getPlataforma(), pageable);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando videoguego con ID: {}", id);
        
        if (!videogameRepository.existsById(id)) {
            throw new IllegalArgumentException("Videoguego no encontrado con ID: " + id);
        }
        
        videogameRepository.deleteById(id);
        log.info("Videoguego eliminado con ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<String> obtenerGeneros() {
        return videogameRepository.findDistinctGeneros();
    }

    @Transactional(readOnly = true)
    public List<String> obtenerPlataformas() {
        return videogameRepository.findDistinctPlataformas();
    }

    @Transactional(readOnly = true)
    public Page<Videojuego> buscar(String query, int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño);
        return videogameRepository.buscar(query, pageable);
    }

    private String generarSlug(String titulo) {
        if (titulo == null) {
            return "";
        }
        return titulo.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
