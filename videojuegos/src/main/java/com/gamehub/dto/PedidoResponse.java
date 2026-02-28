package com.gamehub.dto;

public class PedidoResponse {
    private Long id;
    private String estado;
    private java.math.BigDecimal total;
    private java.util.List<DetallePedidoDto> detalles;
    private java.time.LocalDateTime fechaCreacion;

    public PedidoResponse() {}

    public PedidoResponse(Long id, String estado, java.math.BigDecimal total, 
                         java.util.List<DetallePedidoDto> detalles, java.time.LocalDateTime fechaCreacion) {
        this.id = id;
        this.estado = estado;
        this.total = total;
        this.detalles = detalles;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public java.math.BigDecimal getTotal() { return total; }
    public void setTotal(java.math.BigDecimal total) { this.total = total; }
    public java.util.List<DetallePedidoDto> getDetalles() { return detalles; }
    public void setDetalles(java.util.List<DetallePedidoDto> detalles) { this.detalles = detalles; }
    public java.time.LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public static class DetallePedidoDto {
        private Long id;
        private Long videojuegosId;
        private String titulo;
        private String imagenUrl;
        private Integer cantidad;
        private java.math.BigDecimal precioUnitario;
        private java.math.BigDecimal subtotal;

        public DetallePedidoDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getVideojuegosId() { return videojuegosId; }
        public void setVideojuegosId(Long videojuegosId) { this.videojuegosId = videojuegosId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public java.math.BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(java.math.BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        public java.math.BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(java.math.BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}
