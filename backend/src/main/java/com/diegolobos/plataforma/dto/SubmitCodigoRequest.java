package com.diegolobos.plataforma.dto;

import jakarta.validation.constraints.NotBlank;

/** Body de POST /api/challenges/{id}/submit. */
public class SubmitCodigoRequest {

    @NotBlank
    private String codigo;

    public SubmitCodigoRequest() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
