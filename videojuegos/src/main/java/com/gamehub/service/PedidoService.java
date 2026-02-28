package com.gamehub.service;

import com.gamehub.entity.*;
import com.gamehub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final VideojuegoRepository videogameRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Pedido crearPedido(Long usuarioId, String direccionEnvio, String ciudadEnvio, 
                               String codigoPostalEnvio, String paisEnvio, String metodoPago,
                               BigDecimal costeEnvio, BigDecimal descuento, String notas) {
        log.info("Creando pedido para usuario: {}", usuarioId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        List<CarritoItem> items = carritoItemRepository.findByUsuarioId(usuarioId);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CarritoItem item : items) {
            Videojuego videogame = item.getVideoJuego();
            
            if (videogame.getStock() < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + videogame.getTitulo());
            }
            
            BigDecimal precioUnitario = videogame.getPrecioActual();
            subtotal = subtotal.add(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
            
            videogame.setStock(videogame.getStock() - item.getCantidad());
            videogameRepository.save(videogame);
        }
        
        BigDecimal total = subtotal.add(costeEnvio != null ? costeEnvio : BigDecimal.ZERO);
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(descuento);
        }
        
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .subtotal(subtotal)
                .costeEnvio(costeEnvio != null ? costeEnvio : BigDecimal.ZERO)
                .descuento(descuento != null ? descuento : BigDecimal.ZERO)
                .total(total)
                .metodoPago(metodoPago)
                .direccionEnvio(direccionEnvio)
                .ciudadEnvio(ciudadEnvio)
                .codigoPostalEnvio(codigoPostalEnvio)
                .paisEnvio(paisEnvio)
                .notas(notas)
                .build();
        
        for (CarritoItem item : items) {
            Videojuego videogame = item.getVideoJuego();
            BigDecimal precioUnitario = videogame.getPrecioActual();
            BigDecimal subtotalItem = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
            
            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .videoJuego(videogame)
                    .cantidad(item.getCantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotalItem)
                    .build();
            
            pedido.getDetalles().add(detalle);
        }
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        carritoItemRepository.deleteAllByUsuarioId(usuarioId);
        
        log.info("Pedido creado exitosamente con ID: {}", pedidoGuardado.getId());
        return pedidoGuardado;
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosUsuario(Long usuarioId, int pagina, int tamaño) {
        log.debug("Obteniendo pedidos del usuario: {}", usuarioId);
        Pageable pageable = Pageable.ofSize(tamaño).withPage(pagina);
        return pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId, pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosUsuarioTodos(Long usuarioId) {
        log.debug("Obteniendo todos los pedidos del usuario: {}", usuarioId);
        return pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        log.debug("Buscando pedido por ID: {}", id);
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido actualizarEstadoPedido(Long id, Pedido.EstadoPedido nuevoEstado) {
        log.info("Actualizando estado del pedido {} a {}", id, nuevoEstado);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        
        Pedido.EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);
        
        if (nuevoEstado == Pedido.EstadoPedido.ENVIADO) {
            pedido.setFechaEnvio(java.time.LocalDateTime.now());
        } else if (nuevoEstado == Pedido.EstadoPedido.ENTREGADO) {
            pedido.setFechaEntrega(java.time.LocalDateTime.now());
        } else if (nuevoEstado == Pedido.EstadoPedido.CANCELADO) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Videojuego videogame = detalle.getVideoJuego();
                videogame.setStock(videogame.getStock() + detalle.getCantidad());
                videogameRepository.save(videogame);
            }
        }
        
        log.info("Estado del pedido {} actualizado de {} a {}", id, estadoAnterior, nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void asignarNumeroSeguimiento(Long id, String numeroSeguimiento) {
        log.info("Asignando número de seguimiento {} al pedido {}", numeroSeguimiento, id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        
        pedido.setNumeroSeguimiento(numeroSeguimiento);
        pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorStripePaymentIntentId(String paymentIntentId) {
        return pedidoRepository.findByStripePaymentIntentId(paymentIntentId);
    }
}
