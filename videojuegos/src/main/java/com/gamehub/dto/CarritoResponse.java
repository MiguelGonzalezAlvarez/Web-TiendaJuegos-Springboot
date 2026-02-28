package com.gamehub.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoResponse {
    private List<CarritoItemDto> items;
    private BigDecimal total;
    private Integer totalItems;

    public CarritoResponse() {}

    public CarritoResponse(List<CarritoItemDto> items, BigDecimal total, Integer totalItems) {
        this.items = items;
        this.total = total;
        this.totalItems = totalItems;
    }

    public List<CarritoItemDto> getItems() { return items; }
    public void setItems(List<CarritoItemDto> items) { this.items = items; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public static class CarritoItemDto {
        private Long id;
        private Long videojuegosId;
        private String titulo;
        private String imagenUrl;
        private BigDecimal precio;
        private Integer cantidad;
        private BigDecimal subtotal;

        public CarritoItemDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getVideojuegosId() { return videojuegosId; }
        public void setVideojuegosId(Long videojuegosId) { this.videojuegosId = videojuegosId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}
