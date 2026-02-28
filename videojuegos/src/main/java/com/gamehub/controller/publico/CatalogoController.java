package com.gamehub.controller.publico;

import com.gamehub.entity.Opinion;
import com.gamehub.entity.Videojuego;
import com.gamehub.service.OpinionService;
import com.gamehub.service.VideojuegoService;
import com.gamehub.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CatalogoController {

    private final VideojuegoService videogameService;
    private final OpinionService opinionService;
    private final WishlistService wishlistService;

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String plataforma,
            @RequestParam(required = false) String precioMin,
            @RequestParam(required = false) String precioMax,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamaño,
            Model model) {
        log.debug("Cargando catálogo con filtros - genero: {}, plataforma: {}, precioMin: {}, precioMax: {}", 
                genero, plataforma, precioMin, precioMax);

        List<String> generos = videogameService.obtenerGeneros();
        List<String> plataformas = videogameService.obtenerPlataformas();

        model.addAttribute("generos", generos);
        model.addAttribute("plataformas", plataformas);
        model.addAttribute("generoSeleccionado", genero);
        model.addAttribute("plataformaSeleccionada", plataforma);

        Page<Videojuego> resultados = videogameService.buscarConFiltros(
                genero, plataforma, 
                precioMin != null ? new java.math.BigDecimal(precioMin) : null,
                precioMax != null ? new java.math.BigDecimal(precioMax) : null,
                pagina, tamaño);

        model.addAttribute("videojuegos", resultados.getContent());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", resultados.getTotalPages());
        model.addAttribute("totalElementos", resultados.getTotalElements());

        return "catalogo";
    }

    @GetMapping("/videojuego/{slug}")
    public String detalleVideojuego(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int paginaOpiniones,
            Model model) {
        log.debug("Cargando detalle de videojuego: {}", slug);

        Optional<Videojuego> videogameOpt = videogameService.buscarPorSlug(slug);
        
        if (videogameOpt.isEmpty()) {
            return "redirect:/catalogo";
        }

        Videojuego videogame = videogameOpt.get();
        
        Page<Opinion> opiniones = opinionService.obtenerOpinionesVideojuego(videogame.getId(), paginaOpiniones, 5);
        List<Videojuego> relacionados = videogameService.buscarRelacionados(videogame.getId(), 4);

        model.addAttribute("videojuego", videogame);
        model.addAttribute("opiniones", opiniones.getContent());
        model.addAttribute("paginaOpiniones", paginaOpiniones);
        model.addAttribute("totalPaginasOpiniones", opiniones.getTotalPages());
        model.addAttribute("relacionados", relacionados);

        return "detalle";
    }
}
