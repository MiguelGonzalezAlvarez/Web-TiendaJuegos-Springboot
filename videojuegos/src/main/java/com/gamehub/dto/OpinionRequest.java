package com.gamehub.dto;

public class OpinionRequest {
    private Long videojuegosId;
    private Integer calificacion;
    private String titulo;
    private String contenido;

    public OpinionRequest() {}

    public OpinionRequest(Long videojuegosId, Integer calificacion, String titulo, String contenido) {
        this.videojuegosId = videojuegosId;
        this.calificacion = calificacion;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Long getVideojuegosId() { return videojuegosId; }
    public void setVideojuegosId(Long videojuegosId) { this.videojuegosId = videojuegosId; }
    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}
