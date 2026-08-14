package com.diegolobos.plataforma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String codigoEnviado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultadoSubmission resultado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Submission() {
    }

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getCodigoEnviado() {
        return codigoEnviado;
    }

    public void setCodigoEnviado(String codigoEnviado) {
        this.codigoEnviado = codigoEnviado;
    }

    public ResultadoSubmission getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoSubmission resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
