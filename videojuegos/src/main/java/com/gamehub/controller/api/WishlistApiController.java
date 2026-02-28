package com.gamehub.controller.api;

import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.VideojuegoResponse;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.entity.WishlistItem;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistApiController {

    private final WishlistService wishlistService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VideojuegoResponse>>> obtenerWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        List<WishlistItem> items = wishlistService.obtenerWishlistUsuario(usuario.getId());
        
        List<VideojuegoResponse> response = items.stream()
                .map(item -> toResponse(item.getVideoJuego()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{videojuegoId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> agregarAWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long videoJuegoId) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        wishlistService.agregarAWishlist(usuario.getId(), videoJuegoId);
        
        Map<String, Object> response = Map.of(
                "videojuegoId", videoJuegoId,
                "mensaje", "Juego agregado a wishlist"
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{videojuegoId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> eliminarDeWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long videoJuegoId) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        wishlistService.eliminarDeWishlist(usuario.getId(), videoJuegoId);
        
        Map<String, Object> response = Map.of(
                "videojuegoId", videoJuegoId,
                "mensaje", "Juego eliminado de wishlist"
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private Usuario obtenerUsuario(UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
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
