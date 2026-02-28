package com.gamehub.service;

import com.gamehub.entity.Cupon;
import com.gamehub.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuponService {

    private final CuponRepository cuponRepository;

    @Transactional(readOnly = true)
    public Optional<Cupon> validarCupon(String codigo) {
        log.debug("Validando cupon: {}", codigo);
        
        Optional<Cupon> cuponOpt = cuponRepository.findByCodigo(codigo.toUpperCase());
        
        if (cuponOpt.isEmpty()) {
            log.debug("Cupon no encontrado: {}", codigo);
            return Optional.empty();
        }
        
        Cupon cupon = cuponOpt.get();
        
        if (!cupon.isValido()) {
            log.debug("Cupon no valido: {}", codigo);
            return Optional.empty();
        }
        
        log.debug("Cupon valido: {}", codigo);
        return cuponOpt;
    }

    @Transactional
    public BigDecimal aplicarCupon(String codigo, BigDecimal totalCarrito) {
        log.info("Aplicando cupon: {} al carrito con total: {}", codigo, totalCarrito);
        
        Cupon cupon = validarCupon(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupon inválido o expirado"));
        
        BigDecimal descuento = BigDecimal.ZERO;
        
        if (cupon.getTipoDescuento() == Cupon.TipoDescuento.FIJO) {
            descuento = cupon.getValorDescuento();
        } else if (cupon.getTipoDescuento() == Cupon.TipoDescuento.PORCENTAJE) {
            BigDecimal porcentaje = cupon.getPorcentajeDescuento()
                    .divide(BigDecimal.valueOf(100));
            descuento = totalCarrito.multiply(porcentaje);
            
            if (cupon.getDescuentoMaximo() != null && 
                descuento.compareTo(cupon.getDescuentoMaximo()) > 0) {
                descuento = cupon.getDescuentoMaximo();
            }
        }
        
        if (descuento.compareTo(totalCarrito) > 0) {
            descuento = totalCarrito;
        }
        
        cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        cuponRepository.save(cupon);
        
        log.info("Descuento aplicado: {}", descuento);
        return descuento;
    }

    @Transactional
    public Cupon crearCupon(String codigo, String descripcion, Cupon.TipoDescuento tipoDescuento,
                             BigDecimal valorDescuento, BigDecimal porcentajeDescuento,
                             BigDecimal descuentoMaximo, LocalDateTime fechaInicio,
                             LocalDateTime fechaFin, Integer usosMaximos, Boolean esGlobal) {
        log.info("Creando cupon: {}", codigo);
        
        if (cuponRepository.existsByCodigo(codigo.toUpperCase())) {
            throw new IllegalArgumentException("Ya existe un cupon con el código: " + codigo);
        }
        
        if (tipoDescuento == Cupon.TipoDescuento.FIJO && valorDescuento == null) {
            throw new IllegalArgumentException("El valor de descuento es requerido para cupones fijos");
        }
        
        if (tipoDescuento == Cupon.TipoDescuento.PORCENTAJE && porcentajeDescuento == null) {
            throw new IllegalArgumentException("El porcentaje de descuento es requerido para cupones de porcentaje");
        }
        
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        Cupon cupon = Cupon.builder()
                .codigo(codigo.toUpperCase())
                .descripcion(descripcion)
                .tipoDescuento(tipoDescuento)
                .valorDescuento(valorDescuento)
                .porcentajeDescuento(porcentajeDescuento)
                .descuentoMaximo(descuentoMaximo)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .usosMaximos(usosMaximos)
                .usosActuales(0)
                .activo(true)
                .esGlobal(esGlobal != null ? esGlobal : false)
                .build();
        
        Cupon guardado = cuponRepository.save(cupon);
        log.info("Cupon creado con ID: {}", guardado.getId());
        return guardado;
    }

    @Transactional
    public Cupon actualizarCupon(Long id, String descripcion, BigDecimal valorDescuento,
                                  BigDecimal porcentajeDescuento, BigDecimal descuentoMaximo,
                                  LocalDateTime fechaFin, Integer usosMaximos, Boolean activo) {
        log.info("Actualizando cupon ID: {}", id);
        
        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupon no encontrado"));
        
        if (descripcion != null) {
            cupon.setDescripcion(descripcion);
        }
        if (valorDescuento != null) {
            cupon.setValorDescuento(valorDescuento);
        }
        if (porcentajeDescuento != null) {
            cupon.setPorcentajeDescuento(porcentajeDescuento);
        }
        if (descuentoMaximo != null) {
            cupon.setDescuentoMaximo(descuentoMaximo);
        }
        if (fechaFin != null) {
            cupon.setFechaFin(fechaFin);
        }
        if (usosMaximos != null) {
            cupon.setUsosMaximos(usosMaximos);
        }
        if (activo != null) {
            cupon.setActivo(activo);
        }
        
        return cuponRepository.save(cupon);
    }

    @Transactional
    public void eliminarCupon(Long id) {
        log.info("Eliminando cupon ID: {}", id);
        
        if (!cuponRepository.existsById(id)) {
            throw new IllegalArgumentException("Cupon no encontrado");
        }
        
        cuponRepository.deleteById(id);
        log.info("Cupon eliminado");
    }
}
