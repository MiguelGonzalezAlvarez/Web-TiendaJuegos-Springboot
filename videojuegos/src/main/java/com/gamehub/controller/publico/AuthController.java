package com.gamehub.controller.publico;

import com.gamehub.entity.Usuario;
import com.gamehub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(Model model) {
        log.debug("Mostrando página de login");
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        log.debug("Mostrando página de registro");
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        log.info("Registrando nuevo usuario: {}", usuario.getEmail());

        try {
            if (usuarioService.obtenerUsuarioPorEmail(usuario.getEmail()).isPresent()) {
                model.addAttribute("error", "El email ya está registrado");
                return "registro";
            }

            usuario.setRol(Usuario.Rol.USUARIO);
            usuario.setPassword(usuario.getPassword());

            Usuario usuarioGuardado = usuarioService.registrarUsuario(usuario);
            log.info("Usuario registrado exitosamente: {}", usuarioGuardado.getId());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getPassword()));

            return "redirect:/";

        } catch (Exception e) {
            log.error("Error al registrar usuario: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    @PostMapping("/logout")
    public String logout() {
        log.debug("Cerrando sesión");
        return "redirect:/login?logout";
    }
}
