package com.diegolobos.plataforma.dto;

import com.diegolobos.plataforma.model.ResultadoSubmission;
import java.util.List;

/** Respuesta de POST /api/challenges/{id}/submit: resultado global + detalle por caso. */
public class SubmissionResultDTO {

    private Long submissionId;
    private ResultadoSubmission resultado;
    private List<ValidationCaseResultDTO> detalle;

    public SubmissionResultDTO() {
    }

    public SubmissionResultDTO(Long submissionId, ResultadoSubmission resultado, List<ValidationCaseResultDTO> detalle) {
        this.submissionId = submissionId;
        this.resultado = resultado;
        this.detalle = detalle;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public ResultadoSubmission getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoSubmission resultado) {
        this.resultado = resultado;
    }

    public List<ValidationCaseResultDTO> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<ValidationCaseResultDTO> detalle) {
        this.detalle = detalle;
    }
}
