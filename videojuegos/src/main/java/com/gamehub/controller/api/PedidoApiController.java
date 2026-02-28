package com.gamehub.controller.api;

import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.CheckoutRequest;
import com.gamehub.dto.PedidoResponse;
import com.gamehub.entity.DetallePedido;
import com.gamehub.entity.Pedido;
import com.gamehub.entity.Usuario;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoApiController {

    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponse>> crearPedido(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckoutRequest request) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        
        Pedido pedido = pedidoService.crearPedido(
                usuario.getId(),
                request.getDireccionEnvio(),
                request.getCiudadEnvio(),
                request.getCodigoPostalEnvio(),
                request.getPaisEnvio(),
                request.getMetodoPago(),
                request.getCosteEnvio(),
                request.getDescuento(),
                request.getNotas()
        );
        
        return ResponseEntity.ok(ApiResponse.success("Pedido creado exitosamente", toResponse(pedido)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPedidos(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        List<Pedido> pedidos = pedidoService.obtenerPedidosUsuario(usuario.getId(), pagina, tamanho);
        
        List<PedidoResponse> response = pedidos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoResponse>> detallePedido(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        Usuario usuario = obtenerUsuario(userDetails);
        Pedido pedido = pedidoService.obtenerPedidoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("No autorizado", "forbidden"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(toResponse(pedido)));
    }

    private Usuario obtenerUsuario(UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<PedidoResponse.DetallePedidoDto> detalles = pedido.getDetalles().stream()
                .map(this::toDetalleDto)
                .collect(Collectors.toList());
        
        return new PedidoResponse(
                pedido.getId(),
                pedido.getEstado().name(),
                pedido.getTotal(),
                detalles,
                pedido.getFechaCreacion()
        );
    }

    private PedidoResponse.DetallePedidoDto toDetalleDto(DetallePedido detalle) {
        PedidoResponse.DetallePedidoDto dto = new PedidoResponse.DetallePedidoDto();
        dto.setId(detalle.getId());
        dto.setVideojuegosId(detalle.getVideoJuego().getId());
        dto.setTitulo(detalle.getVideoJuego().getTitulo());
        dto.setImagenUrl(detalle.getVideoJuego().getImagenUrl());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }
}
