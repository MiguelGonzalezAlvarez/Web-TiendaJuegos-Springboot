package com.gamehub.dto;

public class CuponRequest {
    private String codigo;

    public CuponRequest() {}

    public CuponRequest(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
