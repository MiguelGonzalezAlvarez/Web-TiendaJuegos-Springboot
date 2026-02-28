package com.gamehub.dto;

import java.math.BigDecimal;

public class VideojuegoResponse {
    private Long id;
    private String titulo;
    private String slug;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioOferta;
    private String imagenUrl;
    private String plataforma;
    private String genero;
    private Double rating;
    private Integer stock;
    private Boolean destacado;
    private Boolean esOferta;

    public VideojuegoResponse() {}

    public VideojuegoResponse(Long id, String titulo, String slug, String descripcion,
                              BigDecimal precio, BigDecimal precioOferta, String imagenUrl,
                              String plataforma, String genero, Double rating, Integer stock,
                              Boolean destacado, Boolean esOferta) {
        this.id = id;
        this.titulo = titulo;
        this.slug = slug;
        this.descripcion = descripcion;
        this.precio = precio;
        this.precioOferta = precioOferta;
        this.imagenUrl = imagenUrl;
        this.plataforma = plataforma;
        this.genero = genero;
        this.rating = rating;
        this.stock = stock;
        this.destacado = destacado;
        this.esOferta = esOferta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public BigDecimal getPrecioOferta() { return precioOferta; }
    public void setPrecioOferta(BigDecimal precioOferta) { this.precioOferta = precioOferta; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Boolean getDestacado() { return destacado; }
    public void setDestacado(Boolean destacado) { this.destacado = destacado; }
    public Boolean getEsOferta() { return esOferta; }
    public void setEsOferta(Boolean esOferta) { this.esOferta = esOferta; }
}
