package com.gamehub.service;

import com.gamehub.entity.CarritoItem;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.repository.CarritoItemRepository;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.repository.VideojuegoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideojuegoRepository videogameRepository;

    @Transactional
    public CarritoItem agregarAlCarrito(Long usuarioId, Long videogameId, Integer cantidad) {
        log.info("Agregando al carrito - usuarioId: {}, videogameId: {}, cantidad: {}", 
                usuarioId, videogameId, cantidad);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Videojuego videogame = videogameRepository.findById(videogameId)
                .orElseThrow(() -> new IllegalArgumentException("Videoguego no encontrado"));
        
        if (videogame.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        
        CarritoItem itemExistente = carritoItemRepository
                .findByUsuarioIdAndVideojuegoId(usuarioId, videogameId)
                .orElse(null);
        
        if (itemExistente != null) {
            int nuevaCantidad = itemExistente.getCantidad() + cantidad;
            if (videogame.getStock() < nuevaCantidad) {
                throw new IllegalArgumentException("Stock insuficiente para la cantidad total");
            }
            itemExistente.setCantidad(nuevaCantidad);
            log.info("Cantidad actualizada en el carrito: {}", nuevaCantidad);
            return carritoItemRepository.save(itemExistente);
        }
        
        CarritoItem nuevoItem = CarritoItem.builder()
                .usuario(usuario)
                .videoJuego(videogame)
                .cantidad(cantidad)
                .build();
        
        log.info("Nuevo item agregado al carrito");
        return carritoItemRepository.save(nuevoItem);
    }

    @Transactional
    public CarritoItem actualizarCantidad(Long usuarioId, Long videogameId, Integer cantidad) {
        log.info("Actualizando cantidad - usuarioId: {}, videogameId: {}, cantidad: {}", 
                usuarioId, videogameId, cantidad);
        
        if (cantidad <= 0) {
            return eliminarDelCarrito(usuarioId, videogameId);
        }
        
        CarritoItem item = carritoItemRepository
                .findByUsuarioIdAndVideojuegoId(usuarioId, videogameId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado en el carrito"));
        
        Videojuego videogame = item.getVideoJuego();
        if (videogame.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        
        item.setCantidad(cantidad);
        return carritoItemRepository.save(item);
    }

    @Transactional
    public CarritoItem eliminarDelCarrito(Long usuarioId, Long videogameId) {
        log.info("Eliminando del carrito - usuarioId: {}, videogameId: {}", usuarioId, videogameId);
        
        CarritoItem item = carritoItemRepository
                .findByUsuarioIdAndVideojuegoId(usuarioId, videogameId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado en el carrito"));
        
        carritoItemRepository.delete(item);
        log.info("Item eliminado del carrito");
        return item;
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> obtenerCarritoUsuario(Long usuarioId) {
        log.debug("Obteniendo carrito del usuario: {}", usuarioId);
        return carritoItemRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void limpiarCarrito(Long usuarioId) {
        log.info("Limpiando carrito del usuario: {}", usuarioId);
        carritoItemRepository.deleteAllByUsuarioId(usuarioId);
        log.info("Carrito limpiado");
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalCarrito(Long usuarioId) {
        log.debug("Calculando total del carrito - usuarioId: {}", usuarioId);
        
        List<CarritoItem> items = carritoItemRepository.findByUsuarioId(usuarioId);
        BigDecimal total = BigDecimal.ZERO;
        
        for (CarritoItem item : items) {
            BigDecimal precioUnitario = item.getVideoJuego().getPrecioActual();
            BigDecimal subtotalItem = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotalItem);
        }
        
        log.debug("Total del carrito: {}", total);
        return total;
    }

    @Transactional(readOnly = true)
    public Integer obtenerTotalItems(Long usuarioId) {
        Integer total = carritoItemRepository.getTotalItemsByUsuarioId(usuarioId);
        return total != null ? total : 0;
    }
}
