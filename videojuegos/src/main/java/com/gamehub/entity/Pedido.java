package com.gamehub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal costeEnvio;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 50)
    private String metodoPago;

    @Column(length = 500)
    private String direccionEnvio;

    private String ciudadEnvio;

    private String codigoPostalEnvio;

    private String paisEnvio;

    @Column(length = 255)
    private String stripePaymentIntentId;

    @Column(length = 255)
    private String stripeCustomerId;

    private String numeroSeguimiento;

    @Column(length = 1000)
    private String notas;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaEntrega;

    public enum EstadoPedido {
        PENDIENTE,
        PAGADO,
        PROCESANDO,
        ENVIADO,
        ENTREGADO,
        CANCELADO,
        REEMBOLSADO
    }

    public BigDecimal getTotalFinal() {
        BigDecimal subtotalTotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        BigDecimal envio = costeEnvio != null ? costeEnvio : BigDecimal.ZERO;
        BigDecimal desc = descuento != null ? descuento : BigDecimal.ZERO;
        return subtotalTotal.add(envio).subtract(desc);
    }
}
