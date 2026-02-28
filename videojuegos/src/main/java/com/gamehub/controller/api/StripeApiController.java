package com.gamehub.controller.api;

import com.gamehub.config.StripeConfig;
import com.gamehub.dto.ApiResponse;
import com.gamehub.dto.CarritoResponse;
import com.gamehub.entity.Pedido;
import com.gamehub.entity.Usuario;
import com.gamehub.repository.UsuarioRepository;
import com.gamehub.service.CarritoService;
import com.gamehub.service.PedidoService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pago")
@RequiredArgsConstructor
@Slf4j
public class StripeApiController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;
    private final StripeConfig stripeConfig;

    @PostMapping("/create-intent")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPaymentIntent(
            @RequestHeader("Authorization") String authHeader) {
        
        Usuario usuario = obtenerUsuario(authHeader);
        BigDecimal total = carritoService.obtenerTotalCarrito(usuario.getId());
        
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Carrito vacío", "empty_cart"));
        }
        
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(total.multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency(stripeConfig.getCurrency())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            Map<String, String> response = Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "paymentIntentId", paymentIntent.getId()
            );
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (StripeException e) {
            log.error("Error creando PaymentIntent: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al procesar pago", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, stripeConfig.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            log.error("Error verificando firma del webhook: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Firma inválida", "invalid_signature"));
        }
        
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new IllegalArgumentException("PaymentIntent no encontrado"));
                
                String paymentIntentId = paymentIntent.getId();
                pedidoService.obtenerPorStripePaymentIntentId(paymentIntentId)
                        .ifPresent(pedido -> {
                            pedido.setEstado(Pedido.EstadoPedido.PAGADO);
                            pedido.setStripePaymentIntentId(paymentIntentId);
                            pedidoService.actualizarEstadoPedido(pedido.getId(), Pedido.EstadoPedido.PAGADO);
                        });
                break;
                
            case "payment_intent.payment_failed":
                log.error("Pago fallido: ");
                break;
                
            default:
                log.warn("Evento no manejado: {}", event.getType());
        }
        
        return ResponseEntity.ok(ApiResponse.success("Webhook procesado", "success"));
    }

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPublicKey() {
        Map<String, String> config = Map.of(
                "publicKey", stripeConfig.getPublicKey(),
                "currency", stripeConfig.getCurrency()
        );
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    private Usuario obtenerUsuario(String authHeader) {
        String token = authHeader.substring(7);
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getEmail() != null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
