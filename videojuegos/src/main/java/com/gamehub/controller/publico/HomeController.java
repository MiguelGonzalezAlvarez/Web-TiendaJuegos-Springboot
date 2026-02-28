package com.gamehub.controller.publico;

import com.gamehub.entity.Videojuego;
import com.gamehub.service.VideojuegoService;
import com.gamehub.service.CarritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final VideojuegoService videogameService;
    private final CarritoService carritoService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Cargando página home");

        List<Videojuego> destacados = videogameService.buscarDestacados(6);
        List<Videojuego> ofertas = videogameService.buscarEnOferta(6);
        List<Videojuego> recientes = videogameService.listarTodos(0, 6, "fechaCreacion");

        model.addAttribute("destacados", destacados);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("recientes", recientes);

        if (userDetails != null) {
            model.addAttribute("carritoCount", carritoService.obtenerTotalItems(1L));
        } else {
            model.addAttribute("carritoCount", 0);
        }

        return "index";
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamaño,
            Model model) {
        log.debug("Buscando videojuegos: {}, pagina: {}", q, pagina);

        Page<Videojuego> resultados = videogameService.buscar(q, pagina, tamaño);

        model.addAttribute("resultados", resultados.getContent());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", resultados.getTotalPages());
        model.addAttribute("query", q);

        return "buscar";
    }
}
