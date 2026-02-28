package com.gamehub.service;

import com.gamehub.entity.Videojuego;
import com.gamehub.repository.VideojuegoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawgService {

    private final VideojuegoRepository videogameRepository;
    private final RestTemplate restTemplate;

    @Value("${rawg.api.key:}")
    private String rawgApiKey;

    @Value("${rawg.api.url:https://api.rawg.io/api}")
    private String rawgApiUrl;

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerJuegos(String busqueda, int pagina, int tamaño) {
        log.info("Obteniendo juegos de RAWG API - busqueda: {}, pagina: {}", busqueda, pagina);
        
        if (rawgApiKey == null || rawgApiKey.isEmpty()) {
            throw new IllegalStateException("RAWG API key no configurada");
        }
        
        String url = rawgApiUrl + "/games?key=" + rawgApiKey + 
                     "&page=" + pagina + 
                     "&page_size=" + tamaño;
        
        if (busqueda != null && !busqueda.isEmpty()) {
            url += "&search=" + busqueda;
        }
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> respuesta = restTemplate.getForObject(url, Map.class);
            
            if (respuesta != null && respuesta.containsKey("results")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> juegos = (List<Map<String, Object>>) respuesta.get("results");
                
                List<Map<String, Object>> juegosFormateados = new ArrayList<>();
                for (Map<String, Object> juego : juegos) {
                    Map<String, Object> juegoFormateado = formatearJuego(juego);
                    juegosFormateados.add(juegoFormateado);
                }
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("juegos", juegosFormateados);
                resultado.put("count", respuesta.get("count"));
                resultado.put("next", respuesta.get("next"));
                resultado.put("previous", respuesta.get("previous"));
                
                log.info("Se obtuvieron {} juegos de RAWG", juegosFormateados.size());
                return resultado;
            }
            
            return Collections.emptyMap();
            
        } catch (Exception e) {
            log.error("Error al obtener juegos de RAWG API: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con RAWG API: " + e.getMessage());
        }
    }

    @Transactional
    public Videojuego importarJuego(Long rawgId) {
        log.info("Importando juego de RAWG API - rawgId: {}", rawgId);
        
        if (rawgApiKey == null || rawgApiKey.isEmpty()) {
            throw new IllegalStateException("RAWG API key no configurada");
        }
        
        Optional<Videojuego> existente = videogameRepository.findByRawgId(rawgId);
        if (existente.isPresent()) {
            log.info("El juego ya existe en la base de datos con ID: {}", existente.get().getId());
            return existente.get();
        }
        
        String url = rawgApiUrl + "/games/" + rawgId + "?key=" + rawgApiKey;
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> respuesta = restTemplate.getForObject(url, Map.class);
            
            if (respuesta == null) {
                throw new RuntimeException("No se pudo obtener el juego de RAWG API");
            }
            
            Videojuego videogame = convertirARawg(respuesta);
            Videojuego guardado = videogameRepository.save(videogame);
            
            log.info("Juego importado exitosamente con ID: {}", guardado.getId());
            return guardado;
            
        } catch (Exception e) {
            log.error("Error al importar juego de RAWG API: {}", e.getMessage());
            throw new RuntimeException("Error al importar juego: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> formatearJuego(Map<String, Object> juego) {
        Map<String, Object> formateado = new HashMap<>();
        
        formateado.put("id", juego.get("id"));
        formateado.put("nombre", juego.get("name"));
        formateado.put("lanzamiento", juego.get("released"));
        formateado.put("rating", juego.get("rating"));
        
        if (juego.get("background_image") != null) {
            formateado.put("imagen", juego.get("background_image"));
        }
        
        if (juego.get("platforms") != null) {
            List<Map<String, Object>> plataformas = (List<Map<String, Object>>) juego.get("platforms");
            List<String> nombresPlataformas = new ArrayList<>();
            for (Map<String, Object> p : plataformas) {
                if (p.get("platform") != null) {
                    Map<String, Object> platform = (Map<String, Object>) p.get("platform");
                    nombresPlataformas.add((String) platform.get("name"));
                }
            }
            formateado.put("plataformas", String.join(", ", nombresPlataformas));
        }
        
        if (juego.get("genres") != null) {
            List<Map<String, Object>> generos = (List<Map<String, Object>>) juego.get("genres");
            List<String> nombresGeneros = new ArrayList<>();
            for (Map<String, Object> g : generos) {
                nombresGeneros.add((String) g.get("name"));
            }
            formateado.put("generos", String.join(", ", nombresGeneros));
        }
        
        return formateado;
    }

    @SuppressWarnings("unchecked")
    private Videojuego convertirARawg(Map<String, Object> datos) {
        Videojuego videogame = new Videojuego();
        
        videogame.setRawgId(((Number) datos.get("id")).longValue());
        videogame.setTitulo((String) datos.get("name"));
        videogame.setSlug(generarSlug((String) datos.get("name")));
        
        if (datos.get("description_raw") != null) {
            videogame.setDescripcion((String) datos.get("description_raw"));
        } else if (datos.get("description") != null) {
            videogame.setDescripcion((String) datos.get("description"));
        }
        
        if (datos.get("background_image") != null) {
            videogame.setImagenUrl((String) datos.get("background_image"));
        }
        
        if (datos.get("released") != null) {
            try {
                String fechaStr = (String) datos.get("released");
                videogame.setFechaLanzamiento(LocalDate.parse(fechaStr));
            } catch (Exception e) {
                log.warn("Error al parsear fecha de lanzamiento: {}", e.getMessage());
            }
        }
        
        if (datos.get("rating") != null) {
            videogame.setRating(((Number) datos.get("rating")).doubleValue());
        }
        
        if (datos.get("metacritic") != null) {
            videogame.setRating(((Number) datos.get("metacritic")).doubleValue());
        }
        
        videogame.setPrecio(BigDecimal.valueOf(49.99));
        videogame.setStock(0);
        videogame.setActivo(true);
        videogame.setDestacado(false);
        videogame.setEsOferta(false);
        
        if (datos.get("platforms") != null) {
            List<Map<String, Object>> plataformas = (List<Map<String, Object>>) datos.get("platforms");
            List<String> nombresPlataformas = new ArrayList<>();
            for (Map<String, Object> p : plataformas) {
                if (p.get("platform") != null) {
                    Map<String, Object> platform = (Map<String, Object>) p.get("platform");
                    nombresPlataformas.add((String) platform.get("name"));
                }
            }
            videogame.setPlataforma(String.join(", ", nombresPlataformas));
        }
        
        if (datos.get("genres") != null) {
            List<Map<String, Object>> generos = (List<Map<String, Object>>) datos.get("genres");
            List<String> nombresGeneros = new ArrayList<>();
            for (Map<String, Object> g : generos) {
                nombresGeneros.add((String) g.get("name"));
            }
            videogame.setGenero(String.join(", ", nombresGeneros));
        }
        
        if (datos.get("developers") != null) {
            List<Map<String, Object>> desarrolladores = (List<Map<String, Object>>) datos.get("developers");
            if (!desarrolladores.isEmpty()) {
                videogame.setDesarrollador((String) desarrolladores.get(0).get("name"));
            }
        }
        
        if (datos.get("publishers") != null) {
            List<Map<String, Object>> distribuidores = (List<Map<String, Object>>) datos.get("publishers");
            if (!distribuidores.isEmpty()) {
                videogame.setDistribuidor((String) distribuidores.get(0).get("name"));
            }
        }
        
        return videogame;
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
