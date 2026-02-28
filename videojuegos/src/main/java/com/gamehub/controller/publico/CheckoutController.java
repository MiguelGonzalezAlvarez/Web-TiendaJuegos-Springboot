package com.gamehub.controller.publico;

import com.gamehub.entity.CarritoItem;
import com.gamehub.entity.Pedido;
import com.gamehub.entity.Usuario;
import com.gamehub.service.CarritoService;
import com.gamehub.service.CuponService;
import com.gamehub.service.PedidoService;
import com.gamehub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final CuponService cuponService;

    @GetMapping("/checkout")
    public String mostrarCheckout(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Mostrando checkout");

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        Long usuarioId = usuario.getId();

        List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        BigDecimal subtotal = carritoService.obtenerTotalCarrito(usuarioId);
        BigDecimal costeEnvio = BigDecimal.valueOf(4.99);
        BigDecimal total = subtotal.add(costeEnvio);

        model.addAttribute("usuario", usuario);
        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("costeEnvio", costeEnvio);
        model.addAttribute("descuento", BigDecimal.ZERO);
        model.addAttribute("total", total);

        return "checkout";
    }

    @PostMapping("/checkout/procesar")
    public String procesarCheckout(
            @RequestParam String direccionEnvio,
            @RequestParam String ciudadEnvio,
            @RequestParam String codigoPostalEnvio,
            @RequestParam String paisEnvio,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String notas,
            @RequestParam(required = false) String codigoCupon,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        log.info("Procesando checkout - usuario: {}, metodoPago: {}", userDetails.getUsername(), metodoPago);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        Long usuarioId = usuario.getId();

        List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        BigDecimal subtotal = carritoService.obtenerTotalCarrito(usuarioId);
        BigDecimal costeEnvio = BigDecimal.valueOf(4.99);
        BigDecimal descuento = BigDecimal.ZERO;

        if (codigoCupon != null && !codigoCupon.isEmpty()) {
            try {
                descuento = cuponService.aplicarCupon(codigoCupon, subtotal);
            } catch (Exception e) {
                log.warn("Cupón inválido: {}", e.getMessage());
            }
        }

        try {
            Pedido pedido = pedidoService.crearPedido(
                    usuarioId,
                    direccionEnvio,
                    ciudadEnvio,
                    codigoPostalEnvio,
                    paisEnvio,
                    metodoPago,
                    costeEnvio,
                    descuento,
                    notas);

            log.info("Pedido creado exitosamente con ID: {}", pedido.getId());

            return "redirect:/pedido/" + pedido.getId() + "/confirmacion";

        } catch (Exception e) {
            log.error("Error al procesar pedido: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("items", items);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("costeEnvio", costeEnvio);
            model.addAttribute("descuento", descuento);
            model.addAttribute("total", subtotal.add(costeEnvio).subtract(descuento));

            return "checkout";
        }
    }
}
