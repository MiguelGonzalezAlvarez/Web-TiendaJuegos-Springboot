package com.gamehub.dto;

public class CarritoItemRequest {
    private Long videojuegosId;
    private Integer cantidad;

    public CarritoItemRequest() {}

    public CarritoItemRequest(Long videojuegosId, Integer cantidad) {
        this.videojuegosId = videojuegosId;
        this.cantidad = cantidad;
    }

    public Long getVideojuegosId() { return videojuegosId; }
    public void setVideojuegosId(Long videojuegosId) { this.videojuegosId = videojuegosId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
