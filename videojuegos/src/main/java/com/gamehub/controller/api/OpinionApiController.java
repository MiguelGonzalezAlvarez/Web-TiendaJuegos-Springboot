package com.gamehub.controller.api;

import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.OpinionRequest;
import com.gamehub.entity.Opinion;
import com.gamehub.entity.Usuario;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.service.OpinionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/opiniones")
@RequiredArgsConstructor
public class OpinionApiController {

    private final OpinionService opinionService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> agregarOpinion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OpinionRequest request) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        
        Opinion opinion = opinionService.agregarOpinion(
                usuario.getId(),
                request.getVideojuegosId(),
                request.getCalificacion(),
                request.getTitulo(),
                request.getContenido()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", opinion.getId());
        response.put("calificacion", opinion.getCalificacion());
        response.put("titulo", opinion.getTitulo());
        response.put("contenido", opinion.getContenido());
        response.put("fechaCreacion", opinion.getFechaCreacion());
        
        return ResponseEntity.ok(ApiResponse.success("Opinión agregada", response));
    }

    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> listarOpiniones(
            @PathVariable Long videoJuegoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        
        Page<Opinion> opiniones = opinionService.obtenerOpinionesVideojuego(videoJuegoId, pagina, tamanho);
        
        Page<Map<String, Object>> response = opiniones.map(this::toMap);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private Usuario obtenerUsuario(UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private Map<String, Object> toMap(Opinion opinion) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", opinion.getId());
        map.put("calificacion", opinion.getCalificacion());
        map.put("titulo", opinion.getTitulo());
        map.put("contenido", opinion.getContenido());
        map.put("fechaCreacion", opinion.getFechaCreacion());
        map.put("usuario", opinion.getUsuario().getNombreCompleto());
        return map;
    }
}
