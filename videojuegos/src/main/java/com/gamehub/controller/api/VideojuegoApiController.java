package com.gamehub.controller.api;

import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.VideojuegoResponse;
import com.gamehub.entity.Videojuego;
import com.gamehub.service.VideojuegoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videojuegos")
@RequiredArgsConstructor
public class VideojuegoApiController {

    private final VideojuegoService videogameService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VideojuegoResponse>>>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            @RequestParam(defaultValue = "fechaCreacion") String ordenarPor) {
        
        List<Videojuego> videojuegos = videogameService.listarTodos(pagina, tamanho, ordenarPor);
        Page<VideojuegoResponse> response = PageRequest.of(pagina, tamanho).map(v -> toResponse(v));
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideojuegoResponse>> detalle(@PathVariable Long id) {
        Videojuego videogame = videogameService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Videojuego no encontrado"));
        
        return ResponseEntity.ok(ApiResponse.success(toResponse(videogame)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<Page<VideojuegoResponse>>> buscar(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho) {
        
        Page<Videojuego> videojuegos = videogameService.buscar(q, pagina, tamanho);
        Page<VideojuegoResponse> response = videojuegos.map(this::toResponse);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/destacados")
    public ResponseEntity<ApiResponse<List<VideojuegoResponse>>> destacados(
            @RequestParam(defaultValue = "10") int cantidad) {
        
        List<Videojuego> videojuegos = videogameService.buscarDestacados(cantidad);
        List<VideojuegoResponse> response = videojuegos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/ofertas")
    public ResponseEntity<ApiResponse<List<VideojuegoResponse>>> ofertas(
            @RequestParam(defaultValue = "10") int cantidad) {
        
        List<Videojuego> videojuegos = videogameService.buscarEnOferta(cantidad);
        List<VideojuegoResponse> response = videojuegos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/generos")
    public ResponseEntity<ApiResponse<List<String>>> listarGeneros() {
        List<String> generos = videogameService.obtenerGeneros();
        return ResponseEntity.ok(ApiResponse.success(generos));
    }

    @GetMapping("/plataformas")
    public ResponseEntity<ApiResponse<List<String>>> listarPlataformas() {
        List<String> plataformas = videogameService.obtenerPlataformas();
        return ResponseEntity.ok(ApiResponse.success(plataformas));
    }

    private VideojuegoResponse toResponse(Videojuego videogame) {
        return new VideojuegoResponse(
                videogame.getId(),
                videogame.getTitulo(),
                videogame.getSlug(),
                videogame.getDescripcion(),
                videogame.getPrecio(),
                videogame.getPrecioOferta(),
                videogame.getImagenUrl(),
                videogame.getPlataforma(),
                videogame.getGenero(),
                videogame.getRating(),
                videogame.getStock(),
                videogame.getDestacado(),
                videogame.getEsOferta()
        );
    }
}
