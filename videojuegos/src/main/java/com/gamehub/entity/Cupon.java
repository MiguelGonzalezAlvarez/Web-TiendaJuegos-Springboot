package com.gamehub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDescuento tipoDescuento;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuentoMaximo;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Builder.Default
    private Integer usosMaximos = null;

    @Builder.Default
    private Integer usosActuales = 0;

    @Builder.Default
    private Boolean activo = true;

    @Builder.Default
    private Boolean esGlobal = false;

    public boolean isValido() {
        if (!activo) return false;
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(fechaInicio)) return false;
        if (ahora.isAfter(fechaFin)) return false;
        if (usosMaximos != null && usosActuales >= usosMaximos) return false;
        return true;
    }

    public enum TipoDescuento {
        FIJO,
        PORCENTAJE
    }
}
