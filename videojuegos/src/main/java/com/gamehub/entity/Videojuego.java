package com.gamehub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videojuegos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 500)
    private String descripcionCorta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Column(length = 500)
    private String imagenUrl;

    @ElementCollection
    @CollectionTable(name = "videojuego_imagenes", joinColumns = @JoinColumn(name = "videojuego_id"))
    @Column(name = "imagen_url")
    @Builder.Default
    private List<String> imagenesAdicionales = new ArrayList<>();

    private LocalDate fechaLanzamiento;

    @Column(length = 100)
    private String plataforma;

    @Column(length = 100)
    private String genero;

    @Column(length = 200)
    private String desarrollador;

    @Column(length = 200)
    private String distribuidor;

    @Column(columnDefinition = "TEXT")
    private String requisitosMinimos;

    @Column(columnDefinition = "TEXT")
    private String requisitosRecomendados;

    @Builder.Default
    private Integer stock = 0;

    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer valoracionesCount = 0;

    @Builder.Default
    private Boolean destacado = false;

    @Builder.Default
    private Boolean activo = true;

    @Builder.Default
    private Boolean esOferta = false;

    @Column(unique = true)
    private Long rawgId;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "videojuego", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Opinion> opiniones = new ArrayList<>();

    @OneToMany(mappedBy = "videojuego", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WishlistItem> wishlistItems = new ArrayList<>();

    public BigDecimal getPrecioActual() {
        if (precioOferta != null && precioOferta.compareTo(BigDecimal.ZERO) > 0) {
            return precioOferta;
        }
        return precio;
    }

    public boolean hasDescuento() {
        return precioOferta != null && precioOferta.compareTo(BigDecimal.ZERO) > 0 
            && precioOferta.compareTo(precio) < 0;
    }

    public Integer getPorcentajeDescuento() {
        if (!hasDescuento()) return 0;
        return (int) Math.round((1 - precioOferta.doubleValue() / precio.doubleValue()) * 100);
    }
}
