package com.gamehub.controller.publico;

import com.gamehub.entity.Pedido;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.WishlistItem;
import com.gamehub.service.PedidoService;
import com.gamehub.service.UsuarioService;
import com.gamehub.service.WishlistService;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final WishlistService wishlistService;

    @GetMapping("/perfil")
    public String perfil(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Mostrando perfil de usuario");

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        List<Pedido> pedidosRecientes = pedidoService.obtenerPedidosUsuario(usuario.getId(), 0, 5);

        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidosRecientes", pedidosRecientes);

        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @ModelAttribute Usuario usuario,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        log.info("Actualizando perfil de usuario");

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();

        try {
            usuarioService.actualizarUsuario(usuarioId, usuario);
            model.addAttribute("mensaje", "Perfil actualizado correctamente");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/perfil?actualizado";
    }

    @GetMapping("/pedidos")
    public String pedidos(
            @RequestParam(defaultValue = "0") int pagina,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Mostrando historial de pedidos");

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        List<Pedido> pedidos = pedidoService.obtenerPedidosUsuario(usuarioId, pagina, 10);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("paginaActual", pagina);

        return "pedidos";
    }

    @GetMapping("/pedido/{id}")
    public String detallePedido(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Mostrando detalle del pedido: {}", id);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Pedido> pedidoOpt = pedidoService.obtenerPedidoPorId(id);
        if (pedidoOpt.isEmpty()) {
            return "redirect:/pedidos";
        }

        Pedido pedido = pedidoOpt.get();
        if (!pedido.getUsuario().getId().equals(usuarioOpt.get().getId())) {
            return "redirect:/pedidos";
        }

        model.addAttribute("pedido", pedido);

        return "detalle-pedido";
    }

    @GetMapping("/wishlist")
    public String wishlist(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Mostrando wishlist");

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        List<WishlistItem> wishlist = wishlistService.obtenerWishlistUsuario(usuarioId);

        model.addAttribute("wishlist", wishlist);

        return "wishlist";
    }

    @PostMapping("/wishlist/agregar/{id}")
    public String agregarAWishlist(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Agregando a wishlist - videogameId: {}", id);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();

        try {
            wishlistService.agregarAWishlist(usuarioId, id);
        } catch (Exception e) {
            log.warn("Error al agregar a wishlist: {}", e.getMessage());
        }

        return "redirect:/wishlist";
    }

    @PostMapping("/wishlist/eliminar/{id}")
    public String eliminarDeWishlist(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Eliminando de wishlist - videogameId: {}", id);

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Long usuarioId = usuarioOpt.get().getId();
        wishlistService.eliminarDeWishlist(usuarioId, id);

        return "redirect:/wishlist";
    }
}
