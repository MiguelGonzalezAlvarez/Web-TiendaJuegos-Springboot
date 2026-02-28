package com.gamehub.service;

import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.entity.WishlistItem;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.repository.VideojuegoRepository;
import com.gamehub.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideojuegoRepository videogameRepository;

    @Transactional
    public WishlistItem agregarAWishlist(Long usuarioId, Long videogameId) {
        log.info("Agregando a wishlist - usuarioId: {}, videogameId: {}", usuarioId, videogameId);
        
        if (wishlistItemRepository.existsByUsuarioIdAndVideojuegoId(usuarioId, videogameId)) {
            throw new IllegalArgumentException("El juego ya está en la wishlist");
        }
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Videojuego videogame = videogameRepository.findById(videogameId)
                .orElseThrow(() -> new IllegalArgumentException("Videoguego no encontrado"));
        
        WishlistItem item = WishlistItem.builder()
                .usuario(usuario)
                .videoJuego(videogame)
                .build();
        
        log.info("Juego agregado a wishlist");
        return wishlistItemRepository.save(item);
    }

    @Transactional
    public void eliminarDeWishlist(Long usuarioId, Long videogameId) {
        log.info("Eliminando de wishlist - usuarioId: {}, videogameId: {}", usuarioId, videogameId);
        
        wishlistItemRepository.deleteByUsuarioIdAndVideojuegoId(usuarioId, videogameId);
        log.info("Juego eliminado de wishlist");
    }

    @Transactional(readOnly = true)
    public List<WishlistItem> obtenerWishlistUsuario(Long usuarioId) {
        log.debug("Obteniendo wishlist del usuario: {}", usuarioId);
        return wishlistItemRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public boolean estaEnWishlist(Long usuarioId, Long videogameId) {
        log.debug("Verificando si está en wishlist - usuarioId: {}, videogameId: {}", usuarioId, videogameId);
        return wishlistItemRepository.existsByUsuarioIdAndVideojuegoId(usuarioId, videogameId);
    }

    @Transactional(readOnly = true)
    public long obtenerTotalWishlist(Long usuarioId) {
        return wishlistItemRepository.countByUsuarioId(usuarioId);
    }
}
