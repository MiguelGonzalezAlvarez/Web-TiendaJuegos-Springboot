package com.gamehub.config;

import com.gamehub.entity.Cupon;
import com.gamehub.entity.Usuario;
import com.gamehub.entity.Videojuego;
import com.gamehub.repository.CuponRepository;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.repository.VideojuegoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final VideojuegoRepository videoJuegoRepository;
    private final CuponRepository cuponRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            cargarDatosIniciales();
        }
    }

    private void cargarDatosIniciales() {
        log.info("Cargando datos iniciales...");
        
        // Crear usuario admin
        Usuario admin = Usuario.builder()
                .email("admin@gamehub.com")
                .password(passwordEncoder.encode("admin123"))
                .nombre("Admin")
                .apellido("GameHub")
                .rol(Usuario.Rol.ADMIN)
                .enabled(true)
                .build();
        usuarioRepository.save(admin);
        
        // Crear usuario de prueba
        Usuario usuario = Usuario.builder()
                .email("usuario@gamehub.com")
                .password(passwordEncoder.encode("usuario123"))
                .nombre("Usuario")
                .apellido("Prueba")
                .rol(Usuario.Rol.USUARIO)
                .enabled(true)
                .build();
        usuarioRepository.save(usuario);
        
        // Crear videojuegos de ejemplo
        List<Videojuego> videojuegos = Arrays.asList(
            Videojuego.builder()
                .titulo("The Last of Us Part II")
                .slug("the-last-of-us-part-ii")
                .descripcion("The Last of Us Part II es un juego de acción-aventura desarrollado por Naughty Dog. Ambientado cinco años después de The Last of Us, el jugador controla a Ellie en su búsqueda de venganza.")
                .descripcionCorta("Secuela del aclamado juego de Naughty Dog")
                .precio(new BigDecimal("59.99"))
                .precioOferta(new BigDecimal("44.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.webp")
                .fechaLanzamiento(LocalDate.of(2020, 6, 19))
                .plataforma("PlayStation 4")
                .genero("Acción")
                .desarrollador("Naughty Dog")
                .distribuidor("Sony Interactive Entertainment")
                .stock(50)
                .rating(4.8)
                .valoracionesCount(12500)
                .destacado(true)
                .esOferta(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("God of War Ragnarök")
                .slug("god-of-war-ragnarok")
                .descripcion("God of War Ragnarök es un juego de acción y aventura desarrollado por Santa Monica Studio. Es la secuela de God of War de 2018 y representa el Ragnarök de la mitología nórdica.")
                .descripcionCorta("La saga de Kratos continúa")
                .precio(new BigDecimal("69.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4u4y.webp")
                .fechaLanzamiento(LocalDate.of(2022, 11, 9))
                .plataforma("PlayStation 5")
                .genero("Acción")
                .desarrollador("Santa Monica Studio")
                .distribuidor("Sony Interactive Entertainment")
                .stock(100)
                .rating(4.9)
                .valoracionesCount(8900)
                .destacado(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Elden Ring")
                .slug("elden-ring")
                .descripcion("Elden Ring es un juego de rol de acción desarrollado por FromSoftware. Cuenta con la colaboración del escritor George R.R. Martin y es considerado uno de los mejores juegos de todos los tiempos.")
                .descripcionCorta("El nuevo Souls-like de FromSoftware")
                .precio(new BigDecimal("59.99"))
                .precioOferta(new BigDecimal("39.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4qhe.webp")
                .fechaLanzamiento(LocalDate.of(2022, 2, 25))
                .plataforma("PC")
                .genero("RPG")
                .desarrollador("FromSoftware")
                .distribuidor("Bandai Namco")
                .stock(75)
                .rating(4.9)
                .valoracionesCount(15000)
                .destacado(true)
                .esOferta(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Cyberpunk 2077")
                .slug("cyberpunk-2077")
                .descripcion("Cyberpunk 2077 es un RPG de mundo abierto desarrollado por CD Projekt Red. Ambientado en la megalópolis futurista de Night City, el jugador crea un personaje y explora la ciudad.")
                .descripcionCorta("RPG de mundo abierto en Night City")
                .precio(new BigDecimal("59.99"))
                .precioOferta(new BigDecimal("29.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4r76.webp")
                .fechaLanzamiento(LocalDate.of(2020, 12, 10))
                .plataforma("PC")
                .genero("RPG")
                .desarrollador("CD Projekt Red")
                .distribuidor("CD Projekt")
                .stock(80)
                .rating(4.2)
                .valoracionesCount(20000)
                .destacado(false)
                .esOferta(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Spider-Man 2")
                .slug("spider-man-2")
                .descripcion("Spider-Man 2 es un juego de acción-aventura desarrollado por Insomniac Games. Conoce a Peter Parker y Miles Morales en una nueva historia de Spider-Man.")
                .descripcionCorta("Los dos Spider-Men se unen")
                .precio(new BigDecimal("69.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co5s6c.webp")
                .fechaLanzamiento(LocalDate.of(2023, 10, 20))
                .plataforma("PlayStation 5")
                .genero("Acción")
                .desarrollador("Insomniac Games")
                .distribuidor("Sony Interactive Entertainment")
                .stock(60)
                .rating(4.7)
                .valoracionesCount(5600)
                .destacado(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Baldur's Gate 3")
                .slug("baldurs-gate-3")
                .descripcion("Baldur's Gate 3 es un RPG desarrollado por Larian Studios. Basado en Dungeons & Dragons, ofrece una experiencia de rol profunda con elecciones significativas.")
                .descripcionCorta("El mejor RPG de los últimos años")
                .precio(new BigDecimal("59.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co5q0p.webp")
                .fechaLanzamiento(LocalDate.of(2023, 8, 3))
                .plataforma("PC")
                .genero("RPG")
                .desarrollador("Larian Studios")
                .distribuidor("Larian Studios")
                .stock(90)
                .rating(4.9)
                .valoracionesCount(9800)
                .destacado(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Resident Evil 4 Remake")
                .slug("resident-evil-4-remake")
                .descripcion("Resident Evil 4 Remake es una reimaginación del clásico de Capcom. Leon S. Kennedy se enfrenta a una nueva amenaza en una misión de rescate.")
                .descripcionCorta("El clásico actualizado")
                .precio(new BigDecimal("59.99"))
                .precioOferta(new BigDecimal("44.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4rrm.webp")
                .fechaLanzamiento(LocalDate.of(2023, 3, 24))
                .plataforma("PC")
                .genero("Terror")
                .desarrollador("Capcom")
                .distribuidor("Capcom")
                .stock(70)
                .rating(4.8)
                .valoracionesCount(7500)
                .destacado(false)
                .esOferta(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Zelda: Tears of the Kingdom")
                .slug("zelda-tears-of-the-kingdom")
                .descripcion("Zelda: Tears of the Kingdom es la secuela de Breath of the Wild. Link debe salvar a Zelda y al reino de Hyrule en una aventura épica.")
                .descripcionCorta("La aventura continúa en Hyrule")
                .precio(new BigDecimal("69.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co5y2j.webp")
                .fechaLanzamiento(LocalDate.of(2023, 5, 12))
                .plataforma("Nintendo Switch")
                .genero("Aventura")
                .desarrollador("Nintendo")
                .distribuidor("Nintendo")
                .stock(40)
                .rating(4.9)
                .valoracionesCount(11000)
                .destacado(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Final Fantasy XVI")
                .slug("final-fantasy-xvi")
                .descripcion("Final Fantasy XVI es un RPG de acción desarrollado por Square Enix. Con una historia oscura y adultos, marca un nuevo camino para la saga.")
                .descripcionCorta("Un nuevo Final Fantasy")
                .precio(new BigDecimal("69.99"))
                .precioOferta(new BigDecimal("49.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4zun.webp")
                .fechaLanzamiento(LocalDate.of(2023, 6, 22))
                .plataforma("PlayStation 5")
                .genero("RPG")
                .desarrollador("Square Enix")
                .distribuidor("Square Enix")
                .stock(55)
                .rating(4.5)
                .valoracionesCount(4200)
                .destacado(false)
                .esOferta(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Hogwarts Legacy")
                .slug("hogwarts-legacy")
                .descripcion("Hogwarts Legacy es un RPG de acción desarrollado por Avalanche Software. Explora el mundo mágico de Harry Potter en una historia original.")
                .descripcionCorta("Vive la magia de Harry Potter")
                .precio(new BigDecimal("59.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4ywx.webp")
                .fechaLanzamiento(LocalDate.of(2023, 2, 10))
                .plataforma("PC")
                .genero("RPG")
                .desarrollador("Avalanche Software")
                .distribuidor("Warner Bros")
                .stock(85)
                .rating(4.6)
                .valoracionesCount(13000)
                .destacado(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Starfield")
                .slug("starfield")
                .descripcion("Starfield es un RPG espacial desarrollado por Bethesda. Explora el espacio y descubre nuevos mundos en esta aventura galáctica.")
                .descripcionCorta("Bethesda llega al espacio")
                .precio(new BigDecimal("69.99"))
                .precioOferta(new BigDecimal("54.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4zni.webp")
                .fechaLanzamiento(LocalDate.of(2023, 9, 6))
                .plataforma("PC")
                .genero("RPG")
                .desarrollador("Bethesda Game Studios")
                .distribuidor("Bethesda")
                .stock(65)
                .rating(4.3)
                .valoracionesCount(8500)
                .destacado(false)
                .esOferta(true)
                .activo(true)
                .build(),
                
            Videojuego.builder()
                .titulo("Diablo IV")
                .slug("diablo-iv")
                .descripcion("Diablo IV es un ARPG desarrollado por Blizzard. Regresa a la oscuridad en Sanctuary en esta entrega de la saga clásica.")
                .descripcionCorta("La oscuridad ha regresado")
                .precio(new BigDecimal("69.99"))
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4z0x.webp")
                .fechaLanzamiento(LocalDate.of(2023, 6, 6))
                .plataforma("PC")
                .genero("RPG")
                .desarrollador("Blizzard Entertainment")
                .distribuidor("Blizzard")
                .stock(95)
                .rating(4.4)
                .valoracionesCount(9200)
                .destacado(true)
                .activo(true)
                .build()
        );
        
        videoJuegoRepository.saveAll(videojuegos);
        
        // Crear cupones de ejemplo
        List<Cupon> cupones = Arrays.asList(
            Cupon.builder()
                .codigo("WELCOME10")
                .descripcion("10% de descuento en tu primera compra")
                .tipoDescuento(Cupon.TipoDescuento.PORCENTAJE)
                .porcentajeDescuento(new BigDecimal("10"))
                .descuentoMaximo(new BigDecimal("20"))
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusMonths(3))
                .usosMaximos(100)
                .activo(true)
                .esGlobal(true)
                .build(),
                
            Cupon.builder()
                .codigo("GAME20")
                .descripcion("20% de descuento en videojuegos")
                .tipoDescuento(Cupon.TipoDescuento.PORCENTAJE)
                .porcentajeDescuento(new BigDecimal("20"))
                .descuentoMaximo(new BigDecimal("30"))
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusMonths(1))
                .usosMaximos(50)
                .activo(true)
                .esGlobal(true)
                .build(),
                
            Cupon.builder()
                .codigo("VIP50")
                .descripcion("$50 de descuento en compras mayores a $200")
                .tipoDescuento(Cupon.TipoDescuento.FIJO)
                .valorDescuento(new BigDecimal("50"))
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusWeeks(2))
                .usosMaximos(10)
                .activo(true)
                .esGlobal(true)
                .build()
        );
        
        cuponRepository.saveAll(cupones);
        
        log.info("Datos iniciales cargados correctamente!");
    }
}
