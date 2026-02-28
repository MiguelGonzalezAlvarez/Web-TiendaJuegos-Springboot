package com.gamehub.controller.api;

import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.CarritoResponse;
import com.gamehub.dto.CarritoItemRequest;
import com.gamehub.entity.CarritoItem;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoApiController {

    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<CarritoResponse>> obtenerCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuario(userDetails);
        List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuario.getId());
        BigDecimal total = carritoService.obtenerTotalCarrito(usuario.getId());
        Integer totalItems = carritoService.obtenerTotalItems(usuario.getId());

        List<CarritoResponse.CarritoItemDto> itemDtos = items.stream()
                .map(this::toCarritoItemDto)
                .collect(Collectors.toList());

        CarritoResponse response = new CarritoResponse(itemDtos, total, totalItems);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/agregar")
    public ResponseEntity<ApiResponse<CarritoResponse>> agregarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CarritoItemRequest request) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        Integer cantidad = request.getCantidad() != null ? request.getCantidad() : 1;
        
        carritoService.agregarAlCarrito(usuario.getId(), request.getVideojuegosId(), cantidad);
        
        return obtenerCarrito(userDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarritoResponse>> actualizarCantidad(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        
        Videojuego videogame = usuario.getCarritoItems().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"))
                .getVideoJuego();
        
        carritoService.actualizarCantidad(usuario.getId(), videogame.getId(), cantidad);
        
        return obtenerCarrito(userDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CarritoResponse>> eliminarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        
        Videojuego videogame = usuario.getCarritoItems().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"))
                .getVideoJuego();
        
        carritoService.eliminarDelCarrito(usuario.getId(), videogame.getId());
        
        return obtenerCarrito(userDetails);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> limpiarCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuario(userDetails);
        carritoService.limpiarCarrito(usuario.getId());
        return ResponseEntity.ok(ApiResponse.success("Carrito limpiado", null));
    }

    private Usuario obtenerUsuario(UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private CarritoResponse.CarritoItemDto toCarritoItemDto(CarritoItem item) {
        CarritoResponse.CarritoItemDto dto = new CarritoResponse.CarritoItemDto();
        dto.setId(item.getId());
        dto.setVideojuegosId(item.getVideoJuego().getId());
        dto.setTitulo(item.getVideoJuego().getTitulo());
        dto.setImagenUrl(item.getVideoJuego().getImagenUrl());
        dto.setPrecio(item.getVideoJuego().getPrecioActual());
        dto.setCantidad(item.getCantidad());
        dto.setSubtotal(item.getVideoJuego().getPrecioActual().multiply(BigDecimal.valueOf(item.getCantidad())));
        return dto;
    }
}
