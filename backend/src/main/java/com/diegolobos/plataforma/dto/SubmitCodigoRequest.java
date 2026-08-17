package com.diegolobos.plataforma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body de POST /api/challenges/{id}/submit. */
public class SubmitCodigoRequest {

    @NotBlank
    @Size(max = 20000, message = "El codigo no puede superar los 20000 caracteres")
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
