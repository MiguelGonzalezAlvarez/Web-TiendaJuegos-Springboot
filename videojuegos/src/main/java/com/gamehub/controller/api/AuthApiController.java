package com.gamehub.controller.api;

import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.AuthResponse;
import com.gamehub.dto.LoginRequest;
import com.gamehub.dto.RegistroRequest;
import com.gamehub.entity.Usuario;
import com.gamehub.security.JwtService;
import com.gamehub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegistroRequest request) {
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .rol(Usuario.Rol.USUARIO)
                .build();

        usuario = usuarioService.registrarUsuario(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails);

        AuthResponse response = new AuthResponse(token, "Bearer", usuario.getEmail(), usuario.getRol().name());
        return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String token = jwtService.generateToken(userDetails);

        AuthResponse response = new AuthResponse(token, "Bearer", usuario.getEmail(), usuario.getRol().name());
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token requerido", "missing_token"));
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (jwtService.isTokenValid(token, userDetails)) {
            String newToken = jwtService.generateToken(userDetails);
            AuthResponse response = new AuthResponse(newToken, "Bearer", usuario.getEmail(), usuario.getRol().name());
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Token inválido", "invalid_token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logout exitoso", null));
    }
}
