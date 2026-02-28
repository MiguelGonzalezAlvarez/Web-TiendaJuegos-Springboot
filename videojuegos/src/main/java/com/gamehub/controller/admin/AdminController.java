package com.gamehub.controller.admin;

import com.gamehub.entity.Cupon;
import com.gamehub.entity.Pedido;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.repository.PedidoRepository;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.repository.VideojuegoRepository;
import com.gamehub.service.CuponService;
import com.gamehub.service.PedidoService;
import com.gamehub.service.RawgService;
import com.gamehub.service.VideojuegoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final VideojuegoService videogameService;
    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideojuegoRepository videogameRepository;
    private final CuponService cuponService;
    private final RawgService rawgService;

    @GetMapping
    public String dashboard(Model model) {
        log.debug("Cargando dashboard de administración");

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace30Dias = ahora.minusDays(30);

        long pedidosPendentes = pedidoRepository.countByEstado(Pedido.EstadoPedido.PENDIENTE);
        long pedidosProcesando = pedidoRepository.countByEstado(Pedido.EstadoPedido.PROCESANDO);
        long pedidosEnviados = pedidoRepository.countByEstado(Pedido.EstadoPedido.ENVIADO);
        long pedidosEntregados = pedidoRepository.countByEstado(Pedido.EstadoPedido.ENTREGADO);

        BigDecimal ingresosUltimos30Dias = pedidoRepository.sumTotalEntregado(hace30Dias, ahora);
        if (ingresosUltimos30Dias == null) {
            ingresosUltimos30Dias = BigDecimal.ZERO;
        }

        long totalUsuarios = usuarioRepository.count();
        long totalVideojuegos = videogameRepository.count();

        List<Object[]> estadosPedido = pedidoRepository.countByEstadoGroupBy();
        Map<String, Long> pedidosPorEstado = estadosPedido.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> (Long) obj[1]
                ));

        model.addAttribute("pedidosPendentes", pedidosPendentes);
        model.addAttribute("pedidosProcesando", pedidosProcesando);
        model.addAttribute("pedidosEnviados", pedidosEnviados);
        model.addAttribute("pedidosEntregados", pedidosEntregados);
        model.addAttribute("ingresosUltimos30Dias", ingresosUltimos30Dias);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalVideojuegos", totalVideojuegos);
        model.addAttribute("pedidosPorEstado", pedidosPorEstado);

        return "admin/index";
    }

    @GetMapping("/productos")
    public String productos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamaño,
            @RequestParam(required = false) String buscar,
            Model model) {
        log.debug("Mostrando gestión de productos");

        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("id").descending());
        Page<Videojuego> videojuegos;

        if (buscar != null && !buscar.isEmpty()) {
            videojuegos = videogameService.buscar(buscar, pagina, tamaño);
        } else {
            videojuegos = videogameRepository.findAll(pageable);
        }

        model.addAttribute("videojuegos", videojuegos.getContent());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", videojuegos.getTotalPages());
        model.addAttribute("buscar", buscar);

        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {
        log.debug("Mostrando formulario de nuevo producto");
        model.addAttribute("videojuego", new Videojuego());
        model.addAttribute("esEdicion", false);
        return "admin/producto-formulario";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Videojuego videogame) {
        log.info("Guardando producto: {}", videogame.getTitulo());
        videogameService.guardar(videogame);
        return "redirect:/admin/productos?guardado";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        log.debug("Editando producto: {}", id);

        Optional<Videojuego> videogameOpt = videogameService.buscarPorId(id);
        if (videogameOpt.isEmpty()) {
            return "redirect:/admin/productos";
        }

        model.addAttribute("videojuego", videogameOpt.get());
        model.addAttribute("esEdicion", true);
        return "admin/producto-formulario";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        log.info("Eliminando producto: {}", id);
        videogameService.eliminar(id);
        return "redirect:/admin/productos?eliminado";
    }

    @GetMapping("/pedidos")
    public String pedidos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(required = false) String estado,
            Model model) {
        log.debug("Mostrando gestión de pedidos");

        Pageable pageable = PageRequest.of(pagina, 20, Sort.by("fechaCreacion").descending());
        Page<Pedido> pedidos;

        if (estado != null && !estado.isEmpty()) {
            Pedido.EstadoPedido estadoEnum = Pedido.EstadoPedido.valueOf(estado);
            pedidos = pedidoRepository.findAll(pageable);
            List<Pedido> filtrados = pedidos.getContent().stream()
                    .filter(p -> p.getEstado() == estadoEnum)
                    .collect(Collectors.toList());
            model.addAttribute("pedidos", filtrados);
        } else {
            pedidos = pedidoRepository.findAll(pageable);
            model.addAttribute("pedidos", pedidos.getContent());
        }

        model.addAttribute("paginaActual", pagina);
        model.addAttribute("estadoSeleccionado", estado);

        return "admin/pedidos";
    }

    @PostMapping("/pedidos/{id}/estado")
    public String cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("Cambiando estado del pedido {} a {}", id, estado);

        Pedido.EstadoPedido nuevoEstado = Pedido.EstadoPedido.valueOf(estado);
        pedidoService.actualizarEstadoPedido(id, nuevoEstado);

        return "redirect:/admin/pedidos?actualizado";
    }

    @GetMapping("/usuarios")
    public String usuarios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(required = false) String buscar,
            Model model) {
        log.debug("Mostrando gestión de usuarios");

        Pageable pageable = PageRequest.of(pagina, 20);
        Page<Usuario> usuarios;

        if (buscar != null && !buscar.isEmpty()) {
            usuarios = usuarioRepository.findAll(pageable);
            List<Usuario> filtrados = usuarios.getContent().stream()
                    .filter(u -> u.getEmail().contains(buscar) || 
                                u.getNombre().contains(buscar) || 
                                u.getApellido().contains(buscar))
                    .collect(Collectors.toList());
            model.addAttribute("usuarios", filtrados);
        } else {
            usuarios = usuarioRepository.findAll(pageable);
            model.addAttribute("usuarios", usuarios.getContent());
        }

        model.addAttribute("paginaActual", pagina);
        model.addAttribute("buscar", buscar);

        return "admin/usuarios";
    }

    @GetMapping("/cupones")
    public String cupones(Model model) {
        log.debug("Mostrando gestión de cupones");
        return "admin/cupones";
    }

    @GetMapping("/importar")
    public String importar(Model model) {
        log.debug("Mostrando página de importar desde RAWG");
        return "admin/importar";
    }

    @GetMapping("/importar/buscar")
    public String buscarEnRawg(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {
        log.debug("Buscando juegos en RAWG: {}", q);

        try {
            Map<String, Object> resultados = rawgService.obtenerJuegos(q, pagina, 12);
            model.addAttribute("resultados", resultados.get("juegos"));
            model.addAttribute("query", q);
            model.addAttribute("count", resultados.get("count"));
        } catch (Exception e) {
            log.error("Error al buscar en RAWG: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        }

        return "admin/importar";
    }

    @GetMapping("/importar/{rawgId}")
    public String importarJuego(@PathVariable Long rawgId, Model model) {
        log.info("Importando juego de RAWG: {}", rawgId);

        try {
            Videojuego videogame = rawgService.importarJuego(rawgId);
            model.addAttribute("mensaje", "Juego importado: " + videogame.getTitulo());
        } catch (Exception e) {
            log.error("Error al importar: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/productos?importado";
    }
}
