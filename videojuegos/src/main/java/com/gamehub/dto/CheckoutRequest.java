package com.gamehub.dto;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutRequest {
    private List<CarritoItemRequest> items;
    private String direccionEnvio;
    private String ciudadEnvio;
    private String codigoPostalEnvio;
    private String paisEnvio;
    private String metodoPago;
    private String cuponCodigo;

    public CheckoutRequest() {}

    public List<CarritoItemRequest> getItems() { return items; }
    public void setItems(List<CarritoItemRequest> items) { this.items = items; }
    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }
    public String getCiudadEnvio() { return ciudadEnvio; }
    public void setCiudadEnvio(String ciudadEnvio) { this.ciudadEnvio = ciudadEnvio; }
    public String getCodigoPostalEnvio() { return codigoPostalEnvio; }
    public void setCodigoPostalEnvio(String codigoPostalEnvio) { this.codigoPostalEnvio = codigoPostalEnvio; }
    public String getPaisEnvio() { return paisEnvio; }
    public void setPaisEnvio(String paisEnvio) { this.paisEnvio = paisEnvio; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getCuponCodigo() { return cuponCodigo; }
    public void setCuponCodigo(String cuponCodigo) { this.cuponCodigo = cuponCodigo; }
}
