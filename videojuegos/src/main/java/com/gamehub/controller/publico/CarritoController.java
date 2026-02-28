package com.gamehub.controller.publico;

import com.gamehub.entity.CarritoItem;
import com.gamehub.entity.Cupon;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.service.CarritoService;
import com.gamehub.service.CuponService;
import com.gamehub.service.UsuarioService;
import com.gamehub.service.VideojuegoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CarritoController {

    private final CarritoService carritoService;
    private final VideojuegoService videogameService;
    private final CuponService cuponService;
    private final UsuarioService usuarioService;

    @GetMapping("/carrito")
    public String mostrarCarrito(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Mostrando carrito");

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
        BigDecimal total = carritoService.obtenerTotalCarrito(usuarioId);

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("subtotal", total);
        model.addAttribute("descuento", BigDecimal.ZERO);
        model.addAttribute("costeEnvio", BigDecimal.ZERO);

        return "carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(
            @RequestParam Long videogameId,
            @RequestParam(defaultValue = "1") Integer cantidad,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Agregando al carrito - videogameId: {}, cantidad: {}", videogameId, cantidad);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        carritoService.agregarAlCarrito(usuarioId, videogameId, cantidad);

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCarrito(
            @RequestParam Long videogameId,
            @RequestParam Integer cantidad,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Actualizando carrito - videogameId: {}, cantidad: {}", videogameId, cantidad);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        carritoService.actualizarCantidad(usuarioId, videogameId, cantidad);

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar/{id}")
    public String eliminarDelCarrito(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Eliminando del carrito - videogameId: {}", id);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        carritoService.eliminarDelCarrito(usuarioId, id);

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/aplicar-cupon")
    public String aplicarCupon(
            @RequestParam String codigo,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        log.info("Aplicando cupón: {}", codigo);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        
        try {
            BigDecimal totalCarrito = carritoService.obtenerTotalCarrito(usuarioId);
            BigDecimal descuento = cuponService.aplicarCupon(codigo, totalCarrito);
            
            List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
            BigDecimal subtotal = totalCarrito;
            BigDecimal costeEnvio = BigDecimal.valueOf(4.99);
            BigDecimal total = subtotal.add(costeEnvio).subtract(descuento);

            model.addAttribute("items", items);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("descuento", descuento);
            model.addAttribute("costeEnvio", costeEnvio);
            model.addAttribute("total", total);
            model.addAttribute("cuponAplicado", codigo.toUpperCase());
            model.addAttribute("mensaje", "Cupón aplicado correctamente");
        } catch (Exception e) {
            List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
            BigDecimal total = carritoService.obtenerTotalCarrito(usuarioId);
            
            model.addAttribute("items", items);
            model.addAttribute("subtotal", total);
            model.addAttribute("descuento", BigDecimal.ZERO);
            model.addAttribute("costeEnvio", BigDecimal.ZERO);
            model.addAttribute("total", total);
            model.addAttribute("error", e.getMessage());
        }

        return "carrito";
    }
}
